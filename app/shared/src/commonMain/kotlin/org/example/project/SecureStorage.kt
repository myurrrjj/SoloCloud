package org.example.project

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

//expect class SecureStorage() {
//    fun saveString(key: String, value: String)
//    fun getString(key: String): String?
//}


expect fun createDataStore(): DataStore<Preferences>

internal fun createDataStoreWithDefaults(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })

object SecureStorage {
    private val dataStore : DataStore<Preferences> = createDataStore()
    suspend fun saveString(key:String, value:String){
        val prefKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun getString(key:String):String?{
        val prefKey = stringPreferencesKey(key)
        return dataStore.data.first()[prefKey]
    }

    fun getStringFlow(key:String): Flow<String?>{
        val prefKey = stringPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[prefKey]
        }
    }
}
