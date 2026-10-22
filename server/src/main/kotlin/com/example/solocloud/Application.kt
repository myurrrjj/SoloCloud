package com.example.solocloud

import com.example.solocloud.auth.AuthPlugin
import com.example.solocloud.auth.JwtManager
import com.example.solocloud.auth.PairingManager
import com.example.solocloud.routes.authRoutes
import com.example.solocloud.storage.SecureStorage
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable


@Serializable
data class HealthResponse(
    val status: String, val version: String
)


fun main() {
    val secureStorage = SecureStorage()

    val state = ServerBootstrapper(SecureStorage()).bootstrap()
    val jwtManager = JwtManager(loadOrCreateJwtSecret(secureStorage))
    val pairingManager = PairingManager()

    println("SoloCloud running at http://${state.address}:${state.port}")
    println("Serving files from: ${state.rootFolder}")

    println("Pairing token: ${pairingManager.currentToken()}")


    embeddedServer(Netty, port = state.port, host = "0.0.0.0") {
        module(state, jwtManager, pairingManager)
    }.start(wait = true)

}

fun Application.module(
    state: ServerRuntimeState,
    jwtManager: JwtManager,
    pairingManager: PairingManager
) {
    install(ContentNegotiation) {
        json()
    }
    install(AuthPlugin){
        manager = jwtManager
    }
    routing {
        get("/health") {
            call.respond(HealthResponse(status = "ok", version = "1.0.0"))
        }

        route("/api/v1"){
            authRoutes(jwtManager,pairingManager,"SoloCloud")

        }
    }
}

private fun loadOrCreateJwtSecret(storage: SecureStorage):String = runBlocking {
    storage.getJwtSecret()?: JwtManager.generateSecret().also { storage.saveJwtSecret(it) }
}