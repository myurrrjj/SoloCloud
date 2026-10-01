package com.example.solocloud

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform