package com.example.weatherapplication.utils

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.example.weatherapplication.R

object LocationUtils {

    fun hasLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun handleLocationDisabled(context: Context) {
        AlertDialog
            .Builder(context)
            .setTitle(context.getString(R.string.location_disabled))
            .setMessage(context.getString(R.string.please_enable_location_services_to_use_this_app))
            .setCancelable(true)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(context.getString(R.string.go_to_settings)) { dialog, _ ->
                dialog.dismiss()
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .apply { context.startActivity(this) }
            }
            .show()
    }
}