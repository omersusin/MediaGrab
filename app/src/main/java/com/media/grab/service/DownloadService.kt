package com.media.grab.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.media.grab.MediaGrabApplication
import com.media.grab.R
import com.media.grab.data.local.entity.DownloadEntity
import com.media.grab.data.local.entity.DownloadStatus
import com.media.grab.data.repository.DownloadRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : Service() {

    @Inject lateinit var downloadRepository: DownloadRepository
    @Inject lateinit var okHttpClient: OkHttpClient

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val activeJobs = mutableMapOf<String, Job>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val downloadId = intent.getStringExtra(EXTRA_DOWNLOAD_ID) ?: return START_NOT_STICKY
                startDownload(downloadId)
            }
            ACTION_STOP -> {
                val downloadId = intent.getStringExtra(EXTRA_DOWNLOAD_ID)
                if (downloadId != null) {
                    cancelDownload(downloadId)
                } else {
                    stopAllDownloads()
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun startDownload(downloadId: String) {
        val job = serviceScope.launch {
            try {
                val download = downloadRepository.getDownload(downloadId) ?: return@launch
                downloadRepository.updateStatus(downloadId, DownloadStatus.DOWNLOADING)

                startForeground(downloadId.hashCode(), createNotification(download, 0))

                downloadFile(download)
            } catch (e: Exception) {
                downloadRepository.updateStatus(downloadId, DownloadStatus.FAILED)
            }
        }
        activeJobs[downloadId] = job
    }

    private suspend fun downloadFile(download: DownloadEntity) {
        val request = Request.Builder().url(download.url).build()
        val response = okHttpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            downloadRepository.updateStatus(download.id, DownloadStatus.FAILED)
            return
        }

        val body = response.body ?: return
        val totalSize = body.contentLength()
        val downloadDir = File(filesDir, "downloads")
        if (!downloadDir.exists()) downloadDir.mkdirs()

        val extension = getExtension(download.url, response.header("Content-Type"))
        val file = File(downloadDir, "${download.id}.$extension")

        body.byteStream().use { input ->
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(8192)
                var downloaded = 0L
                var lastUpdate = 0L

                while (true) {
                    val bytes = input.read(buffer)
                    if (bytes == -1) break

                    output.write(buffer, 0, bytes)
                    downloaded += bytes

                    val now = System.currentTimeMillis()
                    if (now - lastUpdate > 500) {
                        downloadRepository.updateProgress(download.id, downloaded)
                        val progress = if (totalSize > 0) ((downloaded * 100) / totalSize).toInt() else 0
                        updateNotification(download, progress)
                        lastUpdate = now
                    }
                }
            }
        }

        downloadRepository.markComplete(download.id, file.absolutePath)
        updateNotification(download.copy(filePath = file.absolutePath), 100)
    }

    private fun cancelDownload(downloadId: String) {
        activeJobs[downloadId]?.cancel()
        activeJobs.remove(downloadId)
        serviceScope.launch {
            downloadRepository.updateStatus(downloadId, DownloadStatus.CANCELLED)
        }
    }

    private fun stopAllDownloads() {
        activeJobs.values.forEach { it.cancel() }
        activeJobs.clear()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(download: DownloadEntity, progress: Int): android.app.Notification {
        return NotificationCompat.Builder(this, MediaGrabApplication.CH_DOWNLOAD)
            .setContentTitle(download.title)
            .setContentText("$progress%")
            .setSmallIcon(R.drawable.ic_download)
            .setProgress(100, progress, progress == 0)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(download: DownloadEntity, progress: Int) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(download.id.hashCode(), createNotification(download, progress))
    }

    private fun getExtension(url: String, contentType: String?): String {
        val fromUrl = url.substringAfterLast(".", "").substringBefore("?")
        if (fromUrl.length in 2..5) return fromUrl

        return when (contentType) {
            "video/mp4" -> "mp4"
            "video/webm" -> "webm"
            "video/x-matroska" -> "mkv"
            "audio/mpeg" -> "mp3"
            "audio/mp4" -> "m4a"
            else -> "mp4"
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.media.grab.action.START"
        const val ACTION_STOP = "com.media.grab.action.STOP"
        const val EXTRA_DOWNLOAD_ID = "download_id"

        fun start(context: Context, downloadId: String) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_DOWNLOAD_ID, downloadId)
            }
            context.startService(intent)
        }

        fun stop(context: Context, downloadId: String? = null) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_STOP
                downloadId?.let { putExtra(EXTRA_DOWNLOAD_ID, it) }
            }
            context.startService(intent)
        }
    }
}
