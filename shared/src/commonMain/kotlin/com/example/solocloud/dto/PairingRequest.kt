package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PairingRequest(
    @SerialName("pairingToken")
    val pairingToken: String,
    @SerialName("deviceName")
    val deviceName: String
)