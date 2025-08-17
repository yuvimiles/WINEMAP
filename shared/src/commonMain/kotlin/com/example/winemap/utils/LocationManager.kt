package com.example.winemap.utils

import com.example.winemap.domain.models.Location

expect class LocationManager {
    suspend fun getCurrentLocation(): Location?
    suspend fun checkLocationPermission(): Boolean
    suspend fun requestLocationPermission(): Boolean
}