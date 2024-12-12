package com.example.weatherapplication.data.network.httpclient

import com.example.weatherapplication.data.network.service.NetworkWeatherService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class RetrofitClient(private val retrofit: Retrofit) {

    fun getWeatherService(): NetworkWeatherService =
        retrofit.create(NetworkWeatherService::class.java)

    companion object {

        private var retrofit: Retrofit? = null

        fun create(baseUrl: String) = RetrofitClient(getClient(baseUrl))

        private fun getClient(baseUrl: String): Retrofit {
            val networkJson = Json { ignoreUnknownKeys = true }

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            return retrofit ?: Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
                .build()
        }
    }
}