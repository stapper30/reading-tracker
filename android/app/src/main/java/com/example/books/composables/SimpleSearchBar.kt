package com.example.books.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.books.BookViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookViewModel
) {
    // Controls expansion state of the search bar
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxWidth()
            .padding(16.dp)
            .semantics { isTraversalGroup = true }
    ) {
        LaunchedEffect(textFieldState.text) {
            delay(300L)

            if (textFieldState.text.isNotEmpty()) {
                viewModel.searchBooks(textFieldState.text.toString())
            }
        }
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = {
                        textFieldState.edit { replace(0, length, it) }
                        viewModel.isSearching.value = true
                        // viewModel.searchBooks(it)
                    },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Search") }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            // Display search results in a scrollable column
            Column(Modifier.verticalScroll(rememberScrollState())) {
                if (viewModel.isLoading.value) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), contentAlignment = Alignment.TopCenter) {
                        CircularProgressIndicator()
                    }
                } else {
                    viewModel.searchResults.value.forEach { result ->
                        ListItem(
                            headlineContent = { Text(result.volumeInfo.title) },
                            supportingContent = {
                                Text(
                                    result.volumeInfo.authors?.firstOrNull() ?: ""
                                )
                            },
                            leadingContent = {
                                AsyncImage(
                                    model = result.volumeInfo.imageLinks?.thumbnail
                                        ?: "https://via.placeholder.com/150",
                                    contentDescription = "Book Cover",
                                    modifier = Modifier
                                        .size(50.dp, 75.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            },

                            modifier = Modifier
                                .clickable {
                                    textFieldState.edit {
                                        replace(
                                            0,
                                            length,
                                            result.volumeInfo.title
                                        )
                                    }
                                    viewModel.title.value = result.volumeInfo.title
                                    viewModel.author.value =
                                        result.volumeInfo.authors?.firstOrNull() ?: ""
                                    viewModel.image_url.value =
                                        result.volumeInfo.imageLinks?.thumbnail ?: ""
                                    expanded = false
                                }
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}