package com.example.books

import kotlinx.serialization.Serializable

@Serializable
data class OverdriveResponse(
    val items: List<OverdriveItem> = emptyList()
)

@Serializable
data class OverdriveItem(
    val title: String,
    val firstCreatorName: String,
    val isAvailable: Boolean
)
