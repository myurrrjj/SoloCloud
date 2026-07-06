package org.example.project

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual fun createDataStore(): DataStore<Preferences> {
    return createDataStoreWithDefaults(
        producePath = {
            val userHome = System.getProperty("user.home")
            val configDir = File(userHome, ".solocloud")
            if (!configDir.exists()) {
                configDir.mkdirs()
            }
            File(configDir, "solocloud.preferences_pb").absolutePath
        }
    )
}