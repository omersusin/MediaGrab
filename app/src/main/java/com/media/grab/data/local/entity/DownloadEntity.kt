package com.media.grab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String,
    val url: String,
    val title: String,
    val platform: String,
    val thumbnailUrl: String? = null,
    val filePath: String? = null,
    val fileSize: Long = 0,
    val downloadedSize: Long = 0,
    val status: DownloadStatus = DownloadStatus.PENDING,
    val quality: String? = null,
    val format: String? = null,
    val errorMessage: String? = null,
    val retryCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class DownloadStatus {
    PENDING, QUEUED, DOWNLOADING, PAUSED, COMPLETED, FAILED, CANCELLED
}
