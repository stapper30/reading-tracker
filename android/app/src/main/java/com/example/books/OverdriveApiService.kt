package com.example.books

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OverdriveApiService {
    @GET("v2/libraries/auckland/media")
    suspend fun searchAudiobooks(
        @Query("query") query: String,
        @Query("mediaTypes") mediaTypes: String = "audiobook",
        @Query("format") format: String = "audiobook-overdrive,audiobook-overdrive-provisional",
        @Query("perPage") perPage: Int = 24,
        @Query("page") page: Int = 1,
        @Query("truncateDescription") truncateDescription: Boolean = false,
        @Query("includedFacets") includedFacets: List<String> = listOf(
            "availability", "mediaTypes", "formats", "maturityLevels", 
            "subjects", "languages", "boolean", "addedDates", 
            "audiobookDuration", "freshStart"
        ),
        @Query("x-client-id") xClientId: String = "dewey",
        @Header("accept") accept: String = "*/*",
        @Header("accept-language") acceptLanguage: String = "en-GB,en-US,en",
        @Header("origin") origin: String = "https://libbyapp.com",
        @Header("user-agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36"
    ): OverdriveResponse

    @GET("v2/libraries/auckland/media")
    suspend fun searchEbooks(
        @Query("query") query: String,
        @Query("mediaTypes") mediaTypes: String = "audiobook",
        @Query("format") format: String = "ebook-overdrive,ebook-media-do,ebook-overdrive-provisional",
        @Query("perPage") perPage: Int = 24,
        @Query("page") page: Int = 1,
        @Query("truncateDescription") truncateDescription: Boolean = false,
        @Query("includedFacets") includedFacets: List<String> = listOf(
            "availability", "mediaTypes", "formats", "maturityLevels",
            "subjects", "languages", "boolean", "addedDates",
            "audiobookDuration", "freshStart"
        ),
        @Query("x-client-id") xClientId: String = "dewey",
        @Header("accept") accept: String = "*/*",
        @Header("accept-language") acceptLanguage: String = "en-GB,en-US,en",
        @Header("origin") origin: String = "https://libbyapp.com",
        @Header("user-agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36"
    ): OverdriveResponse
}
