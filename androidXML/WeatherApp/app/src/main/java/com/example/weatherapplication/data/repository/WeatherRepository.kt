package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.network.model.WeatherResponse

fun interface WeatherRepository {

    suspend fun getWeatherData(lat: Double, lon: Double): WeatherResponse
}