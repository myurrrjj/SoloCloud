package org.example.project
expect class SecureStorage() {
    fun saveString(key: String, value: String)
    fun getString(key: String): String?
}