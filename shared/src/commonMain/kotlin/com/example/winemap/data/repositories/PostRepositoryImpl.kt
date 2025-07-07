package com.example.winemap.data.repositories

import com.example.winemap.domain.models.Post
import com.example.winemap.domain.repository.PostRepository
import com.example.winemap.data.remote.PostService
import com.example.winemap.utils.Result

class PostRepositoryImpl(
    private val postService: PostService
) : PostRepository {

    override suspend fun createPost(post: Post): Result<Post> {
        return postService.createPost(post)
    }

    override suspend fun getPostById(postId: String): Result<Post> {
        return postService.getPostById(postId)
    }

    override suspend fun getPostsByWinery(wineryName: String): Result<List<Post>> {
        return postService.getPostsByWinery(wineryName)
    }

    override suspend fun getPostsByUser(userId: String): Result<List<Post>> {
        return postService.getPostsByUser(userId)
    }

    override suspend fun updatePost(post: Post): Result<Post> {
        return postService.updatePost(post)
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return postService.deletePost(postId)
    }

    override suspend fun uploadImage(imagePath: String): Result<String> {
        return postService.uploadImage(imagePath)
    }
}