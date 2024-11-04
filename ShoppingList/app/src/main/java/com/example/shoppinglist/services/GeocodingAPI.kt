package com.example.shoppinglist.services

import com.example.shoppinglist.data.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingAPI {

    @GET("maps/api/geocode/json")
    suspend fun getAddressFromCoordinates(
        @Query("latlng") latlng: String,
        @Query("key") key: String
    ): GeocodingResponse

}