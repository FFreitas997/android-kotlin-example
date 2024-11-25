package com.example.a7minutesworkout.util

import android.os.CountDownTimer

class CountDownTimer(val startTimer: Long, interval: Long) : CountDownTimer(startTimer, interval) {

    private var onFinishCount: () -> Unit = {}
    private var onHalfCountTime: () -> Unit = {}
    private var onTickTime: (Long) -> Unit = {}


    override fun onTick(millisUntilFinished: Long) {
        val secondsRemaining = millisUntilFinished / 1000
        val halfTimeSeconds = (startTimer / 2) / 1000

        onTickTime(secondsRemaining)

        if (secondsRemaining == halfTimeSeconds)
            onHalfCountTime()
    }

    override fun onFinish() { onFinishCount() }

    fun onHalfCountTime(onHalfCountTime: () -> Unit): com.example.a7minutesworkout.util.CountDownTimer {
        this.onHalfCountTime = onHalfCountTime
        return this
    }

    fun onFinishCountDown(onFinishCount: () -> Unit): com.example.a7minutesworkout.util.CountDownTimer {
        this.onFinishCount = onFinishCount
        return this
    }

    fun onTickTime(onTickTime: (Long) -> Unit): com.example.a7minutesworkout.util.CountDownTimer {
        this.onTickTime = onTickTime
        return this
    }

}