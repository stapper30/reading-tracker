package com.example.books

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {
    // Remember: 10.0.2.2 is the "magic" IP to reach your computer's localhost from an emulator
    private const val BASE_URL = "https://dell-propublication-unritually.ngrok-free.dev/"

    private val json = Json {
        ignoreUnknownKeys = true // Prevents crashing if FastAPI adds a new field later
        isLenient = true
    }

    val instance: BookApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(BookApiService::class.java)
    }
}