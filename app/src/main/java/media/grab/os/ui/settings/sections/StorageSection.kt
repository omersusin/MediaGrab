package media.grab.os.ui.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import media.grab.os.R
import media.grab.os.ds.components.MGCard
import media.grab.os.ds.components.MGOutlinedButton

@Composable
fun StorageSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.settings_storage), style = MaterialTheme.typography.titleMedium)
        MGCard {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Files saved to:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("/storage/emulated/0/Download/MediaGrab/", style = MaterialTheme.typography.bodyMedium)
                MGOutlinedButton(text = stringResource(R.string.settings_clear_history), onClick = { /* TODO: confirm + clear */ }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                MGOutlinedButton(text = stringResource(R.string.settings_clear_older), onClick = { /* TODO: clear > 30d */ }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}