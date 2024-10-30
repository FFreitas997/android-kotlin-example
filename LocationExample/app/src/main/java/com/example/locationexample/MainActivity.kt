package com.example.locationexample

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationexample.ui.theme.LocationExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocationExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationDisplay(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

fun onResultFromRequestPermission(context: Context, permissions: Map<String, Boolean>) {
    val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
    val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    if (permissions[coarseLocationPermission] == false || permissions[fineLocationPermission] == false){

        val shouldRequestPermissionForFineLocation = ActivityCompat
                .shouldShowRequestPermissionRationale(context as MainActivity, fineLocationPermission)

        val shouldRequestPermissionForCoarseLocation = ActivityCompat
                        .shouldShowRequestPermissionRationale(context, coarseLocationPermission)

        if (shouldRequestPermissionForFineLocation || shouldRequestPermissionForCoarseLocation)
            Toast
                .makeText(context, "Location permission required", Toast.LENGTH_SHORT)
                .show()
        else
            Toast
                .makeText(context, "Location permission denied, please enable it in the settings", Toast.LENGTH_SHORT)
                .show()
        return
    }
    Toast
        .makeText(context, "Location permission granted", Toast.LENGTH_SHORT)
        .show()
}

@Composable
fun LocationDisplay(
    modifier: Modifier = Modifier,
    viewModel: LocationViewModel = viewModel(),
    context: Context = LocalContext.current,
) {
    val locationUtils = LocationUtils(context)

    val state = viewModel.state.value

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            onResultFromRequestPermission(context, permissions)
        }
    )

    val onClickRequestLocation = { viewModel: LocationViewModel ->
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION

        if (locationUtils.hasLocationPermission()) {
            viewModel.setLoading()
            locationUtils.requestLocationUpdates(viewModel)
        } else
            requestPermissionLauncher
                .launch(arrayOf(coarseLocationPermission, fineLocationPermission))
    }

    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {

        when (state) {
            is LocationState.Location -> {
                val address = locationUtils.reverseGeocodeLocation(state.latitude, state.longitude)
                Text(text = "Latitude: ${state.latitude}, Longitude: ${state.longitude}")
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = "Address: $address", modifier = Modifier.padding(start = 32.dp, end = 32.dp))
            }
            is LocationState.Error -> {
                Text(text = "Error getting location", modifier = Modifier.padding(16.dp))
            }
            is LocationState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(CenterHorizontally))
            }
            else -> {
                Text(text = "No location available")
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))

        if (state !is LocationState.Loading)
            Button(onClick = { onClickRequestLocation(viewModel) }) {
                Text(text = "Request Location")
            }
    }
}