package com.example.books

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface BookApiService {
    @GET("books")
    suspend fun getBooks(
        @Header("Authorization") token: String
    ): List<Book>

    @POST("books/add")
    suspend fun addBook(
        @Header("Authorization") token: String,
        @Body book: Book
    ): List<Book>

    @DELETE("books/delete/{id}")
    suspend fun deleteBook(
        @Header("Authorization") token: String,
        @Path("id") id: Int?
    ): List<Book>

    @POST("books/mark_read/{id}")
    suspend fun markAsRead(
        @Header("Authorization") token: String,
        @Path("id") id: Int?,
        @Body rating: Int
    ): List<Book>

    @FormUrlEncoded
    @POST("users/login")
    suspend fun login(
        @Field("grant_type") grantType: String = "password",
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("users/register")
    suspend fun register(
        @Body user: User
    )
}