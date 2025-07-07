package com.example.winemap.domain.models

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val bio: String = "",
    val favoriteWineries: List<String> = emptyList()
) {
    fun addFavoriteWinery(wineryName: String): User {
        return if (!favoriteWineries.contains(wineryName)) {
            copy(favoriteWineries = favoriteWineries + wineryName)
        } else {
            this
        }
    }

    fun removeFavoriteWinery(wineryName: String): User {
        return copy(favoriteWineries = favoriteWineries - wineryName)
    }

    fun isFavorite(wineryName: String): Boolean {
        return favoriteWineries.contains(wineryName)
    }
}