package media.grab.os.ui.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import media.grab.os.R
import media.grab.os.data.prefs.DownloadSettings
import media.grab.os.data.prefs.NetworkPolicy
import media.grab.os.ds.components.MGCard
import media.grab.os.ds.components.MGOutlinedButton

@Composable
fun DownloadSection(state: DownloadSettings, onSetQuality: (String) -> Unit, onSetParallel: (Int) -> Unit, onSetNetwork: (NetworkPolicy) -> Unit, onSetFilename: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.settings_downloads), style = MaterialTheme.typography.titleMedium)
        MGCard {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.settings_quality), style = MaterialTheme.typography.titleSmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    MGOutlinedButton(text = stringResource(R.string.settings_quality_best), onClick = { onSetQuality("BEST") }, modifier = Modifier.weight(1f))
                    MGOutlinedButton(text = stringResource(R.string.settings_quality_medium), onClick = { onSetQuality("MEDIUM") }, modifier = Modifier.weight(1f))
                    MGOutlinedButton(text = stringResource(R.string.settings_quality_low), onClick = { onSetQuality("LOW") }, modifier = Modifier.weight(1f))
                }
                Text("${stringResource(R.string.settings_parallel)}: ${state.parallel}", style = MaterialTheme.typography.titleSmall)
                Slider(value = state.parallel.toFloat(), onValueChange = { onSetParallel(it.toInt()) }, valueRange = 1f..8f, steps = 7)
                Text(stringResource(R.string.settings_network), style = MaterialTheme.typography.titleSmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    MGOutlinedButton(text = stringResource(R.string.settings_network_any), onClick = { onSetNetwork(NetworkPolicy.ANY) }, modifier = Modifier.weight(1f))
                    MGOutlinedButton(text = stringResource(R.string.settings_network_wifi), onClick = { onSetNetwork(NetworkPolicy.WIFI_ONLY) }, modifier = Modifier.weight(1f))
                }
                Text(stringResource(R.string.settings_filename), style = MaterialTheme.typography.titleSmall)
                androidx.compose.material3.OutlinedTextField(value = state.filenameTemplate, onValueChange = onSetFilename, modifier = Modifier.fillMaxWidth(), placeholder = { Text(stringResource(R.string.settings_filename_hint)) }, singleLine = true)
            }
        }
    }
}