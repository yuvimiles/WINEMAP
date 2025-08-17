package com.example.winemap.utils

object Constants {

    // Firebase Collections
    const val USERS_COLLECTION = "users"
    const val POSTS_COLLECTION = "posts"

    // Storage paths
    const val POSTS_IMAGES_PATH = "images/posts"
    const val PROFILE_IMAGES_PATH = "images/profiles"

    // Rating
    const val MIN_RATING = 1
    const val MAX_RATING = 5

    // Location
    const val DEFAULT_SEARCH_RADIUS_KM = 50.0
    const val ISRAEL_CENTER_LAT = 31.5
    const val ISRAEL_CENTER_LNG = 34.75

    // Validation
    const val MIN_USERNAME_LENGTH = 3
    const val MAX_USERNAME_LENGTH = 20
    const val MIN_POST_CONTENT_LENGTH = 10
    const val MAX_POST_CONTENT_LENGTH = 500

    // Animation durations
    const val ANIMATION_DURATION_SHORT = 300
    const val ANIMATION_DURATION_MEDIUM = 500
    const val ANIMATION_DURATION_LONG = 800
}