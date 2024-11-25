package com.example.a7minutesworkout.model

import com.example.a7minutesworkout.R

val listOfExercises = setOf<Exercise>(

    object : Exercise {
        override val id: Int = 1
        override val name: Int = R.string.jumping_jacks
        override val image: Int = R.drawable.ic_jumping_jacks
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    },

    object : Exercise {
        override val id: Int = 2
        override val name: Int = R.string.wall_sit
        override val image: Int = R.drawable.ic_wall_sit
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    },

    object : Exercise {
        override val id: Int = 3
        override val name: Int = R.string.push_up
        override val image: Int = R.drawable.ic_push_up
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    },

    object : Exercise {
        override val id: Int = 4
        override val name: Int = R.string.abdominal_crunch
        override val image: Int = R.drawable.ic_abdominal_crunch
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    },

    object : Exercise {
        override val id: Int = 5
        override val name: Int = R.string.step_up_on_chair
        override val image: Int = R.drawable.ic_step_up_onto_chair
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    },

    object : Exercise {
        override val id: Int = 6
        override val name: Int = R.string.squat
        override val image: Int = R.drawable.ic_squat
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    },

    object : Exercise {
        override val id: Int = 7
        override val name: Int = R.string.triceps_dip_on_chair
        override val image: Int = R.drawable.ic_triceps_dip_on_chair
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    },

    object : Exercise {
        override val id: Int = 8
        override val name: Int = R.string.plank
        override val image: Int = R.drawable.ic_plank
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    },

    object : Exercise {
        override val id: Int = 9
        override val name: Int = R.string.high_knees_running_in_place
        override val image: Int = R.drawable.ic_high_knees_running_in_place
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    },

    object : Exercise {
        override val id: Int = 10
        override val name: Int = R.string.lunge
        override val image: Int = R.drawable.ic_lunge
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    },

    object : Exercise {
        override val id: Int = 11
        override val name: Int = R.string.push_and_rotation
        override val image: Int = R.drawable.ic_push_up_and_rotation
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    },

    object : Exercise {
        override val id: Int = 12
        override val name: Int = R.string.side_plank
        override val image: Int = R.drawable.ic_side_plank
        override val isCompleted: Boolean = false
        override val isSelected: Boolean = false
    }

)