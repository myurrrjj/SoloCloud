package com.example.solocloud.storage

import android.content.Context
import android.provider.Settings.Global.putString
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.solocloud.dto.ServerConfig
import kotlinx.serialization.json.Json

actual class SecureStorage(context: Context) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        "solocloud_secure_storage",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    actual suspend fun saveToken(token: String) {
        preferences.edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    actual suspend fun clearToken() {
        preferences.edit()
            .remove(KEY_TOKEN)
            .apply()
    }

    actual suspend fun saveServerConfig(config: ServerConfig) {
        preferences.edit()
            .putString(
                KEY_SERVER_CONFIG,
                json.encodeToString(config)
            )
            .apply()
    }

    actual suspend fun getToken(): String? {
        return preferences.getString(KEY_TOKEN, null)
    }



    actual suspend fun getServerConfig(): ServerConfig? {
        val stored = preferences.getString(KEY_SERVER_CONFIG, null)
            ?: return null

        return json.decodeFromString<ServerConfig>(stored)
    }


    actual suspend fun saveJwtSecret(secret: String) {
        preferences.edit()
            .putString(KEY_JWT_SECRET,secret)
            .apply()
    }

    actual suspend fun getJwtSecret(): String? {
        return preferences.getString(KEY_JWT_SECRET,null)
    }





    private companion object {
        const val KEY_TOKEN = "jwt_token"
        const val KEY_SERVER_CONFIG = "server_config"
        const val KEY_JWT_SECRET = "jwt_secret"
    }
}