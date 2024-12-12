package com.example.weatherapplication.ui.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.weatherapplication.R
import com.example.weatherapplication.WeatherApplication
import com.example.weatherapplication.data.network.model.WeatherResponse
import com.example.weatherapplication.data.repository.DefaultWeatherRepository
import com.example.weatherapplication.data.repository.WeatherRepository
import com.example.weatherapplication.databinding.ActivityMainBinding
import com.example.weatherapplication.utils.CommonUtils.getUnit
import com.example.weatherapplication.utils.CommonUtils.unixTime
import com.example.weatherapplication.utils.Constants
import com.example.weatherapplication.utils.Constants.PREFERENCE_NAME
import com.example.weatherapplication.utils.CustomLoadingDialog
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
import kotlinx.serialization.json.Json
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var layout: ActivityMainBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var repository: WeatherRepository
    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var sharedPreferences: SharedPreferences

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

        loadingDialog = CustomLoadingDialog(this)

        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        updateUI()

        val app = application as WeatherApplication
        repository = DefaultWeatherRepository(app.retrofitClient.getWeatherService())

        layout?.toolbar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_refresh -> {
                    requestLocationData()
                    true
                }

                else -> false
            }
        }

        PermissionHelper(this)
            .setOnSuccessListener { requestLocationData() }
            .apply { requestPermission(PERMISSIONS) }
    }

    override fun onDestroy() {
        super.onDestroy()
        layout = null
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        if (!LocationUtils.hasLocationEnabled(this)) {
            LocationUtils.handleLocationDisabled(this)
            return
        }

        loadingDialog.show()

        val request = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMaxUpdates(1)
            .build()

        val locationCallback = object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {
                loadingDialog.dismiss()
                val location = result.lastLocation
                if (location == null) {
                    notifyUser("Unable to get location data")
                    return
                }
                requestWeatherData(location.latitude, location.longitude)
            }
        }

        fusedLocationClient
            .requestLocationUpdates(request, locationCallback, Looper.myLooper())
    }

    private fun requestWeatherData(lat: Double, lon: Double) {
        if (!NetworkUtils.hasNetworkConnection(this)) {
            notifyUser("No internet connection")
            return
        }

        loadingDialog.show()

        lifecycleScope.launch {
            try {
                val response = repository.getWeatherData(lat, lon)
                loadingDialog.dismiss()
                val encoded = Json.encodeToString(WeatherResponse.serializer(), response)
                val editor = sharedPreferences.edit()
                editor.putString(Constants.WEATHER_RESPONSE_DATA, encoded).apply()
                updateUI()
            } catch (e: Exception) {
                loadingDialog.dismiss()
                Log.e(TAG, e.message.toString())
                notifyUser("An error occurred: ${e.message}")
            }
        }
    }

    private fun updateUI() {
        val weatherString = sharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, "")
        if (!weatherString.isNullOrEmpty()) {
            val response = Json.decodeFromString(WeatherResponse.serializer(), weatherString)
            layout?.let { layout ->
                for (i in response.weather.indices) {
                    val weather = response.weather[i]
                    layout.tvMain.text = weather.main
                    layout.tvMainDescription.text = weather.description
                    layout.tvTemp.text = String.format(
                        Locale.getDefault(),
                        response.main.temp.toString() + getUnit(response.sys.country)
                    )
                    layout.tvHumidity.text = String.format(
                        Locale.getDefault(),
                        response.main.humidity.toString() + " per cent"
                    )
                    layout.tvMin.text = String.format(
                        Locale.getDefault(),
                        response.main.tempMin.toString() + " min"
                    )
                    layout.tvMax.text = String.format(
                        Locale.getDefault(),
                        response.main.tempMax.toString() + " max"
                    )
                    layout.tvSpeed.text =
                        String.format(Locale.getDefault(), response.wind.speed.toString())
                    layout.tvName.text = response.name
                    layout.tvCountry.text = response.sys.country
                    layout.tvSunriseTime.text = unixTime(response.sys.sunrise.toLong())
                    layout.tvSunsetTime.text = unixTime(response.sys.sunset.toLong())

                    // Here we update the main icon
                    when (weather.icon) {
                        "01d" -> layout.ivMain.setImageResource(R.drawable.sunny)
                        "02d" -> layout.ivMain.setImageResource(R.drawable.cloud)
                        "03d" -> layout.ivMain.setImageResource(R.drawable.cloud)
                        "04d" -> layout.ivMain.setImageResource(R.drawable.cloud)
                        "04n" -> layout.ivMain.setImageResource(R.drawable.cloud)
                        "10d" -> layout.ivMain.setImageResource(R.drawable.rain)
                        "11d" -> layout.ivMain.setImageResource(R.drawable.storm)
                        "13d" -> layout.ivMain.setImageResource(R.drawable.snowflake)
                        "01n" -> layout.ivMain.setImageResource(R.drawable.cloud)
                        "02n" -> layout.ivMain.setImageResource(R.drawable.cloud)
                        "03n" -> layout.ivMain.setImageResource(R.drawable.cloud)
                        "10n" -> layout.ivMain.setImageResource(R.drawable.cloud)
                        "11n" -> layout.ivMain.setImageResource(R.drawable.rain)
                        "13n" -> layout.ivMain.setImageResource(R.drawable.snowflake)
                    }
                }
            }
        }
    }

    private fun notifyUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val TAG = "MainActivity"
        private val PERMISSIONS = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    }
}