package org.example.project.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtManager(secret: String, private val issuer: String) {
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(username: String, expirationMillis: Long = 900000): String {
        return JWT.create()
            .withIssuer(issuer)
            .withClaim("username",username)
            .withExpiresAt(Date(System.currentTimeMillis()+expirationMillis))
            .sign(algorithm)
    }
}