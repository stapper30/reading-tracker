package com.example.books

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object OverdriveRetrofitClient {
    private const val BASE_URL = "https://thunder.api.overdrive.com/"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val instance: OverdriveApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OverdriveApiService::class.java)
    }
}
