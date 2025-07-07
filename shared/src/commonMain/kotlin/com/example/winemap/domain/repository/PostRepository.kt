package com.example.winemap.domain.repository

import com.example.winemap.domain.models.Post
import com.example.winemap.utils.Result

interface PostRepository {
    suspend fun createPost(post: Post): Result<Post>
    suspend fun getPostById(postId: String): Result<Post>
    suspend fun getPostsByWinery(wineryName: String): Result<List<Post>>
    suspend fun getPostsByUser(userId: String): Result<List<Post>>
    suspend fun updatePost(post: Post): Result<Post>
    suspend fun deletePost(postId: String): Result<Unit>
    suspend fun uploadImage(imagePath: String): Result<String>
}