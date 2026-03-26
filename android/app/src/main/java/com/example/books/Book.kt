package com.example.books

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
@Serializable
data class Book(
    val title: String,
    val author: String,
    val rating: Int,
    val type: String,
    val image_url: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Int? = null
)