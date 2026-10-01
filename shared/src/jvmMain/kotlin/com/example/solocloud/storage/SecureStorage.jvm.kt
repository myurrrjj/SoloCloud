package com.example.solocloud.storage

import com.example.solocloud.dto.ServerConfig

actual class SecureStorage {
    actual suspend fun saveToken(token: String) {
    }

    actual suspend fun getToken(): String? {
        TODO("Not yet implemented")
    }

    actual suspend fun clearToken() {
    }

    actual suspend fun saveServerConfig(config: ServerConfig) {
    }

    actual suspend fun getServerConfig(): ServerConfig? {
        TODO("Not yet implemented")
    }
}