package com.example.winemap.domain.repository


import com.example.winemap.domain.models.Location
import com.example.winemap.domain.models.WineryInfo
import com.example.winemap.utils.Result

interface LocationRepository {
    suspend fun getCurrentLocation(): Result<Location>
    suspend fun checkLocationPermission(): Result<Boolean>
    suspend fun requestLocationPermission(): Result<Boolean>
    suspend fun getNearbyWineries(location: Location, radiusKm: Double): Result<List<WineryInfo>>
}