package media.grab.os.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import media.grab.os.R
import media.grab.os.data.model.Download
import media.grab.os.data.model.DownloadStatus
import media.grab.os.ds.components.MGCard

@Composable
fun ActiveDownloadCard(download: Download) {
    MGCard {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBy) {
                Text(download.fileName, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Text("${(download.progress * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
            }
            LinearProgressIndicator(progress = { download.progress }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            Text(text = stringResource(when (download.status) { DownloadStatus.DOWNLOADING -> R.string.status_downloading; DownloadStatus.PARSING -> R.string.status_pending; else -> R.string.status_pending }), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
        }
    }
}