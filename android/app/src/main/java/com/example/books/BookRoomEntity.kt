package com.example.books

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookRoomEntity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val author: String,
    val rating: Int,
)