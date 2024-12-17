package com.ffreitas.flowify.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.ffreitas.flowify.R
import com.ffreitas.flowify.databinding.ActivitySignUpBinding
import com.ffreitas.flowify.ui.home.HomeActivity
import com.ffreitas.flowify.utils.BackPressedCallback
import com.ffreitas.flowify.utils.ProgressDialog
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

class SignUpActivity : AppCompatActivity(), OnClickListener {

    private var layout: ActivitySignUpBinding? = null
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val model: SignUpViewModel by viewModels { SignUpViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        layout = ActivitySignUpBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        firebaseAnalytics = Firebase.analytics

        ViewCompat.setOnApplyWindowInsetsListener(layout!!.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressDialog = ProgressDialog(this)

        setSupportActionBar(layout!!.toolbar)

        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        onBackPressedDispatcher
            .addCallback(this, BackPressedCallback(this, true))


        layout!!
            .toolbar
            .setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        layout!!
            .inputName
            .doOnTextChanged { text, _, _, _ -> handleNameChanged(text.toString()) }

        layout!!
            .inputEmail
            .doOnTextChanged { text, _, _, _ -> handleEmailChanged(text.toString()) }

        layout!!
            .inputPassword
            .doOnTextChanged { text, _, _, _ -> handlePasswordChanged(text.toString()) }

        layout!!.buttonSignUp.setOnClickListener(this)

        model.hasSignSuccess.observe(this) { user ->
            progressDialog.dismiss()
            if (user == null) {
                handleErrorMessage(R.string.signup_screen_submit_error)
                return@observe
            }
            Log.d(TAG, "Sign Up Success")
            firebaseAnalytics
                .logEvent(FirebaseAnalytics.Event.SIGN_UP) {
                    param(FirebaseAnalytics.Param.METHOD, "email")
                    param(FirebaseAnalytics.Param.CONTENT, "user: ${user.id}")
                }
            Intent(this, HomeActivity::class.java)
                .also { startActivity(it) }
            finish()
        }
    }

    private fun handleNameChanged(name: String) {
        model.onNameChanged(name.trim())

        layout!!.inputName.error =
            if (model.nameIsValid())
                null
            else
                getString(R.string.signup_screen_name_invalid)
    }

    private fun handleEmailChanged(email: String) {
        model.onEmailChanged(email.trim())

        layout!!.inputEmail.error =
            if (model.emailIsValid())
                null
            else
                getString(R.string.signup_screen_email_invalid)
    }

    private fun handlePasswordChanged(password: String) {
        model.onPasswordChanged(password.trim())

        layout!!.inputPassword.error =
            if (model.passwordIsValid())
                null
            else
                getString(R.string.signup_screen_password_invalid)
    }

    private fun handleErrorMessage(@StringRes message: Int) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_sign_up -> {
                if (!model.nameIsValid() || !model.emailIsValid() || !model.passwordIsValid()) {
                    handleErrorMessage(R.string.signup_screen_submit_error)
                    return
                }
                progressDialog.show()
                model.onSignUp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
    }

    companion object {
        private const val TAG = "SignUpActivity"
    }
}