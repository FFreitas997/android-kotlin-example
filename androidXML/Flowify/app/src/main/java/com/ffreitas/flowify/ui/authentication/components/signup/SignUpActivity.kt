package com.ffreitas.flowify.ui.authentication.components.signup

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
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.databinding.ActivitySignUpBinding
import com.ffreitas.flowify.ui.home.HomeActivity
import com.ffreitas.flowify.utils.BackPressedCallback
import com.ffreitas.flowify.utils.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var _layout: ActivitySignUpBinding? = null
    private val layout get() = _layout!!
    private val model: SignUpViewModel by viewModels { SignUpViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        _layout = ActivitySignUpBinding
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
            .inputName
            .doAfterTextChanged {
                layout.inputLayoutName.error = null
                model.onNameChanged(it)
            }

        layout
            .inputEmail
            .doAfterTextChanged {
                layout.inputLayoutEmail.error = null
                model.onEmailChanged(it)
            }

        layout
            .inputPassword
            .doAfterTextChanged {
                layout.inputLayoutPassword.error = null
                model.onPasswordChanged(it)
            }

        layout
            .buttonSignUp
            .setOnClickListener { onClickSubmit() }

        handleUIState()
    }

    private fun handleUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.uiState.collect { state ->
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

    private fun handleSubmitSuccess(user: User) {
        Log.d(TAG, "sign-up success: ${user.id}")
        firebaseAnalytics
            .logEvent(FirebaseAnalytics.Event.SIGN_UP) {
                param(FirebaseAnalytics.Param.METHOD, "email")
                param(FirebaseAnalytics.Param.CONTENT, "user: ${user.id}")
            }
        Intent(this, HomeActivity::class.java)
            .also { startActivity(it) }
        finish()
    }

    private fun handleSubmitError(message: String) {
        Log.e(TAG, "sign-up error: $message")
        firebaseAnalytics
            .logEvent(FirebaseAnalytics.Event.SIGN_UP) {
                param(FirebaseAnalytics.Param.CONTENT_TYPE, "error")
                param(FirebaseAnalytics.Param.CONTENT, message)
            }
        handleErrorMessage(R.string.signup_screen_submit_error)
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

        if (!model.isNameValid()) {
            layout.inputLayoutName.error = getString(R.string.signup_screen_name_invalid)
            isValid = false
        }

        if (!model.isEmailValid()) {
            layout.inputLayoutEmail.error = getString(R.string.signup_screen_email_invalid)
            isValid = false
        }

        if (!model.isPasswordValid()) {
            layout.inputLayoutPassword.error = getString(R.string.signup_screen_password_invalid)
            isValid = false
        }

        return isValid
    }

    private fun onClickSubmit() {
        Log.d(TAG, "submit clicked")
        if (!hasFormValid()) return
        model.signUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _layout = null
    }

    companion object {
        private const val TAG = "SignUpActivity"
    }
}