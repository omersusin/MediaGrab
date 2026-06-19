package media.grab.os.ui.downloads

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import media.grab.os.data.model.Download
import media.grab.os.data.repository.DownloadRepository
import media.grab.os.ds.components.AppCard
import media.grab.os.ds.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(navController: NavHostController? = null) {
    val repo = remember { DownloadRepository.getInstance() }
    val downloads by repo.downloads.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("İndirmeler") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                singleLine = true
            )
            val filtered = if (query.isBlank()) downloads else downloads.filter {
                it.title.contains(query, ignoreCase = true) || it.url.contains(query, ignoreCase = true)
            }
            if (filtered.isEmpty()) {
                EmptyState("Sonuç yok", query.isBlank().let { if (it) "Henüz indirme yok" else "" })
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered) { download ->
                        AppCard {
                            ListItem(
                                headlineContent = { Text(download.title) },
                                supportingContent = {
                                    Text("${download.platform.displayName} - ${download.status.displayName}")
                                },
                                leadingContent = { Icon(Icons.Default.Image, contentDescription = null) },
                                trailingContent = {
                                    IconButton(onClick = { repo.removeDownload(download.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
