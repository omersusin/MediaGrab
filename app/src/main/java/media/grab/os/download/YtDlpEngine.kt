package media.grab.os.download

import android.content.Context
import android.util.Log
import com.yausername.aria2c.Aria2c
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.io.File

/**
 * Wraps yt-dlp (via youtubedl-android). Supports 1000+ sites, merges best video+audio
 * with ffmpeg, and downloads straight to a temp dir that we later move into MediaStore.
 */
object YtDlpEngine {

    private const val TAG = "YtDlpEngine"

    @Volatile private var initialized = false
    @Volatile var lastInitError: String? = null
        private set

    fun ensureInit(context: Context): Boolean {
        if (initialized) return true
        synchronized(this) {
            if (initialized) return true
            return try {
                YoutubeDL.getInstance().init(context.applicationContext)
                FFmpeg.getInstance().init(context.applicationContext)
                Aria2c.getInstance().init(context.applicationContext)
                initialized = true
                lastInitError = null
                true
            } catch (t: Throwable) {
                lastInitError = t.message
                Log.e(TAG, "yt-dlp init failed", t)
                false
            }
        }
    }

    data class YtResult(val file: File, val title: String)

    /**
     * Downloads [url] into a fresh subfolder of [parentDir].
     * @param audioOnly extract bestaudio as m4a.
     * @throws Exception on failure (caller falls back to the meta scraper).
     */
    fun download(
        context: Context,
        url: String,
        parentDir: File,
        audioOnly: Boolean,
        processId: String,
        onProgress: (Int) -> Unit
    ): YtResult {
        if (!ensureInit(context)) {
            throw IllegalStateException("yt-dlp not available: ${lastInitError ?: "init failed"}")
        }
        val workDir = File(parentDir, "yt_$processId").apply { mkdirs() }
        val request = YoutubeDLRequest(url).apply {
            addOption("-o", File(workDir, "%(title).100s.%(ext)s").absolutePath)
            addOption("--no-playlist")
            addOption("--no-mtime")
            addOption("--restrict-filenames")
            addOption("--no-warnings")
            if (audioOnly) {
                addOption("-x")
                addOption("--audio-format", "m4a")
                addOption("-f", "bestaudio/best")
            } else {
                addOption("-f", "bv*+ba/b")
                addOption("--merge-output-format", "mp4")
            }
        }

        YoutubeDL.getInstance().execute(request, processId) { progress, _, _ ->
            if (progress >= 0f) onProgress(progress.toInt().coerceIn(0, 100))
        }

        val produced = workDir.listFiles()
            ?.filter { it.isFile && it.length() > 0 }
            ?.maxByOrNull { it.lastModified() }
            ?: throw IllegalStateException("yt-dlp produced no file.")

        val title = produced.nameWithoutExtension.replace('_', ' ').trim()
        return YtResult(produced, title)
    }

    fun cancel(processId: String) {
        runCatching { YoutubeDL.getInstance().destroyProcessById(processId) }
    }
}
