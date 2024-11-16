package com.example.flagquiz.listener

import android.content.Intent
import android.view.View
import com.example.flagquiz.MainActivity
import com.example.flagquiz.QuizQuestionActivity

class ClickListener(val activity: MainActivity): View.OnClickListener {

    override fun onClick(view: View?) {
        val intent = Intent(activity, QuizQuestionActivity::class.java)
        intent.putExtra("username", activity.username)
        activity.startActivity(intent)
        activity.finish()
    }
}