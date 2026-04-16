package com.media.grab.service

import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import com.media.grab.MediaGrabApplication
import com.media.grab.R
import java.io.FileInputStream
import java.io.FileOutputStream

class GrabberVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startVpn()
        return START_STICKY
    }

    private fun startVpn() {
        val builder = Builder()
            .setSession("MediaGrab Grabber")
            .addAddress("10.0.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("8.8.8.8")
            .setMtu(1500)
            .setBlocking(true)

        vpnInterface = builder.establish()

        // Start packet processing in background thread
        Thread { processPackets() }.start()
    }

    private fun processPackets() {
        val vpnInterface = this.vpnInterface ?: return
        val input = FileInputStream(vpnInterface.fileDescriptor)
        val output = FileOutputStream(vpnInterface.fileDescriptor)
        val buffer = ByteArray(32767)

        try {
            while (vpnInterface != null) {
                val length = input.read(buffer)
                if (length > 0) {
                    // Analyze packet for media URLs
                    analyzePacket(buffer, length)
                    // Forward packet
                    output.write(buffer, 0, length)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun analyzePacket(buffer: ByteArray, length: Int) {
        // Extract HTTP headers and look for media URLs
        try {
            val data = String(buffer, 0, length, Charsets.UTF_8)
            if (data.contains("Host:") && data.contains("video|media|download|stream".toRegex())) {
                // Extract URLs from HTTP headers
                val hostRegex = Regex("Host: ([^\r\n]+)")
                val host = hostRegex.find(data)?.groupValues?.get(1)

                if (host != null) {
                    // Notify about detected media traffic
                    notifyMediaDetected(host)
                }
            }
        } catch (e: Exception) {
            // Ignore parsing errors
        }
    }

    private fun notifyMediaDetected(host: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(SNIFF_NOTIFICATION_ID, createSniffNotification(host))
    }

    private fun createNotification(): Notification {
        return Notification.Builder(this, MediaGrabApplication.CH_GRABBER)
            .setContentTitle("MediaGrab")
            .setContentText("Grabber is running")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()
    }

    private fun createSniffNotification(host: String): Notification {
        return Notification.Builder(this, MediaGrabApplication.CH_GRABBER)
            .setContentTitle("Media Detected")
            .setContentText("Traffic from $host")
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .build()
    }

    override fun onDestroy() {
        vpnInterface?.close()
        vpnInterface = null
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onRevoke() {
        stopSelf()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val SNIFF_NOTIFICATION_ID = 1002
    }
}
