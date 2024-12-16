package com.ffreitas.flowify

import android.app.Application
import com.ffreitas.flowify.data.network.FirebaseService

class FlowifyApplication: Application() {

    val firebase by lazy { FirebaseService() }

    override fun onCreate() {
        super.onCreate()
    }
}