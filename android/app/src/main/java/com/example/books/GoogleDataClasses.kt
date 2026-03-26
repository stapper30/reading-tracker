package com.example.books

import kotlinx.serialization.Serializable

@Serializable
data class GoogleBookResponse(
    val items: List<GoogleBookItem> = emptyList()
)

@Serializable
data class GoogleBookItem(
    val id: String,
    val volumeInfo: GoogleVolumeInfo
)

@Serializable
data class GoogleVolumeInfo(
    val title: String,
    // Use @SerialName if the JSON key matches exactly,
    // but it's good practice for clarity.
    val authors: List<String> = emptyList(),
    val imageLinks: ImageLinks? = null
)

@Serializable
data class ImageLinks(
    val thumbnail: String
)