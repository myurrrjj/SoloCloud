package com.example.solocloud.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.security.SecureRandom
import java.util.Base64
import java.util.Date

class JwtManager(secret: String) {


    @Volatile
    private var algorithm: Algorithm = Algorithm.HMAC256(secret)

    @Volatile
    private var verifier: JWTVerifier = buildVerifier(secret)

    fun createToken(deviceName: String, expiresAtMillis: Long): String {
        return JWT.create().withIssuer(ISSUER)
            .withSubject(deviceName)
            .withIssuedAt(Date())
            .withExpiresAt(Date(expiresAtMillis))
            .sign(algorithm)
    }

    fun verifyToken(token: String): String? {
        return try {
            verifier.verify(token).subject
        } catch (e: JWTVerificationException) {
            null
        }
    }

    @Synchronized
    fun rotateSecret(newSecret:String){
        algorithm = Algorithm.HMAC256(newSecret)
        verifier = buildVerifier(newSecret)
    }

    private fun buildVerifier(secret: String): JWTVerifier{
        return JWT.require(Algorithm.HMAC256(secret))
            .withIssuer(ISSUER)
            .build()
    }


    companion object{
        const val ISSUER = "soloCloud"
        const val TOKEN_LIFETIME_MILLIS = 30L*24*60*60*1000
        private val random = SecureRandom()
        fun generateSecret():String{
            val bytes = ByteArray(32)
            random.nextBytes(bytes)
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        }
    }
}