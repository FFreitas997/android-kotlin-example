package com.example.weatherapplication.data.network.service

import com.example.weatherapplication.data.network.model.WeatherResponse
import com.example.weatherapplication.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkWeatherService {

    @GET("weather")
    suspend fun currentWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("units") units: String = Constants.METRIC_UNIT
    ): WeatherResponse
}