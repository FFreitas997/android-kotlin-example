package com.example.flagquiz

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flagquiz.data.Statistic

class ResultActivity : AppCompatActivity() {

    lateinit var usernameView: TextView
    lateinit var scoreView: TextView
    lateinit var timeView: TextView

    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usernameView = findViewById(R.id.textView1)
        scoreView = findViewById(R.id.textView2)
        timeView = findViewById(R.id.textView3)
        button = findViewById(R.id.button)

        val statistics = IntentCompat
            .getParcelableExtra(intent, "statistic", Statistic::class.java)

        if (statistics == null) {
            finish()
            return
        }

        usernameView.text = statistics.username
        scoreView.text = getString(R.string.score, statistics.score)
        timeView.text = getString(R.string.time, statistics.time)

        button
            .setOnClickListener { finish() }
    }
}