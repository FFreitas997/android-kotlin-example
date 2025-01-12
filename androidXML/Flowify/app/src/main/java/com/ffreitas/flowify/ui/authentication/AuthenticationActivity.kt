package com.ffreitas.flowify.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.CycleInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ffreitas.flowify.R
import com.ffreitas.flowify.databinding.ActivityAuthenticationBinding
import com.ffreitas.flowify.ui.main.HomeActivity
import com.ffreitas.flowify.ui.authentication.components.signin.SignInActivity
import com.ffreitas.flowify.ui.authentication.components.signup.SignUpActivity
import com.ffreitas.flowify.utils.Constants.SIGN_OUT_EXTRA
import com.ffreitas.flowify.utils.Constants.SPLASH_SCREEN_DELAY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity(), OnClickListener {

    private var _layout: ActivityAuthenticationBinding? = null
    private val layout get() = _layout!!

    private var keepSplashOnScreen = true
    private val model by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val isSignOut = intent.getBooleanExtra(SIGN_OUT_EXTRA, false)

        if (!isSignOut) {
            installSplashScreen().apply {
                setKeepOnScreenCondition { keepSplashOnScreen }
                setOnExitAnimationListener { handleExitAnimationListener(it) }
            }

            Handler(Looper.getMainLooper())
                .apply { postDelayed({ keepSplashOnScreen = false }, SPLASH_SCREEN_DELAY) }
        }

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        _layout = ActivityAuthenticationBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layout.btnSignIn.setOnClickListener(this)
        layout.btnSignUp.setOnClickListener(this)
    }

    private fun handleExitAnimationListener(screen: SplashScreenViewProvider) {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(screen.iconView, View.SCALE_X, 0.5f, 0f),
                ObjectAnimator.ofFloat(screen.iconView, View.SCALE_Y, 0.5f, 0f)
            )
            interpolator = CycleInterpolator(2f)
            duration = SPLASH_SCREEN_DELAY / 2
            doOnEnd { screen.remove(); handleAfterSplashScreen() }
            start()
        }
    }

    private fun handleAfterSplashScreen() {
        model.hasCurrentUser() ?: return
        Intent(this, HomeActivity::class.java)
            .also { startActivity(it) }
    }

    private fun handleSignIn() {
        Intent(this, SignInActivity::class.java)
            .also { startActivity(it) }
    }

    private fun handleSignUp() {
        Intent(this, SignUpActivity::class.java)
            .also { startActivity(it) }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_sign_in -> handleSignIn()
            R.id.btn_sign_up -> handleSignUp()
        }
    }

    override fun onDestroy() {
        _layout = null
        super.onDestroy()
    }
}


