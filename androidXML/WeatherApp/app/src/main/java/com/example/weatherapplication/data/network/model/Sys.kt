package com.example.weatherapplication.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sys(
    @SerialName("type") val type: Int? = null,
    @SerialName("message") val message: Double? = null,
    @SerialName("country") val country: String,
    @SerialName("sunrise") val sunrise: Int,
    @SerialName("sunset") val sunset: Int
)