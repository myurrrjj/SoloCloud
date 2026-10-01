package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DirectoryListing(
    @SerialName("items")
    val items: List<FileItem>,
    @SerialName("nextCursor")
    val nextCursor: String? = null
)