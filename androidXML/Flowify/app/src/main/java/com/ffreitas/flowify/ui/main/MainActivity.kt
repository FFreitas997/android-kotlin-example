package com.ffreitas.flowify.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ffreitas.flowify.databinding.ActivityMainBinding
import com.ffreitas.flowify.ui.authentication.AuthenticationActivity
import com.ffreitas.flowify.utils.Constants.SPLASH_SCREEN_DELAY

class MainActivity : AppCompatActivity() {

    private var ui: ActivityMainBinding? = null
    private var keepSplashOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        installSplashScreen()
            .apply {
                setKeepOnScreenCondition { keepSplashOnScreen }
                setOnExitAnimationListener { screen ->
                    val scaleDown = AnimatorSet().apply {
                        playTogether(
                            ObjectAnimator.ofFloat(screen.iconView, View.SCALE_X, 0.5f, 0f),
                            ObjectAnimator.ofFloat(screen.iconView, View.SCALE_Y, 0.5f, 0f)
                        )
                        interpolator = OvershootInterpolator()
                        duration = 500L
                        doOnEnd {
                            val intent = Intent(this@MainActivity, AuthenticationActivity::class.java)
                            this@MainActivity.startActivity(intent)
                            this@MainActivity.finish()
                            screen.remove()
                        }
                    }
                    scaleDown.start()
                }
            }

        Handler(Looper.getMainLooper())
            .postDelayed({ keepSplashOnScreen = false }, SPLASH_SCREEN_DELAY)

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