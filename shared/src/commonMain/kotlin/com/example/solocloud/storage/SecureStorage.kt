package com.example.solocloud.storage

import com.example.solocloud.dto.ServerConfig

expect class SecureStorage {

    suspend fun saveToken(token : String)
    suspend fun getToken():String?
    suspend fun clearToken()
    suspend fun saveServerConfig(config:ServerConfig)
    suspend fun getServerConfig(): ServerConfig?
    suspend fun saveJwtSecret(secret:String)
    suspend fun getJwtSecret():String?

}