package org.example.project


actual class SecureStorage actual constructor() {
    actual fun saveString(key: String, value: String) {
    }

    actual fun getString(key: String): String? {
        return null
    }
}