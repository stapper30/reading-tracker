package com.example.books

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.books.composables.AddBookForm
import com.example.books.composables.SimpleSearchBar

class AddBookActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.instance // Your Retrofit setup
        val googleBooksApiService = GoogleRetroFitClient.instance
        val db = AppDatabase.getDatabase(applicationContext)
        val overdriveApiService = OverdriveRetrofitClient.instance
        val repository = BookRepository(apiService, googleBooksApiService, overdriveApiService,db)
        val viewModel = BookViewModelFactory(repository, TokenManager(applicationContext)).create(BookViewModel::class.java )
        val textFieldState = mutableStateOf(TextFieldState())

        setContent {
            Scaffold(
                topBar = {
                    // Put the search bar here if you want it pinned to the top
                    SimpleSearchBar(
                        textFieldState = textFieldState.value,
                        onSearch = { /* ... */ },
                        viewModel = viewModel
                    )
                }
            ) { innerPadding ->
                // The form goes in the content area
                Column(
                    modifier = Modifier
                        .padding(innerPadding) // Important: prevents topBar overlap
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    AddBookForm(viewModel)
                }
            }
        }
    }
}
