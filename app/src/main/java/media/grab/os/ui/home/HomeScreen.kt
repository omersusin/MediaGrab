package media.grab.os.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import media.grab.os.data.model.Download
import media.grab.os.data.model.DownloadStatus
import media.grab.os.data.repository.DownloadRepository
import media.grab.os.ds.components.AppCard
import media.grab.os.ds.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController? = null) {
    val repo = remember { DownloadRepository.getInstance() }
    val downloads by repo.downloads.collectAsState()
    val active = downloads.filter { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.QUEUED }
    val recent = downloads.filter { it.status == DownloadStatus.COMPLETED }.take(5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MediaGrab") },
                actions = {
                    IconButton(onClick = { navController?.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item {
                AppCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Hoş Geldin!", style = MaterialTheme.typography.headlineSmall)
                        Text("Sosyal medyadan medya indir", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (active.isNotEmpty()) {
                item {
                    Text(
                        "Aktif İndirmeler",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                    )
                }
                items(active) { download ->
                    AppCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(download.title, style = MaterialTheme.typography.titleSmall)
                            Text(download.platform.displayName, style = MaterialTheme.typography.bodySmall)
                            LinearProgressIndicator(progress = { download.progress }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                        }
                    }
                }
            }

            item {
                Text(
                    "Son İndirmeler",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                )
            }
            if (recent.isEmpty()) {
                item { EmptyState("Henüz indirme yok", "URL yapıştırarak başla") }
            } else {
                items(recent) { download ->
                    AppCard {
                        ListItem(
                            headlineContent = { Text(download.title) },
                            supportingContent = { Text(download.platform.displayName) },
                            leadingContent = { Icon(Icons.Default.Image, contentDescription = null) },
                            trailingContent = { Icon(Icons.Default.Check, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }
}

