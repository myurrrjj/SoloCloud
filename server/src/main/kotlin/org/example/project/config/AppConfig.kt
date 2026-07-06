package org.example.project.config

import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    val host: String = "0.0.0.0",
    val port: Int = 8080,
    val adminUsername: String = "admin",
    val passwordHash: String,
    val passwordSalt: String
)