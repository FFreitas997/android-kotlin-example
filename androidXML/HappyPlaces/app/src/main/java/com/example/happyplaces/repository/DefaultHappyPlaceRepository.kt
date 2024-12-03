package com.example.happyplaces.repository

import com.example.happyplaces.data.HappyPlaceModel
import com.example.happyplaces.database.HappyPlaceDao
import com.example.happyplaces.database.HappyPlaceEntity
import kotlinx.coroutines.flow.Flow

class DefaultHappyPlaceRepository(private val dao: HappyPlaceDao): HappyPlacesRepository {

    override suspend fun createHappyPlace(happyPlace: HappyPlaceModel) {
        val entity = HappyPlaceEntity(
            id = -1,
            title = happyPlace.title,
            image = happyPlace.image,
            description = happyPlace.description,
            date = happyPlace.date,
            location = happyPlace.location,
            latitude = happyPlace.latitude,
            longitude = happyPlace.longitude
        )
        dao.insertHappyPlace(entity)
    }

    override suspend fun deleteHappyPlace(happyPlace: HappyPlaceModel) {
        val entity = HappyPlaceEntity(
            id = happyPlace.id,
            title = happyPlace.title,
            image = happyPlace.image,
            description = happyPlace.description,
            date = happyPlace.date,
            location = happyPlace.location,
            latitude = happyPlace.latitude,
            longitude = happyPlace.longitude
        )
        dao.deleteHappyPlace(entity)
    }

    override fun getHappyPlaces(): Flow<List<HappyPlaceEntity>> {
        return dao.getAllHappyPlaces()
    }

}