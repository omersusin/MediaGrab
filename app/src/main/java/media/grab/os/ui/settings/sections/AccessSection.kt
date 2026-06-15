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
fun AccessSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.settings_access), style = MaterialTheme.typography.titleMedium)
        MGCard {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.access_a11y) + ": " + stringResource(R.string.access_status_active), style = MaterialTheme.typography.bodyLarge)
                Text(stringResource(R.string.access_shizuku) + ": " + stringResource(R.string.access_status_inactive), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(stringResource(R.string.access_root) + ": " + stringResource(R.string.access_status_inactive), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                MGOutlinedButton(text = stringResource(R.string.access_grant), onClick = { /* TODO: open settings intent */ }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }
        }
    }
}