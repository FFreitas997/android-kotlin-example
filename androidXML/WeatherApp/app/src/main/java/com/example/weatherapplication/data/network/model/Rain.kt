package com.example.weatherapplication.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rain(@SerialName("1h") val value: Double)
