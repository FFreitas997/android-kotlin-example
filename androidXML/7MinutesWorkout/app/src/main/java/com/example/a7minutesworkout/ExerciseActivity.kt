package com.example.a7minutesworkout

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkout.adapter.ExerciseStatusAdapter
import com.example.a7minutesworkout.constants.Constants
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding
import com.example.a7minutesworkout.model.exercise.Exercise
import com.example.a7minutesworkout.model.exercise.ExerciseModel
import com.example.a7minutesworkout.model.exercise.ExerciseStatus
import com.example.a7minutesworkout.util.BackPressedCallback
import com.example.a7minutesworkout.util.CountDownTimer
import com.example.a7minutesworkout.util.TextSpeech
import java.util.Locale
import kotlin.String

class ExerciseActivity : AppCompatActivity() {

    private var binding: ActivityExerciseBinding? = null

    private var currentExerciseIndex: Int = 0

    private lateinit var model: ExerciseModel
    private lateinit var textSpeech: TextSpeech
    private lateinit var player: MediaPlayer
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var adapter: ExerciseStatusAdapter


    private val startTimeReady: Long = Constants.READY_TIMER
    private val startTimeExercise: Long = Constants.EXERCISE_TIMER


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityExerciseBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding?.toolbar)

        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        onBackPressedDispatcher
            .addCallback(this, BackPressedCallback(this))

        binding
            ?.toolbar
            ?.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        model = ExerciseModel.create(Constants.NUMBER_OF_EXERCISES)
        textSpeech = TextSpeech.create(this)

        adapter = ExerciseStatusAdapter(model.getExercises())
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvExerciseStatus?.adapter = adapter

        val soundURI = Uri.parse(Constants.PRESS_START_URI)
        player = MediaPlayer.create(applicationContext, soundURI)
        player.isLooping = false

        binding?.progressBar?.max = startTimeReady.toInt()
        binding?.progressBar?.progress = startTimeReady.toInt()
        binding?.tvTimer?.text = String.format(Locale.getDefault(), "%02d", startTimeReady)

        if (model.getExerciseCount() != 0) {
            val nextExerciseName = model
                .getExerciseAt(currentExerciseIndex)
                .name
            binding?.upcomingExerciseReady?.text = getString(nextExerciseName)
        }

        countDownTimer = CountDownTimer(startTimeReady * 1000L, 1000)
            .onTickTime { onTickTimeReady(it) }
            .onFinishCountDown { onFinishReadyCountDown() }

        countDownTimer.start()
    }

    private fun onTickTimeReady(seconds: Long) {
        binding?.progressBar?.progress = seconds.toInt()
        binding?.tvTimer?.text = String.format(Locale.getDefault(), "%02d", seconds)
    }

    fun onFinishReadyCountDown() {
        if (model.getExerciseCount() == 0) {
            onFinishWorkout()
            return
        }
        binding?.frameLayoutReady?.visibility = View.GONE
        binding?.readyTitle?.visibility = View.GONE
        binding?.upcomingExerciseReadyTitle?.visibility = View.GONE
        binding?.upcomingExerciseReady?.visibility = View.GONE
        binding?.frameLayoutExercise?.visibility = View.VISIBLE
        binding?.exerciseName?.visibility = View.VISIBLE
        binding?.exerciseImage?.visibility = View.VISIBLE
        binding?.rvExerciseStatus?.visibility = View.VISIBLE
        onNextExercise(model.getExerciseAt(currentExerciseIndex))
    }

    fun onNextExercise(exercise: Exercise) {
        onChangeStatus(currentExerciseIndex, ExerciseStatus.IN_PROGRESS)
        binding?.upcomingExerciseLl?.visibility = View.INVISIBLE
        binding?.exerciseName?.text = getString(exercise.name)
        binding?.exerciseImage?.setImageResource(exercise.image)
        binding?.progressBarExercise?.max = startTimeExercise.toInt()
        binding?.progressBarExercise?.progress = startTimeExercise.toInt()
        binding?.tvTimerExercise?.text =
            String.format(Locale.getDefault(), "%02d", startTimeExercise)
        countDownTimer.cancel()
        countDownTimer =
            CountDownTimer(startTimeExercise * 1000L, 1000)
                .onTickTime { onTickTimeExercise(it) }
                .onHalfCountTime { showNextExercise() }
                .onFinishCountDown { onFinishExercise() }
        countDownTimer.start()
        player.start()
        textSpeech.speakOut(getString(exercise.name))
    }

    private fun onTickTimeExercise(seconds: Long) {
        binding?.progressBarExercise?.progress = seconds.toInt()
        binding?.tvTimerExercise?.text = String.format(Locale.getDefault(), "%02d", seconds)
    }

    fun onFinishExercise() {
        onChangeStatus(currentExerciseIndex, ExerciseStatus.COMPLETED)
        currentExerciseIndex++
        if (currentExerciseIndex == model.getExerciseCount()) {
            textSpeech.speakOut(getString(R.string.congratulations))
            onFinishWorkout()
            return
        }
        onNextExercise(model.getExerciseAt(currentExerciseIndex))
    }

    fun showNextExercise() {
        if ((currentExerciseIndex + 1) >= model.getExerciseCount())
            return
        val exercise = model.getExerciseAt(currentExerciseIndex + 1)
        binding?.upcomingExerciseLl?.visibility = View.VISIBLE
        binding?.upcomingExercise?.text = getString(exercise.name)
        textSpeech.speakOut("${getString(R.string.upcoming_exercise)} ${exercise.name}")
    }

    fun onChangeStatus(position: Int, status: ExerciseStatus) {
        model.setStatusAt(position, status)
        adapter.notifyItemChanged(position)
    }

    fun onFinishWorkout() {
        Intent(this, FinishActivity::class.java)
            .also { startActivity(it); finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
        textSpeech.shutdown()
        player.stop()
        binding = null
    }
}