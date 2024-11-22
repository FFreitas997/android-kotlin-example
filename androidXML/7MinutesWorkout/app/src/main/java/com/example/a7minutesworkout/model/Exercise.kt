package com.example.a7minutesworkout.model

interface Exercise {
    val id: Int
    val name: String
    val image: Int
    val isCompleted: Boolean
    val isSelected: Boolean
}