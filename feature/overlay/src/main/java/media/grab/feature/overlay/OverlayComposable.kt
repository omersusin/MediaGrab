package media.grab.feature.overlay

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow

enum class DownloadStatus { IDLE, DOWNLOADING, SUCCESS }

@Composable
fun OverlayButton(downloadState: StateFlow<DownloadStatus>) {
    val state by downloadState.collectAsState()
    FloatingActionButton(
        onClick = { /* TODO: Trigger extraction and download */ },
        modifier = Modifier.size(48.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        when (state) {
            DownloadStatus.IDLE -> Icon(Icons.Rounded.Download, contentDescription = "Download")
            DownloadStatus.DOWNLOADING -> Icon(Icons.Rounded.Check, contentDescription = "Downloading")
            DownloadStatus.SUCCESS -> Icon(Icons.Rounded.Check, contentDescription = "Completed")
        }
    }
}
