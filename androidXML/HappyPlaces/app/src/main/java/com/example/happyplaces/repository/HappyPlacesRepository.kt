package com.example.happyplaces.repository

import com.example.happyplaces.data.HappyPlaceModel
import com.example.happyplaces.database.HappyPlaceEntity
import kotlinx.coroutines.flow.Flow

interface HappyPlacesRepository {

    suspend fun createHappyPlace(happyPlace: HappyPlaceModel)

    suspend fun deleteHappyPlace(happyPlace: HappyPlaceModel)

    fun getHappyPlaces(): Flow<List<HappyPlaceEntity>>
}