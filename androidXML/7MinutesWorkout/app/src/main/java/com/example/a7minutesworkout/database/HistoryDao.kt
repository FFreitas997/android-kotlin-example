package com.example.a7minutesworkout.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert
    suspend fun insertHistoryItem(entity: HistoryEntity)

    @Query("SELECT * FROM HistoryEntity ORDER BY exercise_date DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>
}