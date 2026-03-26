package com.example.books.composables

import com.example.books.Book
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.books.AvailabilityStatus
import com.example.books.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookItem(book: Book, viewModel: BookViewModel) {
    var showRatingDialog by remember { mutableStateOf(false) }
    var userInput by remember { mutableStateOf("") }
    
    val availabilityStatus = viewModel.availabilityMap[book.id]
    val statusColor = when (availabilityStatus) {
        AvailabilityStatus.AVAILABLE -> Color.Green
        AvailabilityStatus.OUT -> Color(0xFFFFA500) // Orange
        AvailabilityStatus.NO_MATCH -> Color.Red
        null -> Color.Gray
    }

    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Rate the book") },
            text = {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = { Text("Rating...") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val rating = userInput.toIntOrNull() ?: 0
                    viewModel.markAsRead(book.id, rating)

                    showRatingDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(modifier = Modifier
        .padding(4.dp)
        .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = book.image_url ?: "https://via.placeholder.com/150",
                contentDescription = "Book Cover",
                modifier = Modifier
                    .size(40.dp, 60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (book.type == "wishlist") {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(text = book.title, style = MaterialTheme.typography.titleSmall)
                }
                Text(
                    text = "by ${book.author}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (book.type == "read") {
                    Text(text = "${book.rating} / 10")
                } else {
                    Button(
                        onClick = { showRatingDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Done", style = MaterialTheme.typography.labelSmall)
                    }
                }
                IconButton(onClick = { viewModel.deleteBook(book.id) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete book")
                }
            }
        }
    }
}
