package com.ffreitas.flowify.ui.signin

import android.os.Bundle
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
import com.ffreitas.flowify.databinding.ActivitySignInBinding
import com.ffreitas.flowify.utils.BackPressedCallback
import com.ffreitas.flowify.utils.ProgressDialog

class SignInActivity : AppCompatActivity(), OnClickListener {

    private var layout: ActivitySignInBinding? = null
    private lateinit var progressDialog: ProgressDialog
    private val model: SignInViewModel by viewModels { SignInViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        layout = ActivitySignInBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

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

        layout!!.inputEmail
            .doOnTextChanged { text, _, _, _ -> handleEmailChanged(text.toString()) }

        layout!!.inputPassword
            .doOnTextChanged { text, _, _, _ ->  handlePasswordChanged(text.toString()) }

        layout!!.buttonSignIn.setOnClickListener(this)

        model.hasSignInSuccess.observe(this) { success ->
            progressDialog.dismiss()
            if (success) onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun handleEmailChanged(email: String) {
        model.onEmailChanged(email)

        layout!!.inputEmail.error =
            if (model.emailIsValid())
                null
            else
                getString(R.string.signin_screen_email_invalid)
    }

    private fun handlePasswordChanged(password: String) {
        model.onPasswordChanged(password)

        layout!!.inputPassword.error =
            if (model.passwordIsValid())
                null
            else
                getString(R.string.signin_screen_password_invalid)
    }

    private fun handleErrorMessage(@StringRes message: Int) {
        Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_sign_in -> {
                if (!model.emailIsValid() || !model.passwordIsValid()) {
                    handleErrorMessage(R.string.signin_screen_submit_error)
                    return
                }
                progressDialog.show()
                model.signIn()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
    }
}