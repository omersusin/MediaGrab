package com.media.grab.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.media.grab.data.local.entity.DownloadEntity
import com.media.grab.data.local.entity.DownloadStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY createdAt DESC") fun all(): Flow<List<DownloadEntity>>
    @Query("SELECT * FROM downloads WHERE status = :s ORDER BY createdAt DESC") fun byStatus(s: DownloadStatus): Flow<List<DownloadEntity>>
    @Query("SELECT * FROM downloads WHERE status IN (:s) ORDER BY createdAt DESC") fun byStatuses(s: List<DownloadStatus>): Flow<List<DownloadEntity>>
    @Query("SELECT * FROM downloads WHERE id = :id") suspend fun byId(id: String): DownloadEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(d: DownloadEntity)
    @Update suspend fun update(d: DownloadEntity)
    @Delete suspend fun delete(d: DownloadEntity)
    @Query("DELETE FROM downloads WHERE id = :id") suspend fun deleteById(id: String)
    @Query("UPDATE downloads SET status = :s, updatedAt = :t WHERE id = :id") suspend fun updateStatus(id: String, s: DownloadStatus, t: Long)
    @Query("UPDATE downloads SET status = :s, downloadedSize = :size, updatedAt = :t WHERE id = :id") suspend fun updateProgress(id: String, s: DownloadStatus, size: Long, t: Long)
    @Query("UPDATE downloads SET status = :s, filePath = :path, updatedAt = :t WHERE id = :id") suspend fun updateComplete(id: String, s: DownloadStatus, path: String, t: Long)
}
