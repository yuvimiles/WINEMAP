package com.example.winemap.domain.models

data class WineryInfo(
    val name: String,
    val location: Location,
    val region: String = "",
    val description: String = ""
)