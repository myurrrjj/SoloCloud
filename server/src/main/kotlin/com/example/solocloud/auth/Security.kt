package com.example.solocloud.auth


import com.example.solocloud.dto.ApiError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.request.path
import io.ktor.server.response.respond


class AuthPluginConfig {
    lateinit var manager: JwtManager
}

val AuthPlugin = createApplicationPlugin("AuthPlugin", ::AuthPluginConfig) {
    val jwtManager = pluginConfig.manager
    onCall { call->
        val path = call.request.path()
        if (!path.startsWith("/api/v1")) return@onCall
        if (path == "/api/v1/pair") return@onCall

        val header = call.request.headers[HttpHeaders.Authorization]
        val token = header
            ?.takeIf { it.startsWith("Bearer ") }
            ?.removePrefix("Bearer ")
            ?.trim()

        val subject = token?.let { jwtManager.verifyToken(it) }

        if (subject == null){
            call.respond(HttpStatusCode.Unauthorized,ApiError("UNAUTHORIZED","Missing or invalid token"))
        }

    }
}