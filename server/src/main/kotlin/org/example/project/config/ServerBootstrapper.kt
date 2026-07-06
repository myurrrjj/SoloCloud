package org.example.project.config

import org.example.project.security.CryptoManager

object ServerBootstrapper {
    fun initialize(): AppConfig {
        val existingConfig = ConfigManager.loadConfig()
        if (existingConfig != null) {
            println("=== SoloCloud Server Boot Sequence ===")
            println("Configuration loaded from disk successfully.")
            println("Admin Username: ${existingConfig.adminUsername}")
            println("======================================")
            return existingConfig
        }
        println("=== SoloCloud First-Time Setup ===")
        println("No existing configuration found. Generating secure defaults...")

        val plainTextPassword = CryptoManager.generateSecurePassword()
        val salt = CryptoManager.generateSalt()
        val hashedPassword = CryptoManager.hashPassword(plainTextPassword,salt)
        val newConfig = AppConfig(
            passwordHash = hashedPassword,
            passwordSalt = salt
        )

        ConfigManager.saveConfig(newConfig)
        println("=========================================================")
        println("CRITICAL: FIRST-TIME ADMIN CREDENTIALS")
        println("These will ONLY be shown once. Please save them securely.")
        println("Username: ${newConfig.adminUsername}")
        println("Password: $plainTextPassword")
        println("=========================================================")

        return newConfig
    }
}