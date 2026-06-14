package media.grab.os

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import media.grab.core.extractor.InstagramExtractor
import media.grab.feature.dashboard.LogStore
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
            Toast.makeText(this, "No link received", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val progressDialog = ProgressDialog(this).apply {
            setMessage("Resolving media...")
            setCancelable(false)
            show()
        }

        lifecycleScope.launch {
            try {
                val downloadUrl = resolveDownloadUrl(sharedText)
                progressDialog.dismiss()
                if (downloadUrl != null) {
                    LogStore.add("✓ Resolved: $downloadUrl")
                    val serviceIntent = Intent(this@ShareTrampolineActivity, DownloadService::class.java).apply {
                        action = DownloadService.ACTION_DOWNLOAD
                        putExtra(DownloadService.EXTRA_URL, downloadUrl)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(serviceIntent)
                    } else {
                        startService(serviceIntent)
                    }
                    Toast.makeText(this@ShareTrampolineActivity, "Download started", Toast.LENGTH_SHORT).show()
                } else {
                    LogStore.add("✗ Failed to resolve: $sharedText")
                    Toast.makeText(this@ShareTrampolineActivity, "Could not extract media", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                LogStore.add("✗ Crash: ${e.message}")
                Toast.makeText(this@ShareTrampolineActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                finish()
            }
        }
    }

    private suspend fun resolveDownloadUrl(sharedText: String): String? = withContext(Dispatchers.IO) {
        // 1) Native extractor (Instagram)
        if (sharedText.contains("instagram.com")) {
            val result = InstagramExtractor.extractMediaUrl(sharedText)
            if (result != null) return@withContext result
            LogStore.add("→ Instagram native failed, trying yt-dlp fallback...")
        }
        // 2) yt-dlp fallback (any URL)
        return@withContext try {
            YtDlpFallback.resolve(sharedText)
        } catch (e: Exception) {
            LogStore.add("✗ yt-dlp error: ${e.message}")
            null
        }
    }
}
