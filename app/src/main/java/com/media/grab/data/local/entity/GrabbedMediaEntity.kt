package com.media.grab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grabbed_media")
data class GrabbedMediaEntity(
    @PrimaryKey val id: String,
    val url: String,
    val sourceApp: String,
    val sourcePackage: String,
    val title: String? = null,
    val thumbnailUrl: String? = null,
    val filePath: String? = null,
    val fileSize: Long = 0,
    val mimeType: String? = null,
    val duration: Long? = null,
    val width: Int? = null,
    val height: Int? = null,
    val capturedAt: Long = System.currentTimeMillis(),
    val downloaded: Boolean = false
)
