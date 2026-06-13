package media.grab.os.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import media.grab.os.MainActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class DownloadService : Service() {

    companion object {
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_DOWNLOAD = "media.grab.os.action.DOWNLOAD"
        const val EXTRA_URL = "media.grab.os.extra.URL"
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val client = OkHttpClient()

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            CHANNEL_ID,
            "MediaGrab Downloads",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_DOWNLOAD) {
            val url = intent.getStringExtra(EXTRA_URL) ?: return START_NOT_STICKY
            val notification = buildNotification(0, "Starting download...")
            startForeground(NOTIFICATION_ID, notification)
            serviceScope.launch {
                downloadFile(url)
            }
        } else {
            val pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MediaGrab")
                .setContentText("Download service is running")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()
            startForeground(NOTIFICATION_ID, notification)
        }
        return START_NOT_STICKY
    }

    private suspend fun downloadFile(url: String) {
        val fileName = "MediaGrab_${System.currentTimeMillis()}.mp4"
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body ?: throw IOException("Empty response body")
            val totalBytes = body.contentLength()
            var downloadedBytes = 0L

            // Build MediaStore entry
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "video/mp4")
                put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/MediaGrab")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
            }
            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Failed to create MediaStore entry")

            contentResolver.openOutputStream(uri)?.use { outputStream ->
                body.byteStream().use { inputStream ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        val progress = if (totalBytes > 0) ((downloadedBytes * 100) / totalBytes).toInt() else 0
                        updateNotification(progress, "Downloading... $progress%")
                    }
                }
            }

            // Finalize MediaStore entry
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                contentResolver.update(uri, values, null, null)
            }

            updateNotification(100, "Download complete")
            delay(2000)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } catch (e: Exception) {
            updateNotification(0, "Download failed: ${e.message}")
            delay(3000)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun buildNotification(progress: Int, text: String): android.app.Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MediaGrab Download")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentIntent(pendingIntent)
            .setProgress(100, progress, false)
            .setOngoing(progress < 100)
            .build()
    }

    private fun updateNotification(progress: Int, text: String) {
        val notification = buildNotification(progress, text)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    override fun onTimeout(startId: Int, fgsType: Int) {
        serviceScope.cancel()
        stopSelf()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
