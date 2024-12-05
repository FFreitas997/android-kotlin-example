package com.example.happyplaces.repository

import com.example.happyplaces.data.HappyPlaceModel
import com.example.happyplaces.database.HappyPlaceDao
import com.example.happyplaces.database.HappyPlaceEntity
import com.example.happyplaces.utils.HappyPlaceMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DefaultHappyPlaceRepository(
    private val dataAccess: HappyPlaceDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : HappyPlacesRepository {

    override suspend fun createHappyPlace(happyPlace: HappyPlaceModel): Unit =
        withContext(dispatcher) {
            HappyPlaceMapper
                .mapModelToEntity(happyPlace)
                .also { dataAccess.insertHappyPlace(it) }
        }

    override suspend fun updateHappyPlace(happyPlace: HappyPlaceModel): Unit =
        withContext(dispatcher) {
            HappyPlaceMapper
                .mapModelToEntity(happyPlace)
                .also { dataAccess.updateHappyPlace(it) }
        }

    override suspend fun deleteHappyPlace(happyPlace: HappyPlaceModel): Unit =
        withContext(dispatcher) {
            HappyPlaceMapper
                .mapModelToEntity(happyPlace)
                .also { dataAccess.deleteHappyPlace(it) }
        }

    override fun readHappyPlace(id: Int): Flow<HappyPlaceEntity> =
        dataAccess.getHappyPlaceById(id)

    override fun readHappyPlaces(): Flow<List<HappyPlaceEntity>> =
        dataAccess.getAllHappyPlaces()

}