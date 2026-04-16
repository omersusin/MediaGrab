package com.media.grab.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.media.grab.data.local.dao.DownloadDao
import com.media.grab.data.local.dao.GrabbedMediaDao
import com.media.grab.data.local.dao.HistoryDao
import com.media.grab.data.local.entity.DownloadEntity
import com.media.grab.data.local.entity.GrabbedMediaEntity
import com.media.grab.data.local.entity.HistoryEntity

@Database(
    entities = [DownloadEntity::class, GrabbedMediaEntity::class, HistoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
    abstract fun grabbedMediaDao(): GrabbedMediaDao
    abstract fun historyDao(): HistoryDao
}

class Converters {
    @androidx.room.TypeConverter
    fun fromStringList(v: String?): List<String>? = v?.split(",")?.filter { it.isNotEmpty() }

    @androidx.room.TypeConverter
    fun toStringList(v: List<String>?): String? = v?.joinToString(",")
}
