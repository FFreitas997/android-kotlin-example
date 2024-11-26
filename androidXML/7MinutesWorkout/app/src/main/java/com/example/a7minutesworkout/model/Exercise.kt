package com.example.a7minutesworkout.model

interface Exercise {
    val id: Int
    val name: Int
    val image: Int
    var status: ExerciseStatus

    //fun setExerciseStatus(status: ExerciseStatus){ this.status = status }
}