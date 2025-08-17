package com.example.winemap.domain.usecases

import com.example.winemap.domain.models.Post
import com.example.winemap.domain.repository.PostRepository
import com.example.winemap.utils.Result

class GetWineryPostsUseCase(
    private val postRepository: PostRepository
) {
    suspend fun execute(wineryName: String): Result<List<Post>> {
        if (wineryName.isBlank()) {
            return Result.Success(emptyList())
        }

        return postRepository.getPostsByWinery(wineryName)
    }
}