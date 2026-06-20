package media.grab.os.ui.downloads

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import media.grab.os.data.model.Download
import media.grab.os.data.model.DownloadStatus
import media.grab.os.data.repository.DownloadRepository
import media.grab.os.ds.components.AppCard
import media.grab.os.ds.components.EmptyState
import media.grab.os.extractor.DownloadEngine
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(navController: NavHostController? = null) {
    val repo = remember { DownloadRepository.getInstance() }
    val scope = rememberCoroutineScope()
    val downloads by repo.downloads.collectAsState()
    var query by remember { mutableStateOf("") }
    var showCompletedOnly by remember { mutableStateOf(false) }

    val filtered = downloads.filter { d ->
        (query.isBlank() || d.title.contains(query, ignoreCase = true) ||
                d.url.contains(query, ignoreCase = true) || d.fileName.contains(query, ignoreCase = true)) &&
        (!showCompletedOnly || d.status == DownloadStatus.COMPLETED)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("İndirmeler") },
                actions = {
                    IconButton(onClick = { showCompletedOnly = !showCompletedOnly }) {
                        Icon(
                            if (showCompletedOnly) Icons.Default.FilterListOff else Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                    if (downloads.isNotEmpty()) {
                        IconButton(onClick = { repo.clearAll() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear all")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                singleLine = true
            )

            if (filtered.isEmpty()) {
                EmptyState(
                    title = if (query.isNotEmpty()) "Sonuç yok" else "Henüz indirme yok",
                    subtitle = if (query.isBlank()) "URL yapıştırarak başla" else ""
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered, key = { it.id }) { download ->
                        DownloadListItem(
                            download = download,
                            onRetry = {
                                scope.launch {
                                    val info = media.grab.os.extractor.ExtractorRegistry
                                        .extract(download.url).getOrNull()
                                    if (info != null) {
                                        DownloadEngine.downloadAndSave(
                                            navController?.context
                                                ?: return@launch,
                                            info
                                        )
                                    }
                                }
                            },
                            onDelete = { repo.removeDownload(download.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadListItem(
    download: Download,
    onRetry: () -> Unit,
    onDelete: () -> Unit
) {
    AppCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    when (download.mediaType) {
                        media.grab.os.data.model.MediaType.VIDEO -> Icons.Default.VideoFile
                        media.grab.os.data.model.MediaType.IMAGE -> Icons.Default.Image
                        media.grab.os.data.model.MediaType.AUDIO -> Icons.Default.AudioFile
                        else -> Icons.Default.InsertDriveFile
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        download.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1
                    )
                    Text(
                        "${'$'}{download.platform.displayName} • ${'$'}{download.fileName}",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
                StatusChip(download.status)
            }

            if (download.status == DownloadStatus.FAILED && download.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Hata: ${'$'}{download.errorMessage}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row {
                if (download.status == DownloadStatus.FAILED) {
                    TextButton(onClick = onRetry) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tekrar Dene")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: DownloadStatus) {
    val (color, text) = when (status) {
        DownloadStatus.QUEUED -> MaterialTheme.colorScheme.tertiary to "Sırada"
        DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.primary to "İndiriliyor"
        DownloadStatus.COMPLETED -> MaterialTheme.colorScheme.primary to "Tamam"
        DownloadStatus.FAILED -> MaterialTheme.colorScheme.error to "Hata"
        DownloadStatus.CANCELLED -> MaterialTheme.colorScheme.outline to "İptal"
    }
    AssistChip(
        onClick = {},
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.15f),
            labelColor = color
        )
    )
}
