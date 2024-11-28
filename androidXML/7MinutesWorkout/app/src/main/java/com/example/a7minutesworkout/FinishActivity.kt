package com.example.a7minutesworkout

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.a7minutesworkout.databinding.ActivityFinishBinding
import com.example.a7minutesworkout.repository.DefaultHistoryRepository
import com.example.a7minutesworkout.repository.HistoryRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FinishActivity : AppCompatActivity() {

    private var binding: ActivityFinishBinding? = null
    private lateinit var repository: HistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityFinishBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

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

        val application = application as WorkoutApplication
        repository = DefaultHistoryRepository(application.db.historyDao())

        createHistory()
    }

    private fun createHistory() {
        val c = Calendar.getInstance()
        val datetime = c.time

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = sdf.format(datetime)

        lifecycleScope
            .launch { repository.addHistory(date, "Workout completed!") }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}