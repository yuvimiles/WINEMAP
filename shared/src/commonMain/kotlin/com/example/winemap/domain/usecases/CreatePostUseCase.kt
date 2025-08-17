package com.example.winemap.domain.usecases

import com.example.winemap.domain.models.Post
import com.example.winemap.domain.repository.PostRepository
import com.example.winemap.domain.repository.UserRepository
import com.example.winemap.utils.Result
import com.example.winemap.utils.ApiException
import com.example.winemap.utils.NetworkUtils

class CreatePostUseCase(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {
    suspend fun execute(
        wineryName: String,
        content: String,
        rating: Int,
        imagePath: String?
    ): Result<Post> {

        // Validate input
        if (wineryName.isBlank()) {
            return Result.Error(ApiException.ValidationException)
        }

        if (!NetworkUtils.validatePostContent(content)) {
            return Result.Error(ApiException.ValidationException)
        }

        if (!NetworkUtils.validateRating(rating)) {
            return Result.Error(ApiException.ValidationException)
        }

        // Get current user
        val currentUserResult = userRepository.getCurrentUser()
        if (currentUserResult is Result.Error) {
            return Result.Error(currentUserResult.exception)
        }

        val currentUser = (currentUserResult as Result.Success).data
            ?: return Result.Error(ApiException.AuthException)

        // Upload image if provided
        val imageUrl = if (imagePath != null) {
            when (val uploadResult = postRepository.uploadImage(imagePath)) {
                is Result.Success -> uploadResult.data
                is Result.Error -> return Result.Error(uploadResult.exception)
                is Result.Loading -> ""
            }
        } else {
            ""
        }

        // Create post
        val post = Post(
            userId = currentUser.id,
            userName = currentUser.username,
            userProfileImage = currentUser.profileImageUrl,
            wineryName = wineryName,
            content = content,
            rating = rating,
            imageUrl = imageUrl
        )

        return postRepository.createPost(post)
    }
}