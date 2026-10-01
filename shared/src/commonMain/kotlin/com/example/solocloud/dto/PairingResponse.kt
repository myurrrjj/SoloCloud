package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PairingResponse(
    @SerialName("token")
    val token: String,

    @SerialName("expiresAt")
    val expiresAt: Long
)