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
import media.grab.os.data.prefs.DownloadSettings
import media.grab.os.data.prefs.ProgressNotification
import media.grab.os.ds.components.MGCard
import media.grab.os.ds.components.MGOutlinedButton

@Composable
fun NotificationSection(state: DownloadSettings, onSet: (ProgressNotification) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.settings_notifications), style = MaterialTheme.typography.titleMedium)
        MGCard {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.settings_progress_notif), style = MaterialTheme.typography.titleSmall)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    MGOutlinedButton(text = "Always", onClick = { onSet(ProgressNotification.ALWAYS) }, modifier = Modifier.fillMaxWidth())
                    MGOutlinedButton(text = "When finished", onClick = { onSet(ProgressNotification.FINISHED) }, modifier = Modifier.fillMaxWidth())
                    MGOutlinedButton(text = "Off", onClick = { onSet(ProgressNotification.OFF) }, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}