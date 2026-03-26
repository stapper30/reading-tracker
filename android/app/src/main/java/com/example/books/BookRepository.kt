package com.example.books

import android.util.Log

class BookRepository(
    private val apiService: BookApiService,
    private val googleBooksApiService: GoogleBooksApiService,
    private val overdriveApiService: OverdriveApiService,
    private val db: AppDatabase
) {
    suspend fun fetchCachedBooks(): List<Book> {
        return db.bookDao().getAll()
    }

    suspend fun cacheBooks(books: List<Book>) {
        db.bookDao().deleteAll()
        for (book in books) {
            db.bookDao().insert(book)
        }
    }

    suspend fun fetchBooks(token: String): List<Book> {
        return try {
            // "Bearer " prefix is required for JWT
            apiService.getBooks("Bearer $token")
        } catch (e: Exception) {
            Log.e("API", "Error fetching books", e)
            emptyList() // Handle errors gracefully
        }
    }

    suspend fun addBook(token: String, book: Book): List<Book> {
        return try {
            // "Bearer " prefix is required for JWT
            apiService.addBook("Bearer $token", book)
        } catch (e: Exception) {
            Log.e("API", "Error adding book", e)
            emptyList<Book>()
        }
    }

    suspend fun deleteBook(token: String, bookId: Int?): List<Book> {
        return try {
            // "Bearer " prefix is required for JWT
            apiService.deleteBook("Bearer $token", bookId)
        } catch (e: Exception) {
            Log.e("API", "Error deleting book", e)
            emptyList<Book>()
        }
    }

    suspend fun markAsRead(token: String, bookId: Int?, rating: Int): List<Book> {
        return try {
            // "Bearer " prefix is required for JWT
            apiService.markAsRead("Bearer $token", bookId, rating)
        } catch (e: Exception) {
            Log.e("API", "Error marking as read", e)
            emptyList<Book>()
        }
    }

    suspend fun searchBooks(title: String): GoogleBookResponse {
        return try {
            val response = googleBooksApiService.search("intitle:${title}")
            Log.d("API", "Fetched ${response.items}")
            response
        } catch (e: Exception) {
            Log.e("API", "Error searching books", e)
            GoogleBookResponse() // Handle errors gracefully
        }
    }

    suspend fun checkOverdriveAvailability(title: String, author: String): AvailabilityStatus {
        return try {
            val response = overdriveApiService.searchAudiobooks(query = "$title $author")
            val items = response.items.take(10)

            var match: OverdriveItem? = null;

            for (it in items) {
                if (it.title.equals(title, ignoreCase = true) &&
                    it.firstCreatorName.contains(author, ignoreCase = true)
                ) {
                    if (match === null || !match.isAvailable && it.isAvailable) {
                        match = it;
                    }
                }
            }


            when {
                match == null -> AvailabilityStatus.NO_MATCH
                match.isAvailable -> AvailabilityStatus.AVAILABLE
                else -> AvailabilityStatus.OUT
            }
        } catch (e: Exception) {
            Log.e("API", "Error checking Overdrive availability", e)
            AvailabilityStatus.NO_MATCH
        }
    }
}

enum class AvailabilityStatus {
    AVAILABLE, OUT, NO_MATCH
}
