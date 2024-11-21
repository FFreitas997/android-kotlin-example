package com.example.a7minutesworkout

import android.content.Intent
import android.view.View

class StartButtonClick(val context: MainActivity) : View.OnClickListener {

    override fun onClick(button: View?) {
        val intent = Intent(context, ExerciseActivity::class.java)
        context.startActivity(intent)
    }

}