package media.grab.os

import android.content.Intent
import android.os.Build
import android.os.Bundle
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
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return
            if (sharedText.isNotBlank()) {
                lifecycleScope.launch {
                    try {
                        val downloadUrl = resolveDownloadUrl(sharedText)
                        if (downloadUrl != null) {
                            val serviceIntent = Intent(this@MainActivity, DownloadService::class.java).apply {
                                action = DownloadService.ACTION_DOWNLOAD
                                putExtra(DownloadService.EXTRA_URL, downloadUrl)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                // Android 12+ requires explicit foreground service start
                                try {
                                    startForegroundService(serviceIntent)
                                } catch (e: android.app.ForegroundServiceStartNotAllowedException) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "App is in background, cannot start download",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }
                            } else {
                                startService(serviceIntent)
                            }
                            Toast.makeText(
                                this@MainActivity,
                                "Download started",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Could not extract media from this link",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@MainActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private suspend fun resolveDownloadUrl(sharedText: String): String? {
        if (sharedText.contains("instagram.com")) {
            return InstagramExtractor.extractMediaUrl(sharedText)
        }
        // Diğer platformlar için yer tutucular
        if (sharedText.contains("tiktok.com") || sharedText.contains("vm.tiktok")) {
            return null
        }
        if (sharedText.contains("twitter.com") || sharedText.contains("x.com")) {
            return null
        }
        if (sharedText.contains("facebook.com") || sharedText.contains("fb.watch")) {
            return null
        }
        return if (sharedText.startsWith("http")) sharedText else null
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
