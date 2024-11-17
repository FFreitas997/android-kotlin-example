package com.example.flagquiz.listeners

import android.graphics.Typeface
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
            .forEach {
                context.currentQuestion.options
                    .find { option -> option.country.name == it.text.toString() }
                    ?.selected = false
                it.setBackgroundResource(R.drawable.default_option_border_bg)
                it.setTypeface(null, Typeface.NORMAL)
            }

        val currentQuestionOptionSelected = context
            .currentQuestion
            .options
            .find { selectedOption.text.toString() == it.country.name }

        if (currentQuestionOptionSelected == null)
            throw Exception("Option not found in the current question")

        currentQuestionOptionSelected.selected = !currentQuestionOptionSelected.selected

        val backgroundOption =
            if (currentQuestionOptionSelected.selected)
                R.drawable.selected_option_background
            else
                R.drawable.default_option_border_bg

        val textTypeface =
            if (currentQuestionOptionSelected.selected)
                Typeface.BOLD
            else
                Typeface.NORMAL

        selectedOption.setBackgroundResource(backgroundOption)
        selectedOption.setTypeface(null, textTypeface)
    }
}