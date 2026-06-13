package media.grab.os

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import media.grab.core.extractor.InstagramExtractor
import media.grab.os.service.DownloadService

class ShareTrampolineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND) {
            finish()
            return
        }
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText.isNullOrBlank()) {
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val downloadUrl = resolveDownloadUrl(sharedText)
                if (downloadUrl != null) {
                    val serviceIntent = Intent(this@ShareTrampolineActivity, DownloadService::class.java).apply {
                        action = DownloadService.ACTION_DOWNLOAD
                        putExtra(DownloadService.EXTRA_URL, downloadUrl)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(serviceIntent)
                    } else {
                        startService(serviceIntent)
                    }
                }
            } catch (_: Exception) { }
            finish()
        }
    }

    private suspend fun resolveDownloadUrl(sharedText: String): String? {
        return when {
            sharedText.contains("instagram.com") -> InstagramExtractor.extractMediaUrl(sharedText)
            sharedText.contains("tiktok.com") || sharedText.contains("vm.tiktok") -> null
            sharedText.contains("twitter.com") || sharedText.contains("x.com") -> null
            sharedText.contains("facebook.com") || sharedText.contains("fb.watch") -> null
            sharedText.startsWith("http") -> sharedText
            else -> null
        }
    }
}
