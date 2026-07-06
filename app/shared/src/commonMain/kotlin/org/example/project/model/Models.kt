package org.example.project.model

import kotlinx.serialization.Serializable


@Serializable
data class FileItem(
    val id: String,
    val name: String,
    val path: String,
    val sizeBytes: Long,
    val isDirectory: Boolean

)

@Serializable
data class UploadProgress(
    val fileName: String,
    val bytesSent: Long,
    val totalBytes: Long,
    val isComplete: Boolean
)

@Serializable
data class AuthToken(
    val token: String,
    val expireAt: String

)

@Serializable
data class ServerConfig(
    val host: String,
    val port: Int,
    val isTunnelActive: Boolean,
    val tunnelUrl: String?
)

