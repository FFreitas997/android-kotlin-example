package com.example.a7minutesworkout.model.exercise

class ExerciseModel(private val list: List<Exercise>) {

    fun getExerciseAt(position: Int): Exercise {
        if (position < 0 || position >= list.size)
            throw IndexOutOfBoundsException("Invalid position $position")
        return list[position]
    }

    fun getExerciseCount(): Int = list.size

    fun getExercises(): List<Exercise> = list

    fun setStatusAt(position: Int, status: ExerciseStatus) {
        if (position < 0 || position >= list.size)
            throw IndexOutOfBoundsException("Invalid position $position")
        list[position].status = status
    }

    companion object {
        fun create(numberOfExercises: Int): ExerciseModel {
            val list = listOfExercises.take(numberOfExercises)
            return ExerciseModel(list)
        }
    }
}