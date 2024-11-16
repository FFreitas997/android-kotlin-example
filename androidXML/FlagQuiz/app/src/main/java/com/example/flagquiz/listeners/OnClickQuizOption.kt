package com.example.flagquiz.listeners

import android.view.View
import android.widget.TextView
import com.example.flagquiz.QuizQuestionActivity
import com.example.flagquiz.R

class OnClickQuizOption(val context: QuizQuestionActivity) : View.OnClickListener {

    override fun onClick(view: View?) {
        val selectedOption = (view as TextView)
        context
            .options
            .filter { it.text != selectedOption.text }
            .forEach { it.setBackgroundResource(R.drawable.default_option_border_bg) }
        context
            .currentQuestion
            .options
            .forEach {
                if (selectedOption.text.toString() == it.country.name) {
                    it.selected = !it.selected
                    selectedOption
                        .setBackgroundResource(
                            if (it.selected)
                                R.drawable.selected_option_background
                            else
                                R.drawable.default_option_border_bg
                        )
                }
            }
    }
}