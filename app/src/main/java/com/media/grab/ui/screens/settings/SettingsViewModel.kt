package com.media.grab.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.media.grab.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val concurrentDownloads = preferencesManager.concurrentDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    val quality = preferencesManager.downloadQuality
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "best")

    val darkMode = preferencesManager.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val grabNotifications = preferencesManager.grabNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val downloadPath = preferencesManager.downloadPath
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun setConcurrentDownloads(count: Int) {
        viewModelScope.launch {
            preferencesManager.setConcurrentDownloads(count)
        }
    }

    fun setDownloadQuality(quality: String) {
        viewModelScope.launch {
            preferencesManager.setDownloadQuality(quality)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(enabled)
        }
    }

    fun setGrabNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setGrabNotifications(enabled)
        }
    }

    fun setDownloadPath(path: String) {
        viewModelScope.launch {
            preferencesManager.setDownloadPath(path)
        }
    }
}
