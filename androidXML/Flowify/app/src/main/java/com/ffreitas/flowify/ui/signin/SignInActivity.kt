package com.ffreitas.flowify.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ffreitas.flowify.R
import com.ffreitas.flowify.databinding.ActivitySignInBinding
import com.ffreitas.flowify.ui.home.HomeActivity
import com.ffreitas.flowify.utils.BackPressedCallback
import com.ffreitas.flowify.utils.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var _layout: ActivitySignInBinding? = null
    private val layout get() = _layout!!

    private val model: SignInViewModel by viewModels { SignInViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        _layout = ActivitySignInBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        firebaseAnalytics = Firebase.analytics
        progressDialog = ProgressDialog(this)

        ViewCompat.setOnApplyWindowInsetsListener(layout.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(layout.toolbar)

        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        onBackPressedDispatcher
            .addCallback(this, BackPressedCallback(this))

        layout
            .toolbar
            .setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        layout
            .inputEmail
            .doAfterTextChanged {
                layout.inputEmailLayout.error = null
                model.onEmailChanged(it)
            }

        layout.inputPassword
            .doAfterTextChanged {
                layout.inputPasswordLayout.error = null
                model.onPasswordChanged(it)
            }

        layout.buttonSignIn.setOnClickListener { onClickSubmit() }

        handleUIState()
    }

    private fun handleUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->
                    when (state) {
                        is UIState.Loading -> progressDialog.show()
                        is UIState.Success -> {
                            progressDialog.dismiss()
                            handleSubmitSuccess(state.user)
                        }

                        is UIState.Error -> {
                            progressDialog.dismiss()
                            handleSubmitError(state.message)
                        }

                        else -> {
                            progressDialog.dismiss()
                            Log.d(TAG, "State not handled")
                        }
                    }
                }
            }
        }
    }

    private fun handleSubmitSuccess(user: FirebaseUser) {
        Log.d(TAG, "sign-in success: ${user.uid}")
        firebaseAnalytics
            .logEvent(FirebaseAnalytics.Event.LOGIN) {
                param(FirebaseAnalytics.Param.METHOD, "email")
                param(FirebaseAnalytics.Param.CONTENT, "user: ${user.uid}")
            }
        Intent(this, HomeActivity::class.java)
            .also { startActivity(it) }
        finish()
    }

    private fun handleSubmitError(message: String) {
        Log.e(TAG, "sign-in error: $message")
        firebaseAnalytics
            .logEvent(FirebaseAnalytics.Event.LOGIN) {
                param(FirebaseAnalytics.Param.CONTENT_TYPE, "error")
                param(FirebaseAnalytics.Param.CONTENT, message)
            }
        handleErrorMessage(R.string.signin_screen_submit_error)
    }

    private fun handleErrorMessage(@StringRes message: Int) {
        Snackbar
            .make(layout.root, getString(message), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(R.color.md_theme_error, null))
            .setTextColor(resources.getColor(R.color.md_theme_onError, null))
            .show()
    }

    private fun hasFormValid(): Boolean {
        var isValid = true

        if (!model.isEmailValid()) {
            layout.inputEmailLayout.error = getString(R.string.signin_screen_email_invalid)
            isValid = false
        }

        if (!model.isPasswordValid()) {
            layout.inputPasswordLayout.error = getString(R.string.signin_screen_password_invalid)
            isValid = false
        }

        return isValid
    }

    private fun onClickSubmit() {
        Log.d(TAG, "submit clicked")
        if (!hasFormValid()) return
        model.signIn()
    }

    override fun onDestroy() {
        super.onDestroy()
        _layout = null
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}