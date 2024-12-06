package com.example.happyplaces.utils

import com.example.happyplaces.data.HappyPlaceModel
import com.example.happyplaces.database.HappyPlaceEntity

object HappyPlaceMapper {

    fun mapEntityToModel(entity: HappyPlaceEntity): HappyPlaceModel {
        return HappyPlaceModel(
            id = entity.id,
            title = entity.title,
            image = entity.image,
            description = entity.description,
            date = entity.date,
            location = entity.location,
            latitude = entity.latitude,
            longitude = entity.longitude
        )
    }

    fun mapModelToEntity(model: HappyPlaceModel): HappyPlaceEntity {
        return HappyPlaceEntity(
            id = model.id,
            title = model.title,
            image = model.image,
            description = model.description,
            date = model.date,
            location = model.location,
            latitude = model.latitude,
            longitude = model.longitude
        )
    }
}