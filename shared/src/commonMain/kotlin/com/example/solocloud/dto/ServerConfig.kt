package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig(
    @SerialName("rootFolder")
    val rootFolder : String,

    @SerialName("port")
    val port: Int,

    @SerialName("bindAddress")
    val bindAddress: String
)
