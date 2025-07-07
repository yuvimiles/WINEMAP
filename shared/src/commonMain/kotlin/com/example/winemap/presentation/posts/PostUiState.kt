package com.example.winemap.presentation.posts

import com.example.winemap.domain.models.Post

data class PostUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedWinery: String = "",

    // Create Post form
    val newPostWinery: String = "",
    val newPostContent: String = "",
    val newPostRating: Int = 0,
    val newPostImagePath: String? = null,
    val isCreatePostValid: Boolean = false,

    // Filters
    val filterByWinery: String = "",
    val filterByUser: String = ""
)