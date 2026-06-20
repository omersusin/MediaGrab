package media.grab.os.download

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import media.grab.os.MediaGrabApp
import media.grab.os.data.model.Download
import media.grab.os.data.model.DownloadStatus
import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.data.repository.DownloadRepository
import media.grab.os.extractor.ExtractorRegistry
import media.grab.os.network.HttpClient
import media.grab.os.notif.NotificationHelper
import okhttp3.Request
import java.io.File
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

/**
 * Foreground service that performs downloads. Strategy:
 *  1) Try the yt-dlp engine (1000+ sites, real video/audio merging).
 *  2) On failure, fall back to the meta-scraper + direct OkHttp download (good for image posts).
 */
class DownloadService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val active = AtomicInteger(0)
    private lateinit var repo: DownloadRepository

    override fun onCreate() {
        super.onCreate()
        repo = (application as MediaGrabApp).container.downloadRepository
        updateForeground("Starting...", 0)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra(EXTRA_URL)?.trim()
        val audioOnly = intent?.getBooleanExtra(EXTRA_AUDIO_ONLY, false) ?: false
        val retryId = intent?.getStringExtra(EXTRA_RETRY_ID)
        if (url.isNullOrBlank()) {
            stopIfIdle()
            return START_NOT_STICKY
        }
        active.incrementAndGet()
        scope.launch { runDownload(url, audioOnly, retryId) }
        return START_NOT_STICKY
    }

    private fun runDownload(url: String, audioOnly: Boolean, retryId: String?) {
        val platform = Platform.fromUrl(url)
        val id = retryId ?: UUID.randomUUID().toString()
        val notifId = id.hashCode() and 0xFFFF

        var item = repo.get(id)?.copy(
            status = DownloadStatus.EXTRACTING, progress = 0, errorMessage = null
        ) ?: Download(
            id = id, url = url, platform = platform,
            status = DownloadStatus.EXTRACTING,
            mediaType = if (audioOnly) MediaType.AUDIO else MediaType.UNKNOWN
        )
        repo.upsert(item)
        updateForeground(item.title.ifBlank { platform.displayName }, 0)

        // --- 1) yt-dlp engine ---
        val ytOk = runCatching {
            item = item.copy(status = DownloadStatus.DOWNLOADING)
            repo.upsert(item)
            val res = YtDlpEngine.download(
                context = this,
                url = url,
                parentDir = cacheDir,
                audioOnly = audioOnly,
                processId = id
            ) { p ->
                if (p % 2 == 0) {
                    item = item.copy(progress = p)
                    repo.upsert(item)
                    updateForeground(item.title.ifBlank { platform.displayName }, p)
                }
            }
            val type = if (audioOnly) MediaType.AUDIO else guessType(res.file.extension)
            val savedUri = FileSaver.saveFile(this, res.file, res.title.ifBlank { defaultName(platform) }, type)
            res.file.parentFile?.deleteRecursively()
            finishSuccess(item.copy(
                title = res.title, mediaType = type,
                fileName = res.file.name
            ), savedUri, notifId)
            true
        }.getOrElse {
            android.util.Log.w("DownloadService", "yt-dlp path failed: ${it.message}")
            false
        }

        if (!ytOk) {
            // --- 2) meta-scraper + OkHttp fallback ---
            runCatching {
                item = item.copy(status = DownloadStatus.EXTRACTING)
                repo.upsert(item)
                val media = ExtractorRegistry.extract(url).first()
                item = item.copy(
                    status = DownloadStatus.DOWNLOADING,
                    title = media.title.ifBlank { item.title },
                    mediaType = media.mediaType,
                    thumbnailUrl = media.thumbnailUrl
                )
                repo.upsert(item)
                val temp = downloadToTemp(media.downloadUrl, media.suggestedExtension ?: extOf(media.mediaType)) { p ->
                    item = item.copy(progress = p)
                    repo.upsert(item)
                    updateForeground(item.title.ifBlank { platform.displayName }, p)
                }
                val saved = FileSaver.saveFile(this, temp,
                    item.title.ifBlank { defaultName(platform) }, media.mediaType)
                temp.delete()
                finishSuccess(item.copy(fileName = temp.name), saved, notifId)
            }.onFailure { err ->
                finishFailure(item, err.message ?: "Download failed.", notifId)
            }
        }
        stopIfIdle()
    }

    private fun downloadToTemp(url: String, ext: String, onProgress: (Int) -> Unit): File {
        val req: Request = HttpClient.request(url, mobile = true)
        val temp = File.createTempFile("dl_", ".$ext", cacheDir)
        HttpClient.client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) throw IllegalStateException("HTTP ${resp.code}")
            val body = resp.body ?: throw IllegalStateException("Empty body")
            val total = body.contentLength()
            body.byteStream().use { input ->
                temp.outputStream().use { output ->
                    val buf = ByteArray(64 * 1024)
                    var read: Int
                    var done = 0L
                    var lastPct = -1
                    while (input.read(buf).also { read = it } != -1) {
                        output.write(buf, 0, read)
                        done += read
                        if (total > 0) {
                            val pct = ((done * 100) / total).toInt()
                            if (pct != lastPct) { lastPct = pct; onProgress(pct) }
                        }
                    }
                }
            }
        }
        return temp
    }

    private fun finishSuccess(base: Download, uri: String, notifId: Int) {
        val done = base.copy(
            status = DownloadStatus.COMPLETED, progress = 100,
            localUri = uri, completedAt = System.currentTimeMillis(), errorMessage = null
        )
        repo.upsert(done)
        NotificationHelper.notifyDone(this, notifId, done.title.ifBlank { done.platform.displayName },
            true, "Saved to Download/${FileSaver.SUBDIR}/")
    }

    private fun finishFailure(base: Download, message: String, notifId: Int) {
        val failed = base.copy(status = DownloadStatus.FAILED, errorMessage = message)
        repo.upsert(failed)
        NotificationHelper.notifyDone(this, notifId, failed.title.ifBlank { failed.platform.displayName },
            false, message)
    }

    private fun updateForeground(title: String, progress: Int) {
        runCatching {
            val n = NotificationHelper.progressNotification(this, title, progress)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NotificationHelper.FOREGROUND_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            } else {
                startForeground(NotificationHelper.FOREGROUND_ID, n)
            }
        }
    }

    private fun stopIfIdle() {
        if (active.decrementAndGet() <= 0) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun guessType(ext: String): MediaType = when (ext.lowercase()) {
        "mp4", "webm", "mkv", "mov", "m4v" -> MediaType.VIDEO
        "mp3", "m4a", "aac", "wav", "ogg", "opus" -> MediaType.AUDIO
        "jpg", "jpeg", "png", "gif", "webp" -> MediaType.IMAGE
        else -> MediaType.VIDEO
    }

    private fun extOf(type: MediaType) = when (type) {
        MediaType.VIDEO -> "mp4"; MediaType.AUDIO -> "m4a"; MediaType.IMAGE -> "jpg"; MediaType.UNKNOWN -> "bin"
    }

    private fun defaultName(platform: Platform) =
        "${platform.displayName.lowercase().replace(Regex("[^a-z0-9]"), "")}_${System.currentTimeMillis()}"

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val EXTRA_URL = "extra_url"
        const val EXTRA_AUDIO_ONLY = "extra_audio_only"
        const val EXTRA_RETRY_ID = "extra_retry_id"

        fun start(context: Context, url: String, audioOnly: Boolean = false, retryId: String? = null) {
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_AUDIO_ONLY, audioOnly)
                if (retryId != null) putExtra(EXTRA_RETRY_ID, retryId)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
