package com.example.solocloud.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    @SerialName("code")
    val code:String,
    @SerialName("message")
    val message: String
)
