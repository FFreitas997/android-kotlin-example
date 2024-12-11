package com.example.weatherapplication.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wind(
    @SerialName("speed") val speed: Double,
    @SerialName("deg") val deg: Int
)