package com.example.winemap.data.local


import com.example.winemap.domain.models.Location
import com.example.winemap.domain.models.WineryInfo

object WineryDataProvider {

    private val wineries = listOf<WineryInfo>(
        // Add your wineries here
    )

    fun getAllWineries(): List<WineryInfo> = wineries

    fun searchWineries(query: String): List<WineryInfo> {
        if (query.isBlank()) return wineries

        return wineries.filter { winery ->
            winery.name.contains(query, ignoreCase = true) ||
                    winery.region.contains(query, ignoreCase = true) ||
                    winery.description.contains(query, ignoreCase = true)
        }
    }

    fun getWineriesByRegion(region: String): List<WineryInfo> {
        return wineries.filter { it.region.equals(region, ignoreCase = true) }
    }

    fun getNearbyWineries(userLocation: Location, radiusKm: Double = 50.0): List<WineryInfo> {
        return wineries.filter { winery ->
            Location.calculateDistance(userLocation, winery.location) <= radiusKm
        }.sortedBy { winery ->
            Location.calculateDistance(userLocation, winery.location)
        }
    }

    fun getWineryByName(name: String): WineryInfo? {
        return wineries.find { it.name.equals(name, ignoreCase = true) }
    }

    fun getRegions(): List<String> {
        return wineries.map { it.region }.distinct().sorted()
    }
}