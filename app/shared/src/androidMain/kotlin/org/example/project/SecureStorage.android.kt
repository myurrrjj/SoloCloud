package org.example.project

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual fun createDataStore(): DataStore<Preferences> {
    return createDataStoreWithDefaults(
        producePath = {
            val context = AndroidContextProvider.applicationContext
            context.filesDir.resolve("solocloud.preferences_pb").absolutePath
        }
    )
}