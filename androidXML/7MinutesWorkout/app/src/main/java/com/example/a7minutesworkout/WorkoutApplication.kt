package com.example.a7minutesworkout

import android.app.Application
import android.content.res.Configuration
import com.example.a7minutesworkout.database.HistoryDatabase

class WorkoutApplication : Application() {

    val db by lazy { HistoryDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}