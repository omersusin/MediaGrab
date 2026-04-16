package com.media.grab.data.repository

import com.media.grab.data.local.dao.GrabbedMediaDao
import com.media.grab.data.local.entity.GrabbedMediaEntity
import com.media.grab.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GrabberRepositoryImpl @Inject constructor(
    private val dao: GrabbedMediaDao,
    @IoDispatcher private val io: CoroutineDispatcher
) : GrabberRepository {

    override fun getAllGrabbedMedia(): Flow<List<GrabbedMediaEntity>> = dao.all()
    override fun getUndownloadedMedia(): Flow<List<GrabbedMediaEntity>> = dao.undownloaded()

    override suspend fun getGrabbedMedia(id: String): GrabbedMediaEntity? =
        withContext(io) { dao.byId(id) }

    override suspend fun getByUrl(url: String): GrabbedMediaEntity? =
        withContext(io) { dao.byUrl(url) }

    override suspend fun insertGrabbedMedia(media: GrabbedMediaEntity) =
        withContext(io) { dao.insert(media) }

    override suspend fun insertAllGrabbedMedia(mediaList: List<GrabbedMediaEntity>) =
        withContext(io) { dao.insertAll(mediaList) }

    override suspend fun updateGrabbedMedia(media: GrabbedMediaEntity) =
        withContext(io) { dao.update(media) }

    override suspend fun deleteGrabbedMedia(id: String) =
        withContext(io) { dao.deleteById(id) }

    override suspend fun markDownloaded(id: String, downloaded: Boolean) =
        withContext(io) { dao.setDownloaded(id, downloaded) }
}
