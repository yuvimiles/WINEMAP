package com.example.winemap

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform