package com.example.a7minutesworkout

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a7minutesworkout.constants.Constants
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding
import com.example.a7minutesworkout.model.Exercise
import com.example.a7minutesworkout.model.ExerciseModel
import com.example.a7minutesworkout.util.CountDownTimer
import com.example.a7minutesworkout.util.TextSpeech
import java.util.Locale
import kotlin.String

class ExerciseActivity : AppCompatActivity() {

    private var countDownTimer: CountDownTimer? = null
    private var binding: ActivityExerciseBinding? = null

    private var currentExerciseIndex: Int = 0

    private lateinit var exerciseModel: ExerciseModel
    private lateinit var textSpeech: TextSpeech
    private lateinit var player: MediaPlayer


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
        exerciseModel = ExerciseModel.create()
        textSpeech = TextSpeech.create(this)

        val soundURI = Uri.parse(Constants.PRESS_START_URI)
        player = MediaPlayer.create(applicationContext, soundURI)
        player.isLooping = false

        binding?.progressBar?.max = startTimeReady.toInt()
        binding?.progressBar?.progress = startTimeReady.toInt()
        binding?.tvTimer?.text = String.format(Locale.getDefault(), "%02d", startTimeReady)

        if (exerciseModel.getExerciseCount() != 0) {
            val nextExerciseName = exerciseModel
                .getExerciseAt(currentExerciseIndex)
                .name
            binding?.upcomingExerciseReady?.text = getString(nextExerciseName)
            textSpeech.speakOut("${getString(R.string.upcoming_exercise)} $nextExerciseName")
        }


        countDownTimer
            ?.onFinishCountDown { onFinishReadyCountDown() }
            ?.onTickTime { onTickTimeReady(it) }

        countDownTimer?.start()
    }

    private fun onTickTimeReady(seconds: Long) {
        binding?.progressBar?.progress = seconds.toInt()
        binding?.tvTimer?.text = String.format(Locale.getDefault(), "%02d", seconds)
    }

    fun onFinishReadyCountDown() {
        binding?.frameLayoutReady?.visibility = View.GONE
        binding?.readyTitle?.visibility = View.GONE
        binding?.frameLayoutExercise?.visibility = View.VISIBLE
        binding?.exerciseName?.visibility = View.VISIBLE
        binding?.exerciseImage?.visibility = View.VISIBLE
        if (exerciseModel.getExerciseCount() == 0) {
            finish()
            return
        }
        onNextExercise(exerciseModel.getExerciseAt(currentExerciseIndex))
    }

    fun onNextExercise(exercise: Exercise) {
        binding?.upcomingExerciseReadyTitle?.visibility = View.GONE
        binding?.upcomingExerciseReady?.visibility = View.GONE
        binding?.upcomingExerciseLl?.visibility = View.INVISIBLE
        binding?.exerciseName?.text = getString(exercise.name)
        binding?.exerciseImage?.setImageResource(exercise.image)
        binding?.progressBarExercise?.max = startTimeExercise.toInt()
        binding?.progressBarExercise?.progress = startTimeExercise.toInt()
        binding?.tvTimerExercise?.text =
            String.format(Locale.getDefault(), "%02d", startTimeExercise)
        countDownTimer?.cancel()
        countDownTimer = CountDownTimer(startTimeExercise * 1000L, 1000)
        countDownTimer
            ?.onFinishCountDown { onFinishExercise() }
            ?.onTickTime { onTickTimeExercise(it) }
            ?.onHalfCountTime { showNextExercise() }
        countDownTimer?.start()
        player.start()
        textSpeech.speakOut(getString(exercise.name))
    }

    private fun onTickTimeExercise(seconds: Long) {
        binding?.progressBarExercise?.progress = seconds.toInt()
        binding?.tvTimerExercise?.text = String.format(Locale.getDefault(), "%02d", seconds)
    }

    fun onFinishExercise() {
        currentExerciseIndex++
        if (currentExerciseIndex == exerciseModel.getExerciseCount()) {
            textSpeech.speakOut(getString(R.string.congratulations))
            AlertDialog
                .Builder(this)
                .setTitle(getString(R.string.congratulations_title))
                .setMessage(getString(R.string.congratulations))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { _, _ -> finish() }
                .show()
            return
        }
        onNextExercise(exerciseModel.getExerciseAt(currentExerciseIndex))
    }

    fun showNextExercise() {
        if ((currentExerciseIndex + 1) >= exerciseModel.getExerciseCount())
            return
        val exercise = exerciseModel.getExerciseAt(currentExerciseIndex + 1)
        binding?.upcomingExerciseLl?.visibility = View.VISIBLE
        binding?.upcomingExercise?.text = getString(exercise.name)
        textSpeech.speakOut("${getString(R.string.upcoming_exercise)} ${exercise.name}")
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        textSpeech.shutdown()
        player.stop()
        binding?.progressBar?.progress = 0
        binding = null
    }
}