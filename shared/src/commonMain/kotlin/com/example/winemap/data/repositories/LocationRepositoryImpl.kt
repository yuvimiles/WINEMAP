package com.example.winemap.data.repositories

import com.example.winemap.domain.models.Location
import com.example.winemap.domain.models.WineryInfo
import com.example.winemap.domain.repository.LocationRepository
import com.example.winemap.utils.LocationManager
import com.example.winemap.data.local.WineryDataProvider
import com.example.winemap.utils.Result
import com.example.winemap.utils.ApiException

class LocationRepositoryImpl(
    private val locationManager: LocationManager
) : LocationRepository {

    override suspend fun getCurrentLocation(): Result<Location> {
        return try {
            val location = locationManager.getCurrentLocation()
            if (location != null) {
                Result.Success(location)
            } else {
                Result.Error(ApiException.NotFoundException)
            }
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Location error"))
        }
    }

    override suspend fun checkLocationPermission(): Result<Boolean> {
        return try {
            val hasPermission = locationManager.checkLocationPermission()
            Result.Success(hasPermission)
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Permission check failed"))
        }
    }

    override suspend fun requestLocationPermission(): Result<Boolean> {
        return try {
            val granted = locationManager.requestLocationPermission()
            Result.Success(granted)
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Permission request failed"))
        }
    }

    override suspend fun getNearbyWineries(location: Location, radiusKm: Double): Result<List<WineryInfo>> {
        return try {
            val nearbyWineries = WineryDataProvider.getNearbyWineries(location, radiusKm)
            Result.Success(nearbyWineries)
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Get nearby wineries failed"))
        }
    }
}