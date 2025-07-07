package com.example.winemap.domain.models

import kotlinx.datetime.Clock

data class Post(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userProfileImage: String = "",
    val wineryName: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val rating: Int = 0, // 1-5 stars
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val location: Location? = null
) {
    fun isValid(): Boolean {
        return userId.isNotEmpty() &&
                userName.isNotEmpty() &&
                wineryName.isNotEmpty() &&
                content.isNotEmpty() &&
                rating in 1..5
    }

    fun getFormattedTimestamp(): String {
        val now = Clock.System.now().toEpochMilliseconds()
        val diff = now - timestamp

        return when {
            diff < 60_000 -> "now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> "${diff / 3600_000}h ago"
            else -> "${diff / 86400_000}d ago"
        }
    }
}