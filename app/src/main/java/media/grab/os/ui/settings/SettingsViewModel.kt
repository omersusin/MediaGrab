package media.grab.os.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import media.grab.os.data.prefs.NetworkPolicy
import media.grab.os.data.prefs.ProgressNotification
import media.grab.os.data.prefs.SettingsRepository
import media.grab.os.data.prefs.ThemeMode
import media.grab.os.data.prefs.ThemeRepository
import javax.inject.Inject

data class SettingsUiState(val theme: ThemeState = ThemeState(), val download: DownloadSettings = DownloadSettings())

@HiltViewModel
class SettingsViewModel @Inject constructor(private val themeRepo: ThemeRepository, private val settingsRepo: SettingsRepository) : ViewModel() {
    val state: StateFlow<SettingsUiState> = kotlinx.coroutines.flow.combine(themeRepo.themeState, settingsRepo.settings) { theme, download -> SettingsUiState(theme, download) }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState())

    fun setThemeMode(mode: ThemeMode) { viewModelScope.launch { themeRepo.setMode(mode) } }
    fun setDynamicColor(enabled: Boolean) { viewModelScope.launch { themeRepo.setDynamicColor(enabled) } }
    fun setQuality(q: String) { viewModelScope.launch { settingsRepo.setQuality(q) } }
    fun setParallel(p: Int) { viewModelScope.launch { settingsRepo.setParallel(p) } }
    fun setNetwork(n: NetworkPolicy) { viewModelScope.launch { settingsRepo.setNetwork(n) } }
    fun setFilenameTemplate(s: String) { viewModelScope.launch { settingsRepo.setFilenameTemplate(s) } }
    fun setProgressNotif(p: ProgressNotification) { viewModelScope.launch { settingsRepo.setProgressNotif(p) } }
}