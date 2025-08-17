package com.example.winemap.utils

expect object PlatformUtils {
    fun getPlatformName(): String
    fun showToast(message: String)
}