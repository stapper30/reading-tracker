package com.example.books

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object GoogleRetroFitClient {
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"
    private const val API_KEY = "AIzaSyCifOoscEnP_xtC4PI-csuUWcpBVOj0aww"

    private val json = Json {
        ignoreUnknownKeys = true 
        isLenient = true
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("key", API_KEY)
                .build()
            chain.proceed(original.newBuilder().url(url).build())
        }
        .build()

    val instance: GoogleBooksApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GoogleBooksApiService::class.java)
    }
}
