package com.ffreitas.flowify.data.models

data class User(
    val id: String = "",
    var name: String = "",
    val email: String = "",
    var picture: String = "",
    var mobile: Long = 0
)
