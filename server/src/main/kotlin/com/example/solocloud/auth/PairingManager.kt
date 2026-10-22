package com.example.solocloud.auth


import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference

class PairingManager {
    private data class PairingToken(val value: String, val expiresAt: Long)

    private val current = AtomicReference(newToken())
    fun currentToken(): String = current.get().value

    fun expiresAt(): Long = current.get().expiresAt

    fun consume(token: String): Boolean {
        while (true) {
            val snapshot = current.get()
            if (snapshot.value != token) return false
            if (System.currentTimeMillis() > snapshot.expiresAt) return false
            if (current.compareAndSet(snapshot, newToken())) return true

        }
    }

    fun rotate() {
        current.set(newToken())
    }

    private fun newToken(): PairingToken {
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        val value = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        return PairingToken(value, System.currentTimeMillis() + TOKEN_LIFETIME_MILLIS)


    }

    companion object {
        private val random = SecureRandom()
        const val TOKEN_LIFETIME_MILLIS = 15L * 60 * 1000
    }
}