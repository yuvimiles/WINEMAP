package com.example.winemap.domain.models

import kotlin.math.*

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = ""
) {
    companion object {
        fun calculateDistance(loc1: Location, loc2: Location): Double {
            val earthRadius = 6371.0 // קילומטרים
            val dLat = (loc2.latitude - loc1.latitude) * PI / 180
            val dLon = (loc2.longitude - loc1.longitude) * PI / 180

            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(loc1.latitude * PI / 180) * cos(loc2.latitude * PI / 180) *
                    sin(dLon / 2) * sin(dLon / 2)

            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return earthRadius * c
        }
    }
}