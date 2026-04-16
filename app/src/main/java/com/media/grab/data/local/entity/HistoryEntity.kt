package com.media.grab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val id: String,
    val url: String,
    val title: String,
    val platform: String,
    val thumbnailUrl: String? = null,
    val filePath: String? = null,
    val fileSize: Long = 0,
    val downloadedAt: Long = System.currentTimeMillis(),
    val source: HistorySource = HistorySource.DIRECT_DOWNLOAD
)

enum class HistorySource {
    DIRECT_DOWNLOAD, GRABBER_CAPTURE, CACHE_RECOVERY
}
