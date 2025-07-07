package com.example.winemap.utils

import com.example.winemap.domain.models.Location

actual class LocationManager {

    actual suspend fun getCurrentLocation(): Location? {
        // TODO: Implement Android GPS
        return null
    }

    actual suspend fun checkLocationPermission(): Boolean {
        // TODO: Check Android location permission
        return false
    }

    actual suspend fun requestLocationPermission(): Boolean {
        // TODO: Request Android location permission
        return false
    }
}