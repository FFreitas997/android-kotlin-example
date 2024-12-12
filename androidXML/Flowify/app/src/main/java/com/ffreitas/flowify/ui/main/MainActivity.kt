package com.ffreitas.flowify.ui.main

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ffreitas.flowify.R
import com.ffreitas.flowify.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var ui: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        installSplashScreen()
            .apply {
                //setKeepOnScreenCondition { !isDestroyed }
                setOnExitAnimationListener {screen ->
                    val zoomX = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_X, 0.5f, 0f)
                    zoomX.interpolator = OvershootInterpolator()
                    zoomX.duration = 500L
                    zoomX.doOnEnd { screen.remove() }

                    val zoomY = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_Y, 0.5f, 0f)
                    zoomY.interpolator = OvershootInterpolator()
                    zoomY.duration = 500L
                    zoomY.doOnEnd { screen.remove() }

                    zoomX.start()
                    zoomY.start()
                }
            }

        ui = ActivityMainBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(ui?.root!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        ui = null
    }
}