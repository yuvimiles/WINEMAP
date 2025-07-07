package com.example.winemap.data.remote

import com.example.winemap.domain.models.Post
import com.example.winemap.utils.Constants
import com.example.winemap.utils.Result
import com.example.winemap.utils.ApiException
import kotlinx.datetime.Clock

class PostService {

    private val firestore = FirebaseClient.firestore
    private val storage = FirebaseClient.storage

    suspend fun createPost(post: Post): Result<Post> {
        return try {
            val postWithId = post.copy(
                id = firestore.collection(Constants.POSTS_COLLECTION).document.id,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )

            firestore.collection(Constants.POSTS_COLLECTION)
                .document(postWithId.id)
                .set(postWithId)

            Result.Success(postWithId)
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Create post failed"))
        }
    }

    suspend fun getPostById(postId: String): Result<Post> {
        return try {
            val document = firestore.collection(Constants.POSTS_COLLECTION)
                .document(postId)
                .get()

            if (document.exists) {
                val post = document.data<Post>()
                Result.Success(post)
            } else {
                Result.Error(ApiException.NotFoundException)
            }
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Get post failed"))
        }
    }

    suspend fun getPostsByWinery(wineryName: String): Result<List<Post>> {
        return try {
            // Get all posts and filter locally for now
            val documents = firestore.collection(Constants.POSTS_COLLECTION).get()

            val posts = documents.documents
                .map { document -> document.data<Post>() }
                .filter { post -> post.wineryName == wineryName }
                .sortedByDescending { post -> post.timestamp }

            Result.Success(posts)
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Get posts failed"))
        }
    }

    suspend fun getPostsByUser(userId: String): Result<List<Post>> {
        return try {
            // Get all posts and filter locally for now
            val documents = firestore.collection(Constants.POSTS_COLLECTION).get()

            val posts = documents.documents
                .map { document -> document.data<Post>() }
                .filter { post -> post.userId == userId }
                .sortedByDescending { post -> post.timestamp }

            Result.Success(posts)
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Get user posts failed"))
        }
    }

    suspend fun updatePost(post: Post): Result<Post> {
        return try {
            firestore.collection(Constants.POSTS_COLLECTION)
                .document(post.id)
                .set(post)

            Result.Success(post)
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Update post failed"))
        }
    }

    suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.POSTS_COLLECTION)
                .document(postId)
                .delete()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Delete post failed"))
        }
    }

    suspend fun uploadImage(imagePath: String): Result<String> {
        return try {
            // TODO: Implement actual image upload
            // For now return placeholder
            Result.Success("https://placeholder-image-url.com")
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Upload image failed"))
        }
    }
}