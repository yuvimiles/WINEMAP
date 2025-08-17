package com.example.winemap.utils

actual object PlatformUtils {

    actual fun getPlatformName(): String = "iOS"

    actual fun showToast(message: String) {
        // iOS doesn't have toast, use print for now
        println("iOS Message: $message")
    }
}