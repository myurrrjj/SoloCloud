package org.example.project

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.example.project.config.AppConfig
import org.example.project.config.ServerBootstrapper

fun main() {
    val appConfig = ServerBootstrapper.initialize()
    embeddedServer(
        factory = Netty,
        port = appConfig.port,
        host = appConfig.host,
        module = { module(appConfig) }
    ).start(wait = true)
}

fun Application.module(appConfig: AppConfig) {
    routing {
        get("/") {
            call.respondText("SoloCloud Engine Active. Configured for user: ${appConfig.adminUsername}")
        }
    }
}