package com.example.flagquiz.utils

import com.example.flagquiz.data.Country
import com.example.flagquiz.data.Option
import com.example.flagquiz.data.Question
import com.example.flagquiz.data.countries

object Questions {

    private fun generateRandomQuestions(numberOfQuestions: Int): MutableSet<Question> {
        if (numberOfQuestions <= 0 || numberOfQuestions > countries.size)
            throw IllegalArgumentException("Number of questions must be between 1 and ${countries.size}")
        val questionsSet = mutableSetOf<Question>()
        var numQuestion = 1;
        do {
            val country = countries
                .filter { questionsSet.none { q -> q.country == it } }
                .random()
            var options = countries
                .filter { it != country }
                .take(3)
            options = (options + country).shuffled()
            val question = Question(
                id = numQuestion,
                value = "What country does this flag belong to?",
                country = country,
                hint = "This country is located in ${country.continent}",
                options = options.map { country -> Option(country = country, selected = false) }
            )
            numQuestion++
            questionsSet.add(question)
        } while (questionsSet.size < numberOfQuestions)
        return questionsSet
    }

    fun getQuestions(size: Int): MutableSet<Question> {
        if (size <= 0 || size > countries.size)
            throw IllegalArgumentException("Number of questions must be between 1 and ${countries.size}")
        return generateRandomQuestions(size)
    }

    fun isCorrect(question: Question, country: Country): Boolean {
        return question.country.name == country.name
    }

}