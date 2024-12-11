package com.example.weatherapplication.ui.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.Manifest.permission.ACCESS_WIFI_STATE
import android.Manifest.permission.INTERNET
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.weatherapplication.WeatherApplication
import com.example.weatherapplication.data.repository.DefaultWeatherRepository
import com.example.weatherapplication.data.repository.WeatherRepository
import com.example.weatherapplication.databinding.ActivityMainBinding
import com.example.weatherapplication.utils.LocationUtils
import com.example.weatherapplication.utils.NetworkUtils
import com.example.weatherapplication.utils.PermissionHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var layout: ActivityMainBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var repository: WeatherRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        layout = ActivityMainBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(layout?.root!!) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices
            .getFusedLocationProviderClient(this)

        val app = application as WeatherApplication
        repository = DefaultWeatherRepository(app.retrofitClient.getWeatherService())

        PermissionHelper(this)
            .setOnSuccessListener { requestLocationData() }
            .apply { requestPermission(PERMISSIONS) }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        if (!LocationUtils.hasLocationEnabled(this)) {
            LocationUtils.handleLocationDisabled(this)
            return
        }

        val request = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMaxUpdates(1)
            .build()

        val locationCallback = object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                requestWeatherData(location.latitude, location.longitude)
            }
        }

        fusedLocationClient
            .requestLocationUpdates(request, locationCallback, Looper.myLooper())
    }

    private fun requestWeatherData(lat: Double, lon: Double) {
        if (!NetworkUtils.hasNetworkConnection(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = repository.getWeatherData(lat, lon)
                Toast.makeText(this@MainActivity, response.toString(), Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private val PERMISSIONS =
            arrayOf(ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION, INTERNET,
            ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE)
    }
}