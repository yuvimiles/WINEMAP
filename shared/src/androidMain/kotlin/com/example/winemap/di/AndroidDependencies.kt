package com.example.winemap.di

import android.content.Context
import com.example.winemap.utils.LocationManager
import com.example.winemap.utils.ImagePicker
import com.example.winemap.utils.PlatformUtils

object AndroidDependencies {

    fun createAppContainer(context: Context): AppContainer {
        val container = AppContainer()

        // Initialize platform-specific dependencies
        val locationManager = LocationManager()
        container.initializePlatformDependencies(locationManager)

        // Initialize PlatformUtils
        PlatformUtils.init(context)

        return container
    }
}