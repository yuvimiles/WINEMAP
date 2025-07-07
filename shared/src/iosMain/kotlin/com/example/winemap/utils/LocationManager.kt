package com.example.winemap.utils

import com.example.winemap.domain.models.Location

actual class LocationManager {

    actual suspend fun getCurrentLocation(): Location? {
        // TODO: Implement iOS CoreLocation
        return null
    }

    actual suspend fun checkLocationPermission(): Boolean {
        // TODO: Check iOS location permission
        return false
    }

    actual suspend fun requestLocationPermission(): Boolean {
        // TODO: Request iOS location permission
        return false
    }
}