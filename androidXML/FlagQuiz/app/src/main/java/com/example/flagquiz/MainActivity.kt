package com.example.flagquiz

import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flagquiz.listeners.ClickListener
import com.example.flagquiz.listeners.TextChangeListener

class MainActivity : AppCompatActivity() {

    var username = ""
    lateinit var usernameEditText: EditText
    lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usernameEditText = findViewById<EditText>(R.id.username)
        startButton = findViewById<Button>(R.id.startButton)

        val watcher = TextChangeListener(this) { onTextChanged(it) }

        usernameEditText.addTextChangedListener(watcher)
        startButton.setOnClickListener(ClickListener(this))
    }

    fun onTextChanged(editable: Editable?) {
        startButton.isEnabled = editable != null && editable.isNotEmpty()
        if (editable == null || editable.isEmpty()) {
            usernameEditText.error = "This field is required"
        } else {
            usernameEditText.error = null
            username = editable.toString()
        }
    }
}