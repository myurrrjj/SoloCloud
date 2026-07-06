package org.example.project.config

import kotlinx.serialization.json.Json
import java.io.File

object ConfigManager {
    private val userHome = System.getProperty("user.home")
    private val configDir = File(userHome, ".solocloud")
    private val configFile = File(configDir, "config.json")

    private val jsonEngine = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun loadConfig(): AppConfig? {
        if (!configFile.exists()) {
            return null
        }
        return try {
            val fileContent = configFile.readText()
            jsonEngine.decodeFromString<AppConfig>(fileContent)

        } catch (e: Exception) {
            System.err.println("CRITICAL: Failed to parse config.json. The file may be corrupted.")
            e.printStackTrace()
            null
        }
    }

    fun saveConfig(config: AppConfig) {
        if (!configDir.exists()) {
            configDir.mkdirs()

        }
        val tempFile = File(configDir, "config.json.tmp")
        try {
            val jsonString = jsonEngine.encodeToString(config)
            tempFile.writeText(jsonString)
            val success = tempFile.renameTo(configFile)
            if (!success) {
                System.err.println("CRITICAL: Failed to overwrite existing config.json atomically.")
            }

        } catch (e: Exception) {
            System.err.println("CRITICAL: Failed to write configuration to disk.")
            e.printStackTrace()
        }
    }
}