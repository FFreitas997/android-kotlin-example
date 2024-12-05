package com.example.happyplaces.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.happyplaces.HappyPlaceApplication
import com.example.happyplaces.data.ImageType
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailsBinding
import com.example.happyplaces.repository.DefaultHappyPlaceRepository
import com.example.happyplaces.utils.Constants.EXTRA_PLACE_DETAILS
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

        val itemID = intent.getIntExtra(EXTRA_PLACE_DETAILS, -1)

        val application = application as HappyPlaceApplication
        val repository = DefaultHappyPlaceRepository(application.db.happyPlaceDao())

        lifecycleScope.launch {
            repository
                .readHappyPlace(itemID)
                .collect { place ->
                    val bitmap = BitmapFactory
                        .decodeByteArray(place.image, 0, place.image.size)
                    val recordImage =
                        if (ImageType.valueOf(place.imageType) == ImageType.CAMERA)
                            rotateBitmap(bitmap)
                        else bitmap
                    layout?.tvDescription?.text = place.description
                    supportActionBar?.title = place.title
                    layout?.ivPlaceImage?.setImageBitmap(recordImage)
                    layout?.tvLocation?.text = place.location
                }
        }
    }

    private fun rotateBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
    }
}