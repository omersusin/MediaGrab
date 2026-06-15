package media.grab.os.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import media.grab.os.R
import media.grab.os.navigation.Destinations
import media.grab.os.ui.settings.sections.AboutSection
import media.grab.os.ui.settings.sections.AccessSection
import media.grab.os.ui.settings.sections.DownloadSection
import media.grab.os.ui.settings.sections.LegalSection
import media.grab.os.ui.settings.sections.NotificationSection
import media.grab.os.ui.settings.sections.StorageSection
import media.grab.os.ui.settings.sections.ThemeSection

@Composable
fun SettingsScreen(navController: NavController, vm: SettingsViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineMedium)
            ThemeSection(state = state.theme, onSetMode = vm::setThemeMode, onSetDynamic = vm::setDynamicColor)
            AccessSection()
            DownloadSection(state = state.download, onSetQuality = vm::setQuality, onSetParallel = vm::setParallel, onSetNetwork = vm::setNetwork, onSetFilename = vm::setFilenameTemplate)
            NotificationSection(state = state.download, onSet = vm::setProgressNotif)
            StorageSection()
            AboutSection(onClick = { navController.navigate(Destinations.About.route) })
            LegalSection(onClick = { navController.navigate(Destinations.Legal.route) })
        }
    }
}