package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class QrPayload(
    @SerialName("ip")
    val ip: String,
    @SerialName("port")
    val port: Int,
    @SerialName("token")
    val pairingToken: String

)