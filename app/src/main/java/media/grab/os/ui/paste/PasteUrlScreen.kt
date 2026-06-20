package media.grab.os.ui.paste

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import media.grab.os.extractor.ExtractorRegistry
import media.grab.os.extractor.DownloadEngine
import media.grab.os.ui.theme.ErrorText

sealed class DownloadResult {
    object Idle : DownloadResult()
    object Loading : DownloadResult()
    data class Success(val fileName: String) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasteUrlScreen(navController: NavHostController? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var url by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<DownloadResult>(DownloadResult.Idle) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("URL Yapıştır") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.ContentPaste,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = url,
                onValueChange = { url = it; if (result is DownloadResult.Error) result = DownloadResult.Idle },
                label = { Text("Instagram, TikTok, X, Reddit URL") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = result is DownloadResult.Error
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { readClipboard(context)?.let { url = it } }) {
                Text("Panodan Yapıştır")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (url.isBlank()) return@Button
                    result = DownloadResult.Loading
                    scope.launch {
                        val infoResult = ExtractorRegistry.extract(url)
                        val info = infoResult.getOrNull()
                        if (info == null) {
                            result = DownloadResult.Error(
                                infoResult.exceptionOrNull()?.message ?: "URL çözümlenemedi"
                            )
                            return@launch
                        }
                        val saveResult = DownloadEngine.downloadAndSave(context, info)
                        result = saveResult.fold(
                            onSuccess = { DownloadResult.Success(info.fileName) },
                            onFailure = { DownloadResult.Error(it.message ?: "İndirme başarısız") }
                        )
                    }
                },
                enabled = url.isNotBlank() && result !is DownloadResult.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (result is DownloadResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("İndiriliyor...")
                } else {
                    Text("İndirmeyi Başlat")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val r = result) {
                is DownloadResult.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("İndirme tamamlandı!", style = MaterialTheme.typography.titleSmall)
                                Text(r.fileName, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                is DownloadResult.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Hata", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    r.message,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Help text
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Desteklenen platformlar:", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "• Reddit (en iyi çalışır)\n• TikTok (public)\n• Twitter/X (public)\n• Instagram (public)\n• Diğer (og:video/image olan siteler)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun readClipboard(context: Context): String? {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = cm?.primaryClip ?: return null
    if (clip.itemCount == 0) return null
    return clip.getItemAt(0).text?.toString()
}
