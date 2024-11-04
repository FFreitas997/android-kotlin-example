package com.example.shoppinglist.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shoppinglist.data.Location
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationSelectionScreen(
    location: Location,
    onLocationSelected: (Location) -> Unit
) {
    var userLocation by remember { mutableStateOf(LatLng(location.latitude, location.longitude)) }

    var cameraPositionState =
        rememberCameraPositionState{ position = CameraPosition.fromLatLngZoom(userLocation, 10f) }

    val onClickButton = {
        val newLocation = Location(userLocation.latitude, userLocation.longitude)
        onLocationSelected(newLocation)
    }

    Column(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.weight(1f).padding(top = 16.dp),
            cameraPositionState = cameraPositionState,
            onMapClick = { userLocation = it }
        ){
            Marker(state = MarkerState(position = userLocation))
        }

        Button(onClick = { onClickButton() }) { Text("Add Location") }
    }
}