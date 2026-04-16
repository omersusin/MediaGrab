package com.media.grab.ui.screens.grabber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.media.grab.data.local.entity.GrabbedMediaEntity
import com.media.grab.data.repository.GrabberRepository
import com.media.grab.grabber.CachedMedia
import com.media.grab.grabber.GrabberManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrabberViewModel @Inject constructor(
    private val grabberManager: GrabberManager,
    private val grabberRepository: GrabberRepository
) : ViewModel() {

    val isActive = grabberManager.isActive
    val isScanning = grabberManager.isScanning
    val capturedMedia = grabberManager.capturedMedia
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _rootAvailable = MutableStateFlow(false)
    val rootAvailable: StateFlow<Boolean> = _rootAvailable.asStateFlow()

    private val _scanResults = MutableStateFlow<List<CachedMedia>>(emptyList())
    val scanResults: StateFlow<List<CachedMedia>> = _scanResults.asStateFlow()

    init {
        checkRootAccess()
    }

    private fun checkRootAccess() {
        // Check for root - simplified for demo
        _rootAvailable.value = false
    }

    fun startGrabber() {
        grabberManager.startGrabber()
    }

    fun stopGrabber() {
        grabberManager.stopGrabber()
    }

    fun scanCache() {
        viewModelScope.launch {
            val results = grabberManager.scanCache()
            _scanResults.value = results
        }
    }

    fun downloadMedia(media: GrabbedMediaEntity) {
        viewModelScope.launch {
            grabberRepository.markDownloaded(media.id, true)
        }
    }

    fun deleteMedia(id: String) {
        viewModelScope.launch {
            grabberManager.removeCapturedMedia(id)
        }
    }
}
