package com.example.solocloud.routes

import com.example.solocloud.auth.JwtManager
import com.example.solocloud.auth.PairingManager
import com.example.solocloud.dto.ApiError
import com.example.solocloud.dto.PairingRequest
import com.example.solocloud.dto.PairingResponse
import io.ktor.client.utils.HttpRequestCreated
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable

@Serializable
data class WhoAmIResponse(val deviceName: String)


fun Route.authRoutes(
    jwtManager: JwtManager,
    pairingManager: PairingManager,
    serverName:String
){
    post("/pair"){
        val request = call.receive<PairingRequest>()

        if (!pairingManager.consume(request.pairingToken)){
            call.respond(HttpStatusCode.Unauthorized, ApiError("PAIRING_REJECTED","Invalid or expired pairing token"))
            return@post
        }
        val expiresAt = System.currentTimeMillis() + JwtManager.TOKEN_LIFETIME_MILLIS
        val jwt = jwtManager.createToken(request.deviceName,expiresAt)
        call.respond(PairingResponse(jwt = jwt,expiresAt=  expiresAt,serverName = serverName))


    }

    get("/whoami"){
        val header = call.request.headers["Authorization"]
        val token = header?.removePrefix("Bearer ")?.trim()
        val deviceName = token?.let { jwtManager.verifyToken(it) }?:"unknown"
        call.respond(WhoAmIResponse(deviceName))
    }

}