package com.example.a7minutesworkout.model

import com.example.a7minutesworkout.constants.Constants

class ExerciseModel(list: List<Exercise>) {

    private var listOfExercises: List<Exercise> = list

    fun getExerciseAt(position: Int): Exercise = listOfExercises[position]

    fun getExerciseCount(): Int = listOfExercises.size

    //fun getExerciseIndex(exercise: Exercise): Int = listOfExercises.indexOf(exercise)

    //fun getExerciseIndexById(id: Int): Int = listOfExercises.indexOfFirst { it.id == id }

    //fun getExercises(): List<Exercise> = listOfExercises

    companion object Factory {
        fun create(): ExerciseModel {
            val list = listOfExercises
                .take(Constants.NUMBER_OF_EXERCISES)
                .shuffled()
            return ExerciseModel(list)
        }
    }
}