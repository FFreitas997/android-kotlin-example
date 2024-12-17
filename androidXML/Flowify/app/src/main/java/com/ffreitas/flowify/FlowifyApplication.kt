package com.ffreitas.flowify

import android.app.Application
import com.ffreitas.flowify.data.network.auth.DefaultFirebaseAuthService

class FlowifyApplication: Application() {

    val firebase by lazy { DefaultFirebaseAuthService.create() }

    override fun onCreate() {
        super.onCreate()
    }
}