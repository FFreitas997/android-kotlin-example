package com.example.a7minutesworkout

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a7minutesworkout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding
            ?.startButtonFrameLayout
            ?.setOnClickListener {
                val intent = Intent(this, ExerciseActivity::class.java)
                startActivity(intent)
            }

        binding
            ?.bmiButtonFrameLayout
            ?.setOnClickListener {
                val intent = Intent(this, BMIActivity::class.java)
                startActivity(intent)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}