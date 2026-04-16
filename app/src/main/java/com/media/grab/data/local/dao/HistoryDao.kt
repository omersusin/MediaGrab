package com.media.grab.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.media.grab.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY downloadedAt DESC") fun all(): Flow<List<HistoryEntity>>
    @Query("SELECT * FROM history WHERE platform = :p ORDER BY downloadedAt DESC") fun byPlatform(p: String): Flow<List<HistoryEntity>>
    @Query("SELECT * FROM history ORDER BY downloadedAt DESC LIMIT :n") fun recent(n: Int): Flow<List<HistoryEntity>>
    @Query("SELECT * FROM history WHERE id = :id") suspend fun byId(id: String): HistoryEntity?
    @Query("SELECT * FROM history WHERE url = :url") suspend fun byUrl(url: String): HistoryEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(h: HistoryEntity)
    @Delete suspend fun delete(h: HistoryEntity)
    @Query("DELETE FROM history WHERE id = :id") suspend fun deleteById(id: String)
    @Query("DELETE FROM history") suspend fun clearAll()
}
