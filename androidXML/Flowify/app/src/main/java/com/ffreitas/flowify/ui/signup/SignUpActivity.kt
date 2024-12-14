package com.ffreitas.flowify.ui.signup

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.ffreitas.flowify.R
import com.ffreitas.flowify.databinding.ActivitySignUpBinding
import com.ffreitas.flowify.utils.BackPressedCallback

class SignUpActivity : AppCompatActivity() {

    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = DataBindingUtil
            .setContentView(this, R.layout.activity_sign_up)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)

        onBackPressedDispatcher.addCallback(this, BackPressedCallback(this, true))

        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding
            .toolbar
            .setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}