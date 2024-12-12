package com.example.weatherapplication.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object CommonUtils {


    fun getUnit(value: String): String {
        var unit = "°C"
        if ("US" == value || "LR" == value || "MM" == value) {
            unit = "°F"
        }
        return unit
    }

    fun unixTime(timex: Long): String? {
        val date = Date(timex * 1000L)
        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("HH:mm")
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }
}