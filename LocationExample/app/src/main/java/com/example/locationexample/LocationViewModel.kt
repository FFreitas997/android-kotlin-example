package com.example.locationexample

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    private val _state = mutableStateOf<LocationState>(LocationState.NoLocation)
    val state: State<LocationState> = _state

    fun setLocation(location: LocationState.Location) { _state.value = location }

    fun setLoading() { _state.value = LocationState.Loading }

}