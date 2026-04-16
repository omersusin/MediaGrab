package com.media.grab.data.repository

import com.media.grab.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllHistory(): Flow<List<HistoryEntity>>
    fun getRecentHistory(limit: Int): Flow<List<HistoryEntity>>
    suspend fun getHistory(id: String): HistoryEntity?
    suspend fun insertHistory(history: HistoryEntity)
    suspend fun deleteHistory(id: String)
    suspend fun clearAllHistory()
}
