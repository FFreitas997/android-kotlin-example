package com.example.a7minutesworkout

import com.example.a7minutesworkout.model.Exercise

object Constants {

    const val EXERCISE_TIMER: Long = 30L
    const val READY_TIMER: Long = 10L


    val listOfExercises = listOf<Exercise>(

        object : Exercise {
            override val id: Int = 1
            override val name: String = "Jumping Jacks"
            override val image: Int = R.drawable.ic_jumping_jacks
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        },

        object : Exercise {
            override val id: Int = 2
            override val name: String = "Wall Sit"
            override val image: Int = R.drawable.ic_wall_sit
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        },

        object : Exercise {
            override val id: Int = 3
            override val name: String = "Push Up"
            override val image: Int = R.drawable.ic_push_up
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        },

        object : Exercise {
            override val id: Int = 4
            override val name: String = "Abdominal Crunch"
            override val image: Int = R.drawable.ic_abdominal_crunch
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        },

        object : Exercise {
            override val id: Int = 5
            override val name: String = "Step Up On Chair"
            override val image: Int = R.drawable.ic_step_up_onto_chair
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        },

        object : Exercise {
            override val id: Int = 6
            override val name: String = "Squat"
            override val image: Int = R.drawable.ic_squat
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        },

        object : Exercise {
            override val id: Int = 7
            override val name: String = "Tricep Dip On Chair"
            override val image: Int = R.drawable.ic_triceps_dip_on_chair
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        },

        object : Exercise {
            override val id: Int = 8
            override val name: String = "Plank"
            override val image: Int = R.drawable.ic_plank
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        },

        object : Exercise {
            override val id: Int = 9
            override val name: String = "High Knees Running In Place"
            override val image: Int = R.drawable.ic_high_knees_running_in_place
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        },

        object : Exercise {
            override val id: Int = 10
            override val name: String = "Lunge"
            override val image: Int = R.drawable.ic_lunge
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        },

        object : Exercise {
            override val id: Int = 11
            override val name: String = "Pushup And Rotation"
            override val image: Int = R.drawable.ic_push_up_and_rotation
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        },

        object : Exercise {
            override val id: Int = 12
            override val name: String = "Side Plank"
            override val image: Int = R.drawable.ic_side_plank
            override val isCompleted: Boolean = false
            override val isSelected: Boolean = false
        }

    )
}