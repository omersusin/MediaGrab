package com.media.grab.data.repository

import com.media.grab.data.local.dao.HistoryDao
import com.media.grab.data.local.entity.HistoryEntity
import com.media.grab.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val dao: HistoryDao,
    @IoDispatcher private val io: CoroutineDispatcher
) : HistoryRepository {

    override fun getAllHistory(): Flow<List<HistoryEntity>> = dao.all()
    override fun getRecentHistory(limit: Int): Flow<List<HistoryEntity>> = dao.recent(limit)

    override suspend fun getHistory(id: String): HistoryEntity? =
        withContext(io) { dao.byId(id) }

    override suspend fun insertHistory(history: HistoryEntity) =
        withContext(io) { dao.insert(history) }

    override suspend fun deleteHistory(id: String) =
        withContext(io) { dao.deleteById(id) }

    override suspend fun clearAllHistory() =
        withContext(io) { dao.clearAll() }
}
