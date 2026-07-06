package org.example.project.security

//import io.ktor.server.application.Application
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.request.header
import io.ktor.server.response.respond
import kotlin.time.Duration.Companion.seconds

fun Application.configureSecurity(jwtManager: JwtManager) {
    install(RateLimit) {
        register(RateLimitName("login_limit")) {
            rateLimiter(limit = 5, refillPeriod = 60.seconds)
            requestKey { call ->
                call.request.header("CF-Connecting-IP") ?: call.request.local.remoteHost
            }
        }
    }
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(jwtManager.verifier)
            validate { credential ->
                if (credential.payload.getClaim("username").asString().isNotEmpty()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _->
                call.respond(HttpStatusCode.Unauthorized, "Invalid or expired token")
            }
        }
    }
}