package com.example.books

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BookViewModel(private val repository: BookRepository, tokenManager: TokenManager) :
    ViewModel() {
    // This is the state the UI will watch
    var books = mutableStateOf<List<Book>>(emptyList())
    var wishlist = mutableStateOf<List<Book>>(emptyList())
    var history = mutableStateOf<List<Book>>(emptyList())
    var title = mutableStateOf("")
    var author = mutableStateOf("")
    var rating = mutableStateOf("")
    var image_url = mutableStateOf("")
    var type = mutableStateOf("read")
    var isLoading = mutableStateOf(false)
    var isSearching = mutableStateOf(false)
    var searchResults = mutableStateOf<List<GoogleBookItem>>(emptyList())

    // Map of book ID to its availability status
    var availabilityMap = mutableStateMapOf<Int, AvailabilityStatus>()

    private val token: String = tokenManager.getToken().toString()

    companion object {
        private var instance: BookViewModel? = null

        fun getInstance(repository: BookRepository, tokenManager: TokenManager): BookViewModel {
            if (instance == null) {
                instance = BookViewModel(repository, tokenManager)
            }
            return instance!!
        }
    }

    fun loadCachedBooks() {
        viewModelScope.launch {
            try {
                books.value = repository.fetchCachedBooks()
            } finally {
                loadWishlistAndHistory()
                checkAllAvailability()
            }
        }
    }

    fun loadBooks() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                books.value = repository.fetchBooks(token)
                if (books.value.isNotEmpty()) {
                    cacheBooks()
                } else {
                    loadCachedBooks()
                }
                Log.d("API", "Fetched ${books.value}")
            } catch (e: Exception) {
                loadCachedBooks()
                Log.d("API", "Error fetching books: ${e.message}")
            } finally {
                loadWishlistAndHistory()
                isLoading.value = false
                checkAllAvailability()
            }
        }
    }

    private fun checkAllAvailability() {
        books.value.forEach { book ->
            checkAvailability(book)
        }
    }

    fun checkAvailability(book: Book) {
        val bookId = book.id ?: return
        // Only check if we haven't checked or if it's currently being checked (optional optimization)
        viewModelScope.launch {
            val status = repository.checkOverdriveAvailability(book.title, book.author)
            availabilityMap[bookId] = status
        }
    }

    fun cacheBooks() {
        viewModelScope.launch {
            repository.cacheBooks(books.value)
        }
    }

    fun loadWishlist() {
        wishlist.value = books.value.filter { it.type == "wishlist" }
    }

    fun loadHistory() {
        history.value = books.value.filter { it.type == "read" }
    }

    fun loadWishlistAndHistory() {
        loadWishlist()
        loadHistory()
    }

    fun addBook(book: Book, onBookAdded: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                books.value = repository.addBook(token, book)
                onBookAdded()
            } finally {
                isLoading.value = false
                loadWishlistAndHistory()
                checkAllAvailability()
            }
        }
    }

    fun deleteBook(bookId: Int?) {
        viewModelScope.launch {
            try {
                books.value = repository.deleteBook(token, bookId)
            } finally {
                loadWishlistAndHistory()
            }
        }
    }

    fun markAsRead(bookId: Int?, rating: Int) {
        viewModelScope.launch {
            try {
                books.value = repository.markAsRead(token, bookId, rating)
            } finally {
                loadWishlistAndHistory()
            }
        }
    }

    fun searchBooks(title: String) {
        viewModelScope.launch {
            isSearching.value = true
            try {
                val response = repository.searchBooks(title)
                searchResults.value = response.items
            } finally {
                isSearching.value = false
            }
        }
    }
}
