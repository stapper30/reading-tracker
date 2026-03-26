package com.example.books

class AuthRepository(private val apiService: BookApiService) {

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(username = email, password = password)
    }

    suspend fun register(user: User) {
        apiService.register(user)
    }
}
