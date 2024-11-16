package com.example.flagquiz.data

data class Option(
    val country: Country,
    var selected: Boolean = false,
)

data class Question(
    val id: Int,
    val value: String,
    val country: Country,
    val hint: String?,
    val options: List<Option>,
)
