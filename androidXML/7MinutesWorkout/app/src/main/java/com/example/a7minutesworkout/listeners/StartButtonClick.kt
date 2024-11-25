package com.example.a7minutesworkout.listeners

import android.content.Intent
import android.view.View
import com.example.a7minutesworkout.ExerciseActivity
import com.example.a7minutesworkout.MainActivity

class StartButtonClick(val context: MainActivity) : View.OnClickListener {

    override fun onClick(button: View?) {
        val intent = Intent(context, ExerciseActivity::class.java)
        context.startActivity(intent)
    }

}