package com.example.flagquiz.listeners

import android.content.Intent
import android.view.View
import com.example.flagquiz.MainActivity
import com.example.flagquiz.QuizQuestionActivity
import com.example.flagquiz.utils.Constants

class ClickListener(val context: MainActivity): View.OnClickListener {

    override fun onClick(view: View?) {
        val intent = Intent(context, QuizQuestionActivity::class.java)
        intent.putExtra(Constants.USERNAME_KEY, context.username)
        context.startActivity(intent)
        context.finish()
    }
}