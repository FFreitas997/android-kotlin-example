package com.example.a7minutesworkout

import android.os.CountDownTimer
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import java.util.Locale

class CountDownTimer(val startTimer: Long, interval: Long) : CountDownTimer(startTimer, interval) {

    private val tag = CountDownTimer::class.java.simpleName

    private var displayCount: TextView? = null
    private var progressBar: ProgressBar? = null
    private var onFinishCount: () -> Unit = {}
    private var onHalfCountTime: () -> Unit = {}


    override fun onTick(millisUntilFinished: Long) {
        val secondsRemaining = millisUntilFinished / 1000
        progressBar?.progress = secondsRemaining.toInt()
        displayCount?.text = String.format(Locale.getDefault(), "%02d", secondsRemaining)
        Log.d(tag, "onTick: $millisUntilFinished")
        Log.d(tag, "Half: ${startTimer / 2}")
        if (secondsRemaining == ((startTimer / 1000) / 2))
            onHalfCountTime()
        Log.d(tag, "$secondsRemaining second(s) remaining")
    }

    override fun onFinish() {
        Log.d(tag, "Done!!!")
        onFinishCount()
    }

    fun onHalfCountTime(onHalfCountTime: () -> Unit): com.example.a7minutesworkout.CountDownTimer {
        this.onHalfCountTime = onHalfCountTime
        return this
    }

    fun setDisplayCount(displayCount: TextView?): com.example.a7minutesworkout.CountDownTimer {
        this.displayCount = displayCount
        return this
    }

    fun setProgressBar(progressBar: ProgressBar?): com.example.a7minutesworkout.CountDownTimer {
        this.progressBar = progressBar
        return this
    }

    fun onFinishCountDown(onFinishCount: () -> Unit): com.example.a7minutesworkout.CountDownTimer {
        this.onFinishCount = onFinishCount
        return this
    }

}