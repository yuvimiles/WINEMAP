package com.example.winemap.domain.usecases

import com.example.winemap.domain.models.WineryInfo
import com.example.winemap.domain.repository.LocationRepository
import com.example.winemap.utils.Result

class GetNearbyWineriesUseCase(
    private val locationRepository: LocationRepository
) {
    suspend fun execute(radiusKm: Double = 50.0): Result<List<WineryInfo>> {

        // Get current location
        val locationResult = locationRepository.getCurrentLocation()
        if (locationResult is Result.Error) {
            return Result.Error(locationResult.exception)
        }

        val currentLocation = (locationResult as Result.Success).data

        // Get nearby wineries
        return locationRepository.getNearbyWineries(currentLocation, radiusKm)
    }
}