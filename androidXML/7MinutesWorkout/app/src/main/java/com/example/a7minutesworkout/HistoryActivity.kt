package com.example.a7minutesworkout

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkout.adapter.HistoryAdapter
import com.example.a7minutesworkout.database.HistoryEntity
import com.example.a7minutesworkout.databinding.ActivityHistoryBinding
import com.example.a7minutesworkout.repository.DefaultHistoryRepository
import com.example.a7minutesworkout.repository.HistoryRepository
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private var binding: ActivityHistoryBinding? = null
    private lateinit var repository: HistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityHistoryBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding?.toolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = getString(R.string.toolbar_title_history_screen)
        }

        binding
            ?.toolbar
            ?.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val application = application as WorkoutApplication
        repository = DefaultHistoryRepository(application.db.historyDao())

        getAllHistory { handleDBResult(it) }
    }

    private fun getAllHistory(handleResult: (List<HistoryEntity>) -> Unit) =
        lifecycleScope.launch {
            repository
                .getAllHistory()
                .collect { handleResult(it) }
        }

    private fun handleDBResult(list: List<HistoryEntity>) {
        binding?.noDataText?.visibility = View.GONE
        binding?.recyclerView?.visibility = View.VISIBLE
        if (list.isNotEmpty()) {
            binding?.recyclerView?.adapter = HistoryAdapter(list)
            binding?.recyclerView?.layoutManager =
                LinearLayoutManager(this@HistoryActivity, LinearLayoutManager.VERTICAL, false)
            return
        }
        binding?.noDataText?.visibility = View.VISIBLE
        binding?.recyclerView?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}