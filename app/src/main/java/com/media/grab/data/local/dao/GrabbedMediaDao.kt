package com.media.grab.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.media.grab.data.local.entity.GrabbedMediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrabbedMediaDao {
    @Query("SELECT * FROM grabbed_media ORDER BY capturedAt DESC") fun all(): Flow<List<GrabbedMediaEntity>>
    @Query("SELECT * FROM grabbed_media WHERE downloaded = 0 ORDER BY capturedAt DESC") fun undownloaded(): Flow<List<GrabbedMediaEntity>>
    @Query("SELECT * FROM grabbed_media WHERE sourceApp = :app ORDER BY capturedAt DESC") fun byApp(app: String): Flow<List<GrabbedMediaEntity>>
    @Query("SELECT * FROM grabbed_media WHERE id = :id") suspend fun byId(id: String): GrabbedMediaEntity?
    @Query("SELECT * FROM grabbed_media WHERE url = :url") suspend fun byUrl(url: String): GrabbedMediaEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(m: GrabbedMediaEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(l: List<GrabbedMediaEntity>)
    @Update suspend fun update(m: GrabbedMediaEntity)
    @Delete suspend fun delete(m: GrabbedMediaEntity)
    @Query("DELETE FROM grabbed_media WHERE id = :id") suspend fun deleteById(id: String)
    @Query("UPDATE grabbed_media SET downloaded = :d WHERE id = :id") suspend fun setDownloaded(id: String, d: Boolean)
    @Query("DELETE FROM grabbed_media WHERE downloaded = 1") suspend fun deleteDownloaded()
}
