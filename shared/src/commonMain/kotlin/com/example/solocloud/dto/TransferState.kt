package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TransferState {
    @SerialName("pending")
    PENDING,
    @SerialName("running")
    RUNNING,
    @SerialName("paused")
    PAUSED,
    @SerialName("completed")
    COMPLETED,
    @SerialName("failed")
    FAILED,
    @SerialName("cancelled")
    CANCELLED
}