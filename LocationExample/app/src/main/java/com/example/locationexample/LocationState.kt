package com.example.locationexample

sealed interface LocationState {
    data class Location(val latitude: Double, val longitude: Double): LocationState
    object Error : LocationState
    object Loading : LocationState
    object NoLocation : LocationState
}