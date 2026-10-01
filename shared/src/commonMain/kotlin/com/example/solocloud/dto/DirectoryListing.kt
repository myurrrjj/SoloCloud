package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DirectoryListing(
    @SerialName("path")
    val path:String,
    @SerialName("items")
    val items: List<FileItem>
)