package com.example.books

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun search(@Query("q") title: String): GoogleBookResponse
}