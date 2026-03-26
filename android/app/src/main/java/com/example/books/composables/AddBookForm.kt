package com.example.books.composables

import com.example.books.Book
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.books.BookViewModel
import com.example.books.MainActivity

@Composable
fun AddBookForm(viewModel: BookViewModel) {
    var title by viewModel.title
    var author by viewModel.author
    var rating by viewModel.rating
    var imageUrl by viewModel.image_url
    var type by viewModel.type
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Search or Enter Manually")
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (type == "read") {
            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text("Rating") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { type = "read" },
                modifier = Modifier.weight(1f),
                enabled = type != "read"
            ) {
                Text("I've read it!")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { type = "wishlist" },
                modifier = Modifier.weight(1f),
                enabled = type != "wishlist"
            ) {
                Text("I want to read it!")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            OutlinedButton(
                onClick = {
                    clear(viewModel)
                    (context as? Activity)?.finish()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    val bookRating = if (type == "read") rating.toIntOrNull() ?: 0 else 0
                    val book = Book(
                        title = title,
                        author = author,
                        rating = bookRating,
                        type = type,
                        image_url = imageUrl
                    )
                    viewModel.addBook(book) {
                        clear(viewModel)
                        (context as? Activity)?.finish()
                    }
                },
                modifier = Modifier.weight(1f),
            ) {
                Text("Add Book")
            }
        }
    }
}

fun clear(viewModel: BookViewModel) {
    viewModel.title.value = ""
    viewModel.author.value = ""
    viewModel.rating.value = ""
}