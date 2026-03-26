package com.example.books

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import com.example.books.composables.BookListScreen
import com.example.books.composables.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        // In a real app, you'd use a DI tool like Hilt, but for now:
        val apiService = RetrofitClient.instance // Your Retrofit setup
        val googleBooksApiService = GoogleRetroFitClient.instance
        val overdriveApiService = OverdriveRetrofitClient.instance
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = BookRepository(apiService, googleBooksApiService, overdriveApiService, db)
        val tokenManager = TokenManager(applicationContext)
        val viewModel = BookViewModelFactory(repository, tokenManager).create(BookViewModel::class.java)

        if (tokenManager.getToken() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        setContent {
            MainScreen(viewModel, intent.getStringExtra("START_ROUTE"))

            // Trigger the fetch (usually you'd do this after login)
            LaunchedEffect(Unit) {
                viewModel.loadCachedBooks()
                viewModel.loadBooks()
            }
        }
    }
}
