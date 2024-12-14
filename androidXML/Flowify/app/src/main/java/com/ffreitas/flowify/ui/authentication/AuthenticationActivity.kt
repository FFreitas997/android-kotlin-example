package com.ffreitas.flowify.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ffreitas.flowify.R
import com.ffreitas.flowify.databinding.ActivityAuthenticationBinding
import com.ffreitas.flowify.ui.signin.SignInActivity
import com.ffreitas.flowify.ui.signup.SignUpActivity

class AuthenticationActivity : AppCompatActivity(), OnClickListener {

    private var layout: ActivityAuthenticationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        layout = ActivityAuthenticationBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout?.root!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layout?.btnSignIn?.setOnClickListener(this)
        layout?.btnSignUp?.setOnClickListener(this)
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
}


