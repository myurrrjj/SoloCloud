package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PairingResponse(
    @SerialName("jwt")
    val jwt: String,
    @SerialName("expiresAt")
    val expiresAt: Long,
    @SerialName("serverName")
    val serverName: String
)