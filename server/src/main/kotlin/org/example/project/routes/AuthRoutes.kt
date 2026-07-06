package org.example.project.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.example.project.security.JwtManager


@Serializable
data class LoginRequest(val username: String, val passwordHash: String)

@Serializable
data class TokenResponse(val token: String)

fun Application.authRoutes(jwtManager: JwtManager, expectedUser: String, expectedHash: String) {
    routing {
        rateLimit(RateLimitName("login_limit")) {
            post("/login") {
                val request = runCatching { call.receive<LoginRequest>() }.getOrNull()
                if (request == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (request.username == expectedUser && request.passwordHash == expectedHash) {
                    val token = jwtManager.generateToken(request.username)
                    call.respond(HttpStatusCode.OK, TokenResponse(token))

                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
        authenticate("auth-jwt") {
            get("/system/status") {
                call.respond(HttpStatusCode.OK, "Authorised System Operational")
            }
        }
    }
}