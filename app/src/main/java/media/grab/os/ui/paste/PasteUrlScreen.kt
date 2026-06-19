package media.grab.os.ui.paste

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import media.grab.os.data.model.Download
import media.grab.os.data.model.MediaType
import media.grab.os.data.model.Platform
import media.grab.os.data.repository.DownloadRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasteUrlScreen(navController: NavHostController? = null) {
    val repo = remember { DownloadRepository.getInstance() }
    var url by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("URL Yapıştır") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                onValueChange = { url = it },
                label = { Text("Instagram, TikTok, X URL") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (url.isNotBlank()) {
                        val platform = Platform.fromUrl(url)
                        val download = Download(
                            url = url,
                            title = "Yeni indirme: ${platform.displayName}",
                            fileName = url.substringAfterLast("/").take(50),
                            platform = platform,
                            mediaType = MediaType.UNKNOWN
                        )
                        repo.addDownload(download)
                        navController?.popBackStack()
                    }
                },
                enabled = url.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("İndirmeyi Başlat")
            }
        }
    }
}
