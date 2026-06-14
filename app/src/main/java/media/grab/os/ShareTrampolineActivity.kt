package media.grab.os

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import media.grab.core.extractor.InstagramExtractor
import media.grab.os.service.DownloadService
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

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
            setMessage("Extracting media link...")
            setCancelable(false)
            show()
        }

        lifecycleScope.launch {
            try {
                val downloadUrl = resolveDownloadUrl(sharedText)
                progressDialog.dismiss()
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
                    Toast.makeText(this@ShareTrampolineActivity, "Download started", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ShareTrampolineActivity, "Could not extract media from this link", Toast.LENGTH_LONG).show()
                    logError("Extraction failed for: $sharedText", null)
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(this@ShareTrampolineActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                logError("Trampoline crash", e)
            } finally {
                finish()
            }
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

    private fun logError(message: String, throwable: Throwable?) {
        try {
            val sw = StringWriter()
            if (throwable != null) {
                throwable.printStackTrace(PrintWriter(sw))
            }
            val log = "$message\n${sw}\n\n"
            val dir = File(getExternalFilesDir(null), "logs")
            if (!dir.exists()) dir.mkdirs()
            File(dir, "error_log.txt").appendText(log)
        } catch (_: Exception) { }
    }
}
