package com.example.a7minutesworkout.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "exercise_date", defaultValue = "CURRENT_TIMESTAMP")
    val date: String,

    @ColumnInfo(name = "exercise_description", defaultValue = "No description")
    val description: String
)