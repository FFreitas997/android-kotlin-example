package com.example.happyplaces

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.happyplaces.data.MapModel
import com.example.happyplaces.databinding.ActivityMapBinding
import com.example.happyplaces.utils.Constants.EXTRA_PLACE_MAP
import com.example.happyplaces.utils.Constants.MAP_ZOOM
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var layout: ActivityMapBinding? = null
    private var currentModel: MapModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        layout = ActivityMapBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout!!.root) { v, insets ->
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

        if (intent.hasExtra(EXTRA_PLACE_MAP)){
            currentModel = IntentCompat
                .getParcelableExtra(intent, EXTRA_PLACE_MAP, MapModel::class.java)
            supportActionBar?.title = currentModel?.title
        }


        val hasFragment = supportFragmentManager.findFragmentById(R.id.mapView)
        if (hasFragment == null) {
            val mapFragment = SupportMapFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.mapView, mapFragment)
                .commit()
            mapFragment.getMapAsync(this)
        }else if (hasFragment is SupportMapFragment) {
            hasFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        val currentLatLng = currentModel
            ?.let { LatLng(it.latitude, it.longitude) }

        val marker = MarkerOptions()
            .position(currentLatLng ?: return)
            .title(currentModel?.location)

        map.addMarker(marker)

        map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, MAP_ZOOM))
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
    }
}