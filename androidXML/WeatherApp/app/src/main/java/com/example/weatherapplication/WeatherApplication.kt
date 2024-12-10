package com.example.weatherapplication

import android.app.Application
import com.example.weatherapplication.data.httpclient.RetrofitClient
import com.example.weatherapplication.utils.Constants

class WeatherApplication: Application() {

    val retrofitClient by lazy { RetrofitClient.create(Constants.BASE_URL) }

}