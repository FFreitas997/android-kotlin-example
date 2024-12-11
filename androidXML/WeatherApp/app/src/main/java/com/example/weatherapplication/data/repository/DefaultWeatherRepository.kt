package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.network.service.NetworkWeatherService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultWeatherRepository(
    private val service: NetworkWeatherService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherRepository {


    override suspend fun getWeatherData(lat: Double, lon: Double) =
        withContext(dispatcher) { service.currentWeatherData(lat, lon) }
}