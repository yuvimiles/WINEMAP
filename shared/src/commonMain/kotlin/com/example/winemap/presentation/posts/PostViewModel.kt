package com.example.winemap.presentation.posts

import com.example.winemap.domain.usecases.CreatePostUseCase
import com.example.winemap.domain.usecases.GetWineryPostsUseCase
import com.example.winemap.domain.repository.PostRepository
import com.example.winemap.presentation.BaseViewModel
import com.example.winemap.utils.NetworkUtils
import com.example.winemap.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val createPostUseCase: CreatePostUseCase,
    private val getWineryPostsUseCase: GetWineryPostsUseCase,
    private val postRepository: PostRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    // Load posts for a winery
    fun loadWineryPosts(wineryName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = getWineryPostsUseCase.execute(wineryName)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        posts = result.data,
                        isLoading = false,
                        selectedWinery = wineryName
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
                is Result.Loading -> {
                    // Stay loading
                }
            }
        }
    }

    // Load user's posts
    fun loadUserPosts(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = postRepository.getPostsByUser(userId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        posts = result.data,
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
                is Result.Loading -> {
                    // Stay loading
                }
            }
        }
    }

    // Create Post form actions
    fun updateNewPostWinery(winery: String) {
        _uiState.value = _uiState.value.copy(
            newPostWinery = winery,
            isCreatePostValid = validateCreatePostForm(
                winery,
                _uiState.value.newPostContent,
                _uiState.value.newPostRating
            )
        )
    }

    fun updateNewPostContent(content: String) {
        _uiState.value = _uiState.value.copy(
            newPostContent = content,
            isCreatePostValid = validateCreatePostForm(
                _uiState.value.newPostWinery,
                content,
                _uiState.value.newPostRating
            )
        )
    }

    fun updateNewPostRating(rating: Int) {
        _uiState.value = _uiState.value.copy(
            newPostRating = rating,
            isCreatePostValid = validateCreatePostForm(
                _uiState.value.newPostWinery,
                _uiState.value.newPostContent,
                rating
            )
        )
    }

    fun updateNewPostImage(imagePath: String?) {
        _uiState.value = _uiState.value.copy(newPostImagePath = imagePath)
    }

    fun createPost() {
        val currentState = _uiState.value
        if (!currentState.isCreatePostValid) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = createPostUseCase.execute(
                wineryName = currentState.newPostWinery,
                content = currentState.newPostContent,
                rating = currentState.newPostRating,
                imagePath = currentState.newPostImagePath
            )) {
                is Result.Success -> {
                    // Reset form and refresh posts
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        newPostWinery = "",
                        newPostContent = "",
                        newPostRating = 0,
                        newPostImagePath = null,
                        isCreatePostValid = false
                    )

                    // Refresh posts for the selected winery
                    if (currentState.selectedWinery.isNotEmpty()) {
                        loadWineryPosts(currentState.selectedWinery)
                    }
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
                is Result.Loading -> {
                    // Stay loading
                }
            }
        }
    }

    fun clearAuthError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetCreatePostForm() {
        _uiState.value = _uiState.value.copy(
            newPostWinery = "",
            newPostContent = "",
            newPostRating = 0,
            newPostImagePath = null,
            isCreatePostValid = false
        )
    }

    // Validation
    private fun validateCreatePostForm(winery: String, content: String, rating: Int): Boolean {
        return winery.isNotBlank() &&
                NetworkUtils.validatePostContent(content) &&
                NetworkUtils.validateRating(rating)
    }
}
