package com.example.a7minutesworkout.repository

import com.example.a7minutesworkout.database.HistoryEntity
import kotlinx.coroutines.flow.Flow


interface HistoryRepository {

    suspend fun addHistory(date: String, description: String)

    fun getAllHistory(): Flow<List<HistoryEntity>>
}