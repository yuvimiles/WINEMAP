package com.example.winemap.utils

import android.content.Context
import android.widget.Toast

actual object PlatformUtils {

    private var context: Context? = null

    fun init(context: Context) {
        this.context = context
    }

    actual fun getPlatformName(): String = "Android"

    actual fun showToast(message: String) {
        context?.let { ctx ->
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
        }
    }
}