package com.example.locationexample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class LocationUtils(val context: Context) {

    private val _fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION

        val checkCoarseLocationPermission = ContextCompat
            .checkSelfPermission(context, fineLocationPermission)

        val checkFineLocationPermission = ContextCompat
            .checkSelfPermission(context, coarseLocationPermission)

        return checkCoarseLocationPermission == PackageManager.PERMISSION_GRANTED &&
                checkFineLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(viewModel: LocationViewModel) {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let {
                    viewModel
                        .setLocation(LocationState.Location(it.latitude, it.longitude))
                }
            }
        }

        val request = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .build()

        _fusedLocationClient
            .requestLocationUpdates(request, callback, Looper.getMainLooper())
    }

    fun reverseGeocodeLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val coordinate = LatLng(latitude, longitude)
        val address: MutableList<Address>? = geocoder
            .getFromLocation(coordinate.latitude, coordinate.longitude, 1)
        return if (address != null && address.isNotEmpty())
            address[0].getAddressLine(0)
        else
            "No address found"
    }
}