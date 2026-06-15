package media.grab.os.ui.settings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import media.grab.os.R
import media.grab.os.data.prefs.ThemeMode
import media.grab.os.data.prefs.ThemeState
import media.grab.os.ds.components.MGCard
import media.grab.os.ds.components.MGOutlinedButton

@Composable
fun ThemeSection(state: ThemeState, onSetMode: (ThemeMode) -> Unit, onSetDynamic: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.settings_theme), style = MaterialTheme.typography.titleMedium)
        MGCard {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.settings_dynamic_color), modifier = Modifier.weight(1f))
                    Switch(checked = state.dynamicColor, onCheckedChange = onSetDynamic)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MGOutlinedButton(text = stringResource(R.string.settings_theme_system), onClick = { onSetMode(ThemeMode.SYSTEM_DEFAULT) }, modifier = Modifier.weight(1f))
            MGOutlinedButton(text = stringResource(R.string.settings_theme_light), onClick = { onSetMode(ThemeMode.LIGHT) }, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MGOutlinedButton(text = stringResource(R.string.settings_theme_dark), onClick = { onSetMode(ThemeMode.DARK) }, modifier = Modifier.weight(1f))
            MGOutlinedButton(text = stringResource(R.string.settings_theme_amoled), onClick = { onSetMode(ThemeMode.AMOLED) }, modifier = Modifier.weight(1f))
        }
    }
}