package com.example.winemap.di

import com.example.winemap.utils.LocationManager

object IOSDependencies {

    fun createAppContainer(): AppContainer {
        val container = AppContainer()

        // Initialize platform-specific dependencies
        val locationManager = LocationManager()
        container.initializePlatformDependencies(locationManager)

        return container
    }
}