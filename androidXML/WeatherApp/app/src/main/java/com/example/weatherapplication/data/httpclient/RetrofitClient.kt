package com.example.weatherapplication.data.httpclient

import com.example.weatherapplication.data.service.NetworkWeatherService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient(private val retrofit: Retrofit) {

    fun getWeatherService(): NetworkWeatherService {
        return retrofit.create(NetworkWeatherService::class.java)
    }

    companion object {

        private var retrofit: Retrofit? = null

        fun create(baseUrl: String) = RetrofitClient(getClient(baseUrl))

        private fun getClient(baseUrl: String): Retrofit {
            return retrofit ?: Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}