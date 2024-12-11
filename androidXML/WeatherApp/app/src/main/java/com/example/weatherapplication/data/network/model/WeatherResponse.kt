package com.example.weatherapplication.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("coord") val coord: Coord,
    @SerialName("weather") val weather: List<Weather>,
    @SerialName("rain") val rain: Rain? = null,
    @SerialName("snow") val snow: Snow? = null,
    @SerialName("base") val base: String,
    @SerialName("main") val main: Main,
    @SerialName("visibility") val visibility: Int,
    @SerialName("wind") val wind: Wind,
    @SerialName("clouds") val clouds: Clouds,
    @SerialName("dt") val dt: Int,
    @SerialName("sys") val sys: Sys,
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("cod") val cod: Int
)