package com.example.solocloud

import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

object NetworkUtils {
 private val VIRTUAL_INTERFACE_PREFIXES = listOf(
     "docker", "veth", "br-", "tun", "tap", "utun",
     "vmnet", "vboxnet", "wg", "tailscale", "zerotier"
 )

    fun detectLanAddress():String? = listLanCandidates().firstOrNull()

    fun listLanCandidates():List<String>{
        return try{
            NetworkInterface.getNetworkInterfaces()
                .toList()
                .filter { isPhysicalCandidate(it) }
                .flatMap { nic->
                    nic.inetAddresses.toList()
                        .filterIsInstance<Inet4Address>()
                        .filter { it.isSiteLocalAddress && !it.isLoopbackAddress }
                        .map { it.hostAddress }
                }
        } catch (e: SocketException){
            emptyList()
        }
    }
    private fun isPhysicalCandidate(nic: NetworkInterface):Boolean{
        if (!nic.isUp||nic.isLoopback) return false
        val name = nic.name.lowercase()
        return VIRTUAL_INTERFACE_PREFIXES.none {name.startsWith(it)}
    }
}