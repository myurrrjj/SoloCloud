package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DownloadProgress(
    @SerialName("fileName")
    val fileName: String,
    @SerialName("bytesReceived")
    val bytesReceived: Long,
    @SerialName("totalBytes")
    val totalBytes: Long,
    @SerialName("state")
    val state: TransferState
)
