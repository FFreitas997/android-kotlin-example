package com.example.flagquiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.flagquiz.data.Question
import com.example.flagquiz.data.Questions
import com.example.flagquiz.data.Statistic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizQuestionActivity : AppCompatActivity() {

    private val quizSize = 10
    private var startTime = System.currentTimeMillis()
    private var score = 0
    private val quizQuestions = Questions.getQuestions(quizSize)
    private lateinit var currentQuestion: Question

    private lateinit var questionTextView: TextView
    private lateinit var questionHintTextView: TextView
    private lateinit var questionImageView: ImageView

    private lateinit var progressBar: ProgressBar
    private lateinit var progressTextView: TextView

    private lateinit var optionOneTextView: TextView
    private lateinit var optionTwoTextView: TextView
    private lateinit var optionThreeTextView: TextView
    private lateinit var optionFourTextView: TextView

    private lateinit var buttonSubmit: Button

    private lateinit var usernameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz_question)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startTime = System.currentTimeMillis()

        questionTextView = findViewById<TextView>(R.id.question_number_text_view)
        questionImageView = findViewById<ImageView>(R.id.flag_image_view)
        questionHintTextView = findViewById<TextView>(R.id.question_hint)

        progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressTextView = findViewById<TextView>(R.id.option_a_text_view)

        optionOneTextView = findViewById<TextView>(R.id.option_one)
        optionTwoTextView = findViewById<TextView>(R.id.option_two)
        optionThreeTextView = findViewById<TextView>(R.id.option_three)
        optionFourTextView = findViewById<TextView>(R.id.option_four)

        buttonSubmit = findViewById<Button>(R.id.submit_button)

        usernameTextView = findViewById<TextView>(R.id.username)

        optionOneTextView.setOnClickListener { onClickOption(it) }
        optionTwoTextView.setOnClickListener { onClickOption(it) }
        optionThreeTextView.setOnClickListener { onClickOption(it) }
        optionFourTextView.setOnClickListener { onClickOption(it) }

        // Get the username from the intent and display it in the TextView
        usernameTextView.text =
            getString(R.string.username, intent.extras?.getString("username") ?: "")

        // Initialize the progress bar and text view
        progressBar.max = quizSize
        progressBar.progress = 1
        progressTextView.text =
            getString(R.string.progress_text, progressBar.progress, quizSize)


        buttonSubmit.setOnClickListener {
            buttonSubmit.isEnabled = false
            val optionsViews =
                listOf<TextView>(
                    optionOneTextView, optionTwoTextView,
                    optionThreeTextView, optionFourTextView
                )
            optionsViews.forEach { it.isEnabled = false }
            val selectedOption = currentQuestion.options.firstOrNull { it.selected }
            if (selectedOption == null) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val hasCorrectAnswer = Questions.isCorrect(currentQuestion, selectedOption.country)

            if (!hasCorrectAnswer)
                Toast.makeText(this, "Sorry Wrong Answer :(", Toast.LENGTH_LONG).show()
            else {
                Toast.makeText(this, "Congratulations! You've answered correctly :)", Toast.LENGTH_LONG).show()
                score += 1
            }
            optionsViews.forEach {
                if (it.text == currentQuestion.country.name)
                    it.setBackgroundResource(R.drawable.correct_option_background)
                else
                    it.setBackgroundResource(R.drawable.incorrect_option_background)
            }

            lifecycleScope.launch {
                delay(4000)
                optionsViews.forEach {
                    it.isEnabled = true
                    it.setBackgroundResource(R.drawable.default_option_border_bg)
                }
                buttonSubmit.isEnabled = true
                nextQuestion()
            }
        }

        currentQuestion = quizQuestions
            .elementAtOrNull((progressBar.progress) - 1) ?: throw IllegalStateException("No questions found")
        setupQuestion()
    }

    fun setupQuestion(question: Question = currentQuestion) {
        val options =
            listOf<TextView>(
                optionOneTextView, optionTwoTextView,
                optionThreeTextView, optionFourTextView
            )
        if (options.size != question.options.size)
            throw IllegalArgumentException("Question options must be 4")
        questionTextView.text = question.value
        questionHintTextView.text = question.hint
        questionImageView.setImageResource(question.country.flag)

        options
            .forEachIndexed { index, textView ->
                textView.setBackgroundResource(R.drawable.default_option_border_bg)
                textView.isEnabled = true
                textView.text = question.options[index].country.name
            }
    }

    fun onClickOption(view: View) {
        val options =
            listOf<TextView>(
                optionOneTextView, optionTwoTextView,
                optionThreeTextView, optionFourTextView
            )
        val selectedOption = (view as TextView)
        selectedOption.setBackgroundResource(R.drawable.selected_option_background)
        options
            .filter { it.text != selectedOption.text }
            .forEach { it.setBackgroundResource(R.drawable.default_option_border_bg) }
        currentQuestion.options
            .forEach { it.selected = it.country.name == selectedOption.text.toString() }
    }

    fun nextQuestion() {
        if (progressBar.progress == progressBar.max) {
            endQuiz()
            return
        }
        progressBar.progress += 1
        val nextQuestion = quizQuestions
            .elementAtOrNull((progressBar.progress) - 1)
        currentQuestion = nextQuestion ?: throw IllegalStateException("No questions found")
        progressTextView.text = getString(R.string.progress_text, progressBar.progress, quizSize)
        setupQuestion()
    }

    fun endQuiz() {
        Toast
            .makeText(this, "Congratulations! You have completed the quiz!", Toast.LENGTH_LONG)
            .show()
        val endTime = System.currentTimeMillis()
        val statistic = Statistic(
            username = usernameTextView.text.toString(),
            score = score,
            time = SimpleDateFormat("HH:mm:ss", Locale.GERMAN)
                .format(Date(endTime - startTime)).toString()
        )

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("statistic", statistic)
        startActivity(intent)
        finish()
    }
}