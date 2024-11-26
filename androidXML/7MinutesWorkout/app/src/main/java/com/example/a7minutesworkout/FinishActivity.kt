package com.example.a7minutesworkout

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a7minutesworkout.databinding.ActivityFinishBinding

class FinishActivity : AppCompatActivity() {

    private var binding: ActivityFinishBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFinishBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        //setContentView(R.layout.activity_finish)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding?.toolbarFinishActivity)

        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding
            ?.toolbarFinishActivity
            ?.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding
            ?.btnFinish
            ?.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}