package com.example.happyplaces.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HappyPlaceDao {

    @Insert
    suspend fun insertHappyPlace(entity: HappyPlaceEntity)

    @Delete
    suspend fun deleteHappyPlace(entity: HappyPlaceEntity)

    @Query("SELECT * FROM happy_places")
    fun getAllHappyPlaces(): Flow<List<HappyPlaceEntity>>
}