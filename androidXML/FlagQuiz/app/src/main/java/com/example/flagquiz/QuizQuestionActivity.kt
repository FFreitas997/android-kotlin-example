package com.example.flagquiz

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
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
import com.example.flagquiz.data.Statistic
import com.example.flagquiz.listeners.OnClickNextQuestionListener
import com.example.flagquiz.listeners.OnClickQuizOption
import com.example.flagquiz.utils.Constants
import com.example.flagquiz.utils.Questions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizQuestionActivity : AppCompatActivity() {

    val quizSize = Constants.QUIZ_SIZE
    var score = 0
    val quizQuestions = Questions.getQuestions(quizSize)
    lateinit var currentQuestion: Question

    lateinit var chronometer: Chronometer
    lateinit var questionTextView: TextView
    lateinit var questionHintTextView: TextView
    lateinit var questionImageView: ImageView

    lateinit var progressBar: ProgressBar
    lateinit var progressTextView: TextView

    var options = emptyList<TextView>()

    lateinit var buttonSubmit: Button

    lateinit var usernameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz_question)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        chronometer = findViewById<Chronometer>(R.id.chronometer)
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.format = "Quiz Time: %s"
        chronometer.start()

        questionTextView = findViewById<TextView>(R.id.question_number_text_view)
        questionImageView = findViewById<ImageView>(R.id.flag_image_view)
        questionHintTextView = findViewById<TextView>(R.id.question_hint)
        progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressTextView = findViewById<TextView>(R.id.option_a_text_view)

        options = listOf<TextView>(
            findViewById<TextView>(R.id.option_one),
            findViewById<TextView>(R.id.option_two),
            findViewById<TextView>(R.id.option_three),
            findViewById<TextView>(R.id.option_four)
        )

        options.forEach { it.setOnClickListener(OnClickQuizOption(this)) }

        buttonSubmit = findViewById<Button>(R.id.submit_button)
        usernameTextView = findViewById<TextView>(R.id.username)


        val username = intent.extras?.getString(Constants.USERNAME_KEY) ?: ""
        usernameTextView.text = getString(R.string.username, username)

        // Initialize the progress bar and text view
        progressBar.max = quizSize
        progressBar.progress = 1
        progressTextView.text = getString(R.string.progress_text, progressBar.progress, quizSize)

        val onSubmit = OnClickNextQuestionListener(this) { nextQuestion() }

        buttonSubmit.setOnClickListener(onSubmit)

        currentQuestion = quizQuestions.elementAtOrNull((progressBar.progress) - 1)
            ?: throw IllegalStateException("No questions found")

        setupQuestion()
    }

    fun nextQuestion() {
        buttonSubmit.isEnabled = false
        options.forEach { it.isEnabled = false }
        lifecycleScope.launch {
            delay(5000)
            if (progressBar.progress == progressBar.max) {
                endQuiz()
                return@launch
            }
            progressBar.progress += 1
            progressTextView.text = getString(R.string.progress_text, progressBar.progress, quizSize)
            currentQuestion = quizQuestions.elementAtOrNull((progressBar.progress) - 1)
                ?: throw IllegalStateException("No questions found")
            setupQuestion()
        }
    }

    fun setupQuestion(question: Question = currentQuestion) {
        if (options.size != question.options.size)
            throw IllegalArgumentException("Question options must be 4")
        questionTextView.text = question.value
        questionHintTextView.text = question.hint
        questionImageView.setImageResource(question.country.flag)
        buttonSubmit.isEnabled = true

        options
            .forEachIndexed { index, textView ->
                textView.setBackgroundResource(R.drawable.default_option_border_bg)
                textView.text = question.options[index].country.name
                textView.isEnabled = true
                textView.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
    }

    fun endQuiz() {
        Toast
            .makeText(this, "Congratulations! You have completed the quiz!", Toast.LENGTH_LONG)
            .show()
        chronometer.stop()
        val statistic = Statistic(
            username = usernameTextView.text.toString(),
            score = score,
            time = SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(SystemClock.elapsedRealtime() - chronometer.base)
                .toString(),
            numQuestions = quizSize
        )
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(Constants.STATISTIC_KEY, statistic)
        startActivity(intent)
        finish()
    }
}