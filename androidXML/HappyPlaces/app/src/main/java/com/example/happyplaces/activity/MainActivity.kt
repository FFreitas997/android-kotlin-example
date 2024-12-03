package com.example.happyplaces.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplaces.HappyPlaceApplication
import com.example.happyplaces.adapter.HappyPlacesAdapter
import com.example.happyplaces.database.HappyPlaceEntity
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.repository.DefaultHappyPlaceRepository
import com.example.happyplaces.repository.HappyPlacesRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var layout: ActivityMainBinding? = null
    private lateinit var repository: HappyPlacesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        layout = ActivityMainBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout?.root!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layout?.fab?.setOnClickListener {
            Intent(this, CreateHappyPlaceActivity::class.java)
                .also { startActivity(it) }
        }

        val application = application as HappyPlaceApplication
        repository = DefaultHappyPlaceRepository(application.db.happyPlaceDao())

        lifecycleScope.launch {
            repository
                .getHappyPlaces()
                .collect { handleResultFromDB(it) }
        }
    }

    private fun handleResultFromDB(list: List<HappyPlaceEntity>) {
        layout?.emptyView?.visibility = View.GONE
        layout?.recyclerView?.visibility = View.VISIBLE
        if (list.isNotEmpty()) {
            layout?.recyclerView?.adapter = HappyPlacesAdapter(list)
            layout?.recyclerView?.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            return
        }
        layout?.emptyView?.visibility = View.VISIBLE
        layout?.recyclerView?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
    }
}