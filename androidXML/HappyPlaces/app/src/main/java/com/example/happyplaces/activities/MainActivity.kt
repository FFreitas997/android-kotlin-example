package com.example.happyplaces.activities

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
import com.example.happyplaces.adapter.OnItemClickListener
import com.example.happyplaces.data.HappyPlaceModel
import com.example.happyplaces.data.ImageType
import com.example.happyplaces.database.HappyPlaceEntity
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.repository.DefaultHappyPlaceRepository
import com.example.happyplaces.repository.HappyPlacesRepository
import com.example.happyplaces.utils.Constants.EXTRA_PLACE_DETAILS
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
                .readHappyPlaces()
                .collect { handleResultFromDB(it) }
        }
    }

    private fun handleResultFromDB(list: List<HappyPlaceEntity>) {
        layout?.emptyView?.visibility = View.GONE
        layout?.recyclerView?.visibility = View.VISIBLE
        if (list.isEmpty()) {
            layout?.emptyView?.visibility = View.VISIBLE
            layout?.recyclerView?.visibility = View.GONE
            return
        }
        val adapter = HappyPlacesAdapter(list)
        layout?.recyclerView?.adapter = adapter
        layout?.recyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onClick(position: Int) {
                if (position < 0 || list.isEmpty()) return
                val record = list[position].let {
                    HappyPlaceModel(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        date = it.date,
                        location = it.location,
                        image = it.image,
                        imageType = ImageType.valueOf(it.imageType),
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }
                Intent(this@MainActivity, HappyPlaceDetails::class.java)
                    .also { it.putExtra(EXTRA_PLACE_DETAILS, record.id ?: -1) }
                    .also { startActivity(it) }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
    }
}