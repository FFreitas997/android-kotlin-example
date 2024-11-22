package com.example.a7minutesworkout

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding
import com.example.a7minutesworkout.model.Exercise
import java.util.Locale
import kotlin.String

class ExerciseActivity : AppCompatActivity() {

    private var countDownTimer: CountDownTimer? = null
    private var binding: ActivityExerciseBinding? = null

    private var currentExerciseIndex: Int = 0

    private var exerciseList = Constants.listOfExercises.shuffled()
    private var startTimeReady: Long = Constants.READY_TIMER
    private var startTimeExercise: Long = Constants.EXERCISE_TIMER


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityExerciseBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        //setContentView(R.layout.activity_exercise)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding?.toolbar)

        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding
            ?.toolbar
            ?.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        countDownTimer = CountDownTimer(startTimeReady * 1000L, 1000)

        binding?.progressBar?.max = startTimeReady.toInt()
        binding?.progressBar?.progress = startTimeReady.toInt()
        binding?.tvTimer?.text = String.format(Locale.getDefault(), "%02d", startTimeReady)

        if (exerciseList.isNotEmpty())
            binding?.upcomingExerciseReady?.text = exerciseList[currentExerciseIndex].name

        countDownTimer
            ?.setDisplayCount(binding?.tvTimer)
            ?.setProgressBar(binding?.progressBar)
            ?.onFinishCountDown { onFinishReadyCountDown() }

        countDownTimer?.start()
    }

    fun onFinishReadyCountDown() {
        binding?.frameLayoutReady?.visibility = View.GONE
        binding?.readyTitle?.visibility = View.GONE
        binding?.frameLayoutExercise?.visibility = View.VISIBLE
        binding?.exerciseName?.visibility = View.VISIBLE
        binding?.exerciseImage?.visibility = View.VISIBLE

        if (exerciseList.isEmpty()) {
            finish()
            return
        }
        val exercise = exerciseList[currentExerciseIndex]
        onNextExercise(exercise)
    }

    fun onNextExercise(exercise: Exercise) {
        binding?.upcomingExerciseReadyTitle?.visibility = View.GONE
        binding?.upcomingExerciseReady?.visibility = View.GONE
        binding?.upcomingExerciseLl?.visibility = View.INVISIBLE
        binding?.exerciseName?.text = exercise.name
        binding?.exerciseImage?.setImageResource(exercise.image)
        binding?.progressBarExercise?.max = startTimeExercise.toInt()
        binding?.progressBarExercise?.progress = startTimeExercise.toInt()
        binding?.tvTimerExercise?.text =
            String.format(Locale.getDefault(), "%02d", startTimeExercise)
        countDownTimer?.cancel()
        countDownTimer = CountDownTimer(startTimeExercise * 1000L, 1000)
        countDownTimer
            ?.setDisplayCount(binding?.tvTimerExercise)
            ?.setProgressBar(binding?.progressBarExercise)
            ?.onFinishCountDown { onFinishExercise() }
            ?.onHalfCountTime { showNextExercise() }
        countDownTimer?.start()
    }

    fun onFinishExercise() {
        currentExerciseIndex++
        if (currentExerciseIndex >= exerciseList.size) {
            finish()
            return
        }
        val exercise = exerciseList[currentExerciseIndex]
        onNextExercise(exercise)
    }

    fun showNextExercise() {
        Log.d("ExerciseActivity", "showNextExercise")
        if ((currentExerciseIndex + 1) >= exerciseList.size)
            return
        Log.d("ExerciseActivity", "showNextExercise")
        val exercise = exerciseList[(currentExerciseIndex + 1)]
        binding?.upcomingExerciseLl?.visibility = View.VISIBLE
        binding?.upcomingExercise?.text = exercise.name
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ExerciseActivity", "onDestroy")
        countDownTimer?.cancel()
        binding?.progressBar?.progress = 0
        binding = null
    }
}