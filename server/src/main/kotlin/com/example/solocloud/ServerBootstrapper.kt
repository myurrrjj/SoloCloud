package com.example.solocloud

import com.example.solocloud.dto.ServerConfig
import com.example.solocloud.storage.SecureStorage
import io.ktor.http.DEFAULT_PORT
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket

data class ServerRuntimeState(
    val address: String,
    val port: Int,
    val rootFolder: String
)

class ServerBootstrapper(private val secureStorage: SecureStorage) {
    fun bootstrap(): ServerRuntimeState {
        val lanAddress = NetworkUtils.detectLanAddress()
            ?: throw IllegalStateException("No LAN address found. Are you connected to Wifi?")
        val config = loadOrCreateConfig(lanAddress)
        val port = findAvailablePort(config.port)


        return ServerRuntimeState(
            address = lanAddress,
            port = port,
            rootFolder = config.rootFolder
        )
    }

    private fun loadOrCreateConfig(lanAddress: String): ServerConfig = runBlocking {
        secureStorage.getServerConfig() ?: ServerConfig(
            rootFolder = defaultRootFolder(),
            port = DEFAULT_PORT,
            bindAddress = lanAddress
        ).also { secureStorage.saveServerConfig(it) }

    }

    private fun defaultRootFolder(): String {
        return System.getProperty("user.home") + "/SoloCloud"

    }


    private fun findAvailablePort(start: Int): Int {
        for (port in start until start + MAX_PORT_ATTEMPTS) {
            if (isPortFree(port)) return port
        }
        throw IllegalStateException("No free port found starting at $start")

    }

    private fun isPortFree(port: Int): Boolean = try {

        ServerSocket().use { socket ->
            socket.reuseAddress = true
            socket.bind(InetSocketAddress("0.0.0.0", port))
        }
        true

    } catch (e: IOException) {
        false
    }
    private companion object{
        const val DEFAULT_PORT = 8080
        const val MAX_PORT_ATTEMPTS = 20
    }

}