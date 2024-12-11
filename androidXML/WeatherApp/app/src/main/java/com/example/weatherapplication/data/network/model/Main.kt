package com.example.weatherapplication.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Main(
    @SerialName("temp") val temp: Double,
    @SerialName("pressure") val pressure: Double,
    @SerialName("humidity") val humidity: Int,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
    @SerialName("sea_level") val seaLevel: Double,
    @SerialName("grnd_level") val grndLevel: Double
)