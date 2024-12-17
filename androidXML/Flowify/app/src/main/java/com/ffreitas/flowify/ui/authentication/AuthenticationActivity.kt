package com.ffreitas.flowify.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.OvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ffreitas.flowify.R
import com.ffreitas.flowify.databinding.ActivityAuthenticationBinding
import com.ffreitas.flowify.ui.home.HomeActivity
import com.ffreitas.flowify.ui.signin.SignInActivity
import com.ffreitas.flowify.ui.signup.SignUpActivity
import com.ffreitas.flowify.utils.Constants.SPLASH_SCREEN_DELAY
import com.ffreitas.flowify.utils.ProgressDialog

class AuthenticationActivity : AppCompatActivity(), OnClickListener {

    private var layout: ActivityAuthenticationBinding? = null
    private lateinit var progressDialog: ProgressDialog
    private var keepSplashOnScreen = true
    private val model by viewModels<AuthenticationViewModel> { AuthenticationViewModel.Factory }

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
                            screen.remove()
                            handleAfterSplashScreen()
                        }
                    }
                    scaleDown.start()
                }
            }

        Handler(Looper.getMainLooper())
            .postDelayed({ keepSplashOnScreen = false }, SPLASH_SCREEN_DELAY)

        layout = ActivityAuthenticationBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        progressDialog = ProgressDialog(this)

        ViewCompat.setOnApplyWindowInsetsListener(layout?.root!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layout?.btnSignIn?.setOnClickListener(this)
        layout?.btnSignUp?.setOnClickListener(this)
        model.hasUser.observe(this) { user ->
            progressDialog.dismiss()
            if (user == null) return@observe
            Log.d(TAG, "User found: ${user.email}")
            Intent(this, HomeActivity::class.java)
                .also { startActivity(it) }
        }
    }

    private fun handleAfterSplashScreen() {
        val hasUser = model.hasCurrentUser() ?: return
        Log.d(TAG, "User is already signed in")
        model.getUser(hasUser.email ?: return)
        progressDialog.show()
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
        layout = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "AuthenticationActivity"
    }
}


