package com.example.flagquiz.listeners

import android.content.Intent
import android.view.View
import com.example.flagquiz.MainActivity
import com.example.flagquiz.QuizQuestionActivity

class ClickListener(val context: MainActivity): View.OnClickListener {

    override fun onClick(view: View?) {
        val intent = Intent(context, QuizQuestionActivity::class.java)
        intent.putExtra("username", context.username)
        context.startActivity(intent)
        context.finish()
    }
}