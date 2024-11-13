package com.example.dobcalc

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val datePickerBtn: Button = findViewById(R.id.datePickerButton)

        datePickerBtn.setOnClickListener(ButtonOnClickListener(this))
    }
}