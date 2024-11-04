package com.example.shoppinglist.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.GeocodingResult
import com.example.shoppinglist.data.Location
import com.example.shoppinglist.services.RetrofitClient
import kotlinx.coroutines.launch

class LocationViewModel: ViewModel() {

    private val _location = mutableStateOf<Location?>(null)
    val location: State<Location?> = _location

    private val _address = mutableStateOf(listOf<GeocodingResult>())
    val address: State<List<GeocodingResult>> = _address

    private val geocodingAPI = RetrofitClient.createGeocodingAPI()

    fun updateLocation(location: Location) { _location.value = location }

    fun fetchAddress(latlng: String){
        try {
            viewModelScope.launch {
                val response = geocodingAPI
                    .getAddressFromCoordinates(latlng, "AIzaSyBpcdOF-3pIdjd789HDuiYZ-N-aUOfmraA")
                _address.value = response.results
            }
        }catch (e: Exception){
            Log.d("LocationViewModel", "fetchAddress: ${e.message}")
        }
    }

}