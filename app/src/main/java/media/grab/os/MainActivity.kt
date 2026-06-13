package media.grab.os

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import media.grab.core.extractor.InstagramExtractor
import media.grab.feature.dashboard.DashboardScreen
import media.grab.os.service.DownloadService
import media.grab.os.service.MediaAccessibilityService
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingShareIntent(intent)
        setContent {
            MaterialTheme(colorScheme = dynamicLightColorScheme(this)) {
                DashboardScreen(
                    onEnableAccessibility = {
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    },
                    accessibilityEnabled = isAccessibilityEnabled()
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingShareIntent(intent)
    }

    private fun handleIncomingShareIntent(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND) return
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText.isNullOrBlank()) return

        lifecycleScope.launch {
            try {
                val downloadUrl = resolveDownloadUrl(sharedText)
                if (downloadUrl != null) {
                    val serviceIntent = Intent(this@MainActivity, DownloadService::class.java).apply {
                        action = DownloadService.ACTION_DOWNLOAD
                        putExtra(DownloadService.EXTRA_URL, downloadUrl)
                    }
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(serviceIntent)
                        } else {
                            startService(serviceIntent)
                        }
                        Toast.makeText(this@MainActivity, "Download started", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        logError("Failed to start download service", e)
                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Could not extract media from this link", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                logError("Unhandled error in share handling", e)
                Toast.makeText(this@MainActivity, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun resolveDownloadUrl(sharedText: String): String? {
        return try {
            when {
                sharedText.contains("instagram.com") -> InstagramExtractor.extractMediaUrl(sharedText)
                sharedText.contains("tiktok.com") || sharedText.contains("vm.tiktok") -> null
                sharedText.contains("twitter.com") || sharedText.contains("x.com") -> null
                sharedText.contains("facebook.com") || sharedText.contains("fb.watch") -> null
                sharedText.startsWith("http") -> sharedText
                else -> null
            }
        } catch (e: Exception) {
            logError("Extractor failed for: $sharedText", e)
            null
        }
    }

    private fun logError(message: String, throwable: Throwable) {
        try {
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            val log = "${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())} - $message\n${sw}\n\n"
            val dir = File(Environment.getExternalStorageDirectory(), "MediaGrab")
            if (!dir.exists()) dir.mkdirs()
            File(dir, "error_log.txt").appendText(log)
        } catch (_: Exception) { }
    }

    private fun isAccessibilityEnabled(): Boolean {
        val serviceName = "$packageName/${MediaAccessibilityService::class.java.name}"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(serviceName) == true
    }
}
