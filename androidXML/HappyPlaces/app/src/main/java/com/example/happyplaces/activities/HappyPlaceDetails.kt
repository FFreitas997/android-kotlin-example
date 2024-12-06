package com.example.happyplaces.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.happyplaces.HappyPlaceApplication
import com.example.happyplaces.data.HappyPlaceModel
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailsBinding
import com.example.happyplaces.repository.DefaultHappyPlaceRepository
import com.example.happyplaces.utils.Constants.EXTRA_PLACE_DETAILS
import com.example.happyplaces.utils.HappyPlaceMapper
import kotlinx.coroutines.launch

class HappyPlaceDetails : AppCompatActivity() {

    private var layout: ActivityHappyPlaceDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        layout = ActivityHappyPlaceDetailsBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout?.root!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(layout?.toolbar)

        if (supportActionBar != null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layout
            ?.toolbar
            ?.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val application = application as HappyPlaceApplication
        val repository = DefaultHappyPlaceRepository(application.db.happyPlaceDao())

        if (intent.hasExtra(EXTRA_PLACE_DETAILS)) {
            val itemID = intent.getIntExtra(EXTRA_PLACE_DETAILS, 0)
            lifecycleScope.launch {
                repository
                    .readHappyPlace(itemID)
                    .collect { handleExtraDetails(HappyPlaceMapper.mapEntityToModel(it)) }
            }
        }
    }

    private fun handleExtraDetails(place: HappyPlaceModel){
        val bitmap = BitmapFactory
            .decodeByteArray(place.image, 0, place.image.size)
        layout?.tvDescription?.text = place.description
        supportActionBar?.title = place.title
        layout?.ivPlaceImage?.setImageBitmap(bitmap)
        layout?.tvLocation?.text = place.location
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
    }
}