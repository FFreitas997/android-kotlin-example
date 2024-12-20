package com.ffreitas.flowify.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id")
    val id: String = "",

    @SerialName("name")
    var name: String = "",

    @SerialName("email")
    val email: String = "",

    @SerialName("picture")
    var picture: String = "",

    @SerialName("mobile")
    var mobile: Long = 0,
)
