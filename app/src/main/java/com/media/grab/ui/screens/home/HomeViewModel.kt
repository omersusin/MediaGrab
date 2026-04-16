package com.media.grab.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.media.grab.data.local.entity.DownloadEntity
import com.media.grab.data.local.entity.DownloadStatus
import com.media.grab.data.local.entity.HistoryEntity
import com.media.grab.data.local.entity.HistorySource
import com.media.grab.data.repository.DownloadRepository
import com.media.grab.data.repository.HistoryRepository
import com.media.grab.grabber.GrabberManager
import com.media.grab.grabber.MediaDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val historyRepository: HistoryRepository,
    private val grabberManager: GrabberManager
) : ViewModel() {

    val downloads = downloadRepository.getAllDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentHistory = historyRepository.getRecentHistory(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun download(url: String) {
        viewModelScope.launch {
            val platform = MediaDetector.detectPlatform(url)

            if (!platform.isSupported && !platform.requiresGrabber) {
                _message.value = "Unsupported platform"
                return@launch
            }

            val download = DownloadEntity(
                id = UUID.randomUUID().toString(),
                url = url,
                title = "Downloading...",
                platform = platform.name
            )

            downloadRepository.insertDownload(download)
            _message.value = "Download started"

            // Start download service would go here
        }
    }

    fun grab(url: String) {
        viewModelScope.launch {
            val platform = MediaDetector.detectPlatform(url)
            grabberManager.captureMedia(
                url = url,
                sourceApp = platform.name,
                sourcePackage = "unknown"
            )
            _message.value = "Media captured via Grabber"
        }
    }

    fun cancelDownload(id: String) {
        viewModelScope.launch {
            downloadRepository.updateStatus(id, DownloadStatus.CANCELLED)
        }
    }

    fun retryDownload(id: String) {
        viewModelScope.launch {
            downloadRepository.updateStatus(id, DownloadStatus.PENDING)
        }
    }

    fun deleteDownload(id: String) {
        viewModelScope.launch {
            downloadRepository.deleteDownload(id)
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
