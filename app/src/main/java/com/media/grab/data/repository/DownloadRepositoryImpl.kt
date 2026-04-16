package com.media.grab.data.repository

import com.media.grab.data.local.dao.DownloadDao
import com.media.grab.data.local.entity.DownloadEntity
import com.media.grab.data.local.entity.DownloadStatus
import com.media.grab.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(
    private val dao: DownloadDao,
    @IoDispatcher private val io: CoroutineDispatcher
) : DownloadRepository {

    override fun getAllDownloads(): Flow<List<DownloadEntity>> = dao.all()

    override fun getActiveDownloads(): Flow<List<DownloadEntity>> =
        dao.byStatuses(listOf(DownloadStatus.PENDING, DownloadStatus.QUEUED, DownloadStatus.DOWNLOADING))

    override fun getCompletedDownloads(): Flow<List<DownloadEntity>> =
        dao.byStatus(DownloadStatus.COMPLETED)

    override suspend fun getDownload(id: String): DownloadEntity? = withContext(io) { dao.byId(id) }

    override suspend fun insertDownload(download: DownloadEntity) = withContext(io) { dao.insert(download) }

    override suspend fun updateDownload(download: DownloadEntity) = withContext(io) { dao.update(download) }

    override suspend fun deleteDownload(id: String) = withContext(io) { dao.deleteById(id) }

    override suspend fun updateStatus(id: String, status: DownloadStatus) =
        withContext(io) { dao.updateStatus(id, status, System.currentTimeMillis()) }

    override suspend fun updateProgress(id: String, downloadedSize: Long) =
        withContext(io) { dao.updateProgress(id, DownloadStatus.DOWNLOADING, downloadedSize, System.currentTimeMillis()) }

    override suspend fun markComplete(id: String, filePath: String) =
        withContext(io) { dao.updateComplete(id, DownloadStatus.COMPLETED, filePath, System.currentTimeMillis()) }
}
