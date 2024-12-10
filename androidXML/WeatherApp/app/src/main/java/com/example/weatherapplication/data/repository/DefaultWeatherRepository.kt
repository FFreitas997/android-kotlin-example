package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.service.NetworkWeatherService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultWeatherRepository(
    private val service: NetworkWeatherService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherRepository {


}