package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadProgress(
    @SerialName("fileName")
    val fileName: String,

    @SerialName("bytesTransferred")
    val bytesTransferred: Long,

    @SerialName("totalBytes")
    val totalBytes: Long,

    @SerialName("state")
    val state: TransferState
)