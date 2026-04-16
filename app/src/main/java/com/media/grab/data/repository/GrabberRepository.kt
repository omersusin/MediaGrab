package com.media.grab.data.repository

import com.media.grab.data.local.entity.GrabbedMediaEntity
import kotlinx.coroutines.flow.Flow

interface GrabberRepository {
    fun getAllGrabbedMedia(): Flow<List<GrabbedMediaEntity>>
    fun getUndownloadedMedia(): Flow<List<GrabbedMediaEntity>>
    suspend fun getGrabbedMedia(id: String): GrabbedMediaEntity?
    suspend fun getByUrl(url: String): GrabbedMediaEntity?
    suspend fun insertGrabbedMedia(media: GrabbedMediaEntity)
    suspend fun insertAllGrabbedMedia(mediaList: List<GrabbedMediaEntity>)
    suspend fun updateGrabbedMedia(media: GrabbedMediaEntity)
    suspend fun deleteGrabbedMedia(id: String)
    suspend fun markDownloaded(id: String, downloaded: Boolean)
}
