package com.media.grab.data.repository

import com.media.grab.data.local.entity.DownloadEntity
import com.media.grab.data.local.entity.DownloadStatus
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    fun getAllDownloads(): Flow<List<DownloadEntity>>
    fun getActiveDownloads(): Flow<List<DownloadEntity>>
    fun getCompletedDownloads(): Flow<List<DownloadEntity>>
    suspend fun getDownload(id: String): DownloadEntity?
    suspend fun insertDownload(download: DownloadEntity)
    suspend fun updateDownload(download: DownloadEntity)
    suspend fun deleteDownload(id: String)
    suspend fun updateStatus(id: String, status: DownloadStatus)
    suspend fun updateProgress(id: String, downloadedSize: Long)
    suspend fun markComplete(id: String, filePath: String)
}
