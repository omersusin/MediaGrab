package media.grab.os.share

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import media.grab.os.data.model.Download
import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.data.repository.DownloadRepository
import media.grab.os.extractor.ExtractorRegistry
import media.grab.os.extractor.DownloadEngine
import media.grab.os.ui.theme.MediaGrabTheme

class ShareReceiverActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedUrl = extractUrl(intent)
        setContent {
            MediaGrabTheme {
                ShareReceiverScreen(
                    url = sharedUrl,
                    onDownload = { url -> startDownload(url) }
                )
            }
        }
    }

    private fun extractUrl(intent: Intent?): String? {
        if (intent?.action != Intent.ACTION_SEND) return null
        return intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
            Regex("https?://[\\S]+").find(text)?.value
        }
    }

    private fun startDownload(url: String) {
        val scope = kotlinx.coroutines.MainScope()
        scope.launch {
            val info = ExtractorRegistry.extract(url).getOrNull() ?: return@launch
            DownloadEngine.downloadAndSave(applicationContext, info)
        }
    }
}

@Composable
fun ShareReceiverScreen(url: String?, onDownload: (String) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("MediaGrab", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            if (url != null) {
                Text("URL bulundu:", style = MaterialTheme.typography.bodyLarge)
                Text(url, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { onDownload(url) }) {
                    Text("İndirmeyi Başlat")
                }
            } else {
                Text("Geçerli URL bulunamadı", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
