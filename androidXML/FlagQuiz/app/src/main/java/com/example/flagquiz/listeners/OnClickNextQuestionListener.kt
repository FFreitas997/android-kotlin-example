package com.example.flagquiz.listeners

import android.view.View
import android.widget.Toast
import com.example.flagquiz.QuizQuestionActivity
import com.example.flagquiz.R
import com.example.flagquiz.utils.Questions

class OnClickNextQuestionListener(
    val context: QuizQuestionActivity,
    val nextQuestion: () -> Unit
) : View.OnClickListener {

    override fun onClick(view: View?) {
        val selectedOption = context
            .currentQuestion
            .options
            .firstOrNull { it.selected }
        if (selectedOption == null) {
            Toast
                .makeText(context, "Please select an option", Toast.LENGTH_LONG)
                .show()
            return
        }
        val hasCorrectAnswer = Questions
            .isCorrect(context.currentQuestion, selectedOption.country)
        if (!hasCorrectAnswer)
            Toast
                .makeText(context, "Sorry Wrong Answer :(", Toast.LENGTH_LONG)
                .show()
        else {
            Toast
                .makeText(context, "You've answered correctly :)", Toast.LENGTH_LONG)
                .show()
            context.score += 1
        }
        context
            .options
            .forEach {
                it.setBackgroundResource(
                    if (it.text == context.currentQuestion.country.name)
                        R.drawable.correct_option_background
                    else
                        R.drawable.incorrect_option_background
                )
            }
        nextQuestion()
    }
}