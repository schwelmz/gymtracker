package com.example.gymtracker.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit


object RetrofitClient {
    private const val BASE_URL = "https://world.openfoodfacts.org/"

    private val json = Json {
        ignoreUnknownKeys = true // Important: The API has many fields we don't use
    }

    val instance: OpenFoodFactsApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(OpenFoodFactsApiService::class.java)
    }
}