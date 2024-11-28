package com.example.a7minutesworkout.repository

import android.util.Log
import com.example.a7minutesworkout.database.HistoryDao
import com.example.a7minutesworkout.database.HistoryEntity
import kotlinx.coroutines.flow.Flow

class DefaultHistoryRepository(private val historyDao: HistoryDao) : HistoryRepository {

    override suspend fun addHistory(date: String, description: String) {
        Log.d("DefaultHistoryRepository", "addHistory: $date, $description")
        val entity = HistoryEntity(0, date, description)
        historyDao.insertHistoryItem(entity)
    }

    override fun getAllHistory(): Flow<List<HistoryEntity>> {
        Log.d("DefaultHistoryRepository", "getAllHistory")
        return historyDao.getAllHistory()
    }

}