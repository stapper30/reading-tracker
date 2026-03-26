package com.example.books

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Int = -1, val email: String, val password: String)

@Serializable
data class LoginResponse(val access_token: String, val token_type: String)
