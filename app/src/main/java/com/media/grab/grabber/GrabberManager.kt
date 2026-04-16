package com.media.grab.grabber

import android.content.Context
import com.media.grab.data.local.entity.GrabbedMediaEntity
import com.media.grab.data.repository.GrabberRepository
import com.media.grab.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class GrabberManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cacheScanner: CacheScanner,
    private val grabberRepository: GrabberRepository,
    @IoDispatcher private val io: CoroutineDispatcher
) {
    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _capturedMedia = MutableStateFlow<List<GrabbedMediaEntity>>(emptyList())
    val capturedMedia: StateFlow<List<GrabbedMediaEntity>> = _capturedMedia.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun startGrabber() {
        _isActive.value = true
    }

    fun stopGrabber() {
        _isActive.value = false
    }

    suspend fun scanCache(): List<CachedMedia> = withContext(io) {
        _isScanning.value = true
        try {
            val cached = cacheScanner.scanCache()
            val mediaList = cached.map { cachedMedia ->
                GrabbedMediaEntity(
                    id = UUID.randomUUID().toString(),
                    url = cachedMedia.path,
                    sourceApp = "Cache",
                    sourcePackage = "android",
                    title = cachedMedia.name,
                    fileSize = cachedMedia.size,
                    mimeType = cachedMedia.mimeType,
                    capturedAt = cachedMedia.lastModified,
                    downloaded = false
                )
            }
            grabberRepository.insertAllGrabbedMedia(mediaList)
            _capturedMedia.value = mediaList
            mediaList
        } finally {
            _isScanning.value = false
        }
    }

    suspend fun captureMedia(
        url: String,
        sourceApp: String,
        sourcePackage: String,
        title: String? = null
    ) = withContext(io) {
        val existing = grabberRepository.getByUrl(url)
        if (existing == null) {
            val media = GrabbedMediaEntity(
                id = UUID.randomUUID().toString(),
                url = url,
                sourceApp = sourceApp,
                sourcePackage = sourcePackage,
                title = title,
                capturedAt = System.currentTimeMillis(),
                downloaded = false
            )
            grabberRepository.insertGrabbedMedia(media)
            _capturedMedia.value = _capturedMedia.value + media
        }
    }

    suspend fun removeCapturedMedia(id: String) = withContext(io) {
        grabberRepository.deleteGrabbedMedia(id)
        _capturedMedia.value = _capturedMedia.value.filter { it.id != id }
    }
}
