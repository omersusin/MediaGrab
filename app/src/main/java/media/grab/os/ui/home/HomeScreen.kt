package media.grab.os.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import media.grab.os.data.model.DownloadStatus
import media.grab.os.data.repository.DownloadRepository
import media.grab.os.ds.components.AppCard
import media.grab.os.ds.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController? = null) {
    val repo = remember { DownloadRepository.getInstance() }
    val downloads by repo.downloads.collectAsState()
    val active = downloads.filter {
        it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.QUEUED
    }
    val recent = downloads.filter { it.status == DownloadStatus.COMPLETED }.take(5)
    val totalCount = downloads.size
    val completedCount = downloads.count { it.status == DownloadStatus.COMPLETED }

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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController?.navigate("paste") },
                icon = { Icon(Icons.Default.ContentPaste, contentDescription = null) },
                text = { Text("URL") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                AppCard {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Hoş Geldin!", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "Sosyal medyadan medya indir",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row {
                            StatChip("Toplam", totalCount.toString())
                            Spacer(modifier = Modifier.width(8.dp))
                            StatChip("Tamamlanan", completedCount.toString(), highlight = true)
                        }
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
                items(active, key = { it.id }) { download ->
                    AppCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(download.title, style = MaterialTheme.typography.titleSmall)
                            Text(
                                download.platform.displayName,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { download.progress },
                                modifier = Modifier.fillMaxWidth()
                            )
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
                item {
                    AppCard {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Henüz indirme yok", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Sağ alttaki + butonuyla URL yapıştır",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                items(recent, key = { it.id }) { download ->
                    AppCard {
                        ListItem(
                            headlineContent = { Text(download.title) },
                            supportingContent = { Text(download.platform.displayName) },
                            leadingContent = {
                                Icon(Icons.Default.Image, contentDescription = null)
                            },
                            trailingContent = {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, highlight: Boolean = false) {
    val containerColor = if (highlight) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (highlight) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = contentColor)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleSmall,
                color = contentColor
            )
        }
    }
}
