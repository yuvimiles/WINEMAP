package com.example.winemap.di


import com.example.winemap.domain.repository.PostRepository
import com.example.winemap.domain.repository.UserRepository
import com.example.winemap.domain.repository.LocationRepository
import com.example.winemap.utils.Result
import com.example.winemap.domain.models.User
import com.example.winemap.domain.models.Post
import com.example.winemap.domain.models.Location
import com.example.winemap.domain.models.WineryInfo

class TestContainer {

    // Mock Repositories for testing
    val mockUserRepository = object : UserRepository {
        override suspend fun signIn(email: String, password: String): Result<User> {
            return Result.Success(User(id = "test", username = "testuser", email = email))
        }

        override suspend fun signUp(email: String, password: String, username: String): Result<User> {
            return Result.Success(User(id = "test", username = username, email = email))
        }

        override suspend fun signOut(): Result<Unit> = Result.Success(Unit)

        override suspend fun getCurrentUser(): Result<User?> {
            return Result.Success(User(id = "test", username = "testuser", email = "test@test.com"))
        }

        override suspend fun updateUser(user: User): Result<User> = Result.Success(user)

        override suspend fun uploadProfileImage(imagePath: String): Result<String> {
            return Result.Success("test_image_url")
        }
    }

    val mockPostRepository = object : PostRepository {
        override suspend fun createPost(post: Post): Result<Post> = Result.Success(post)
        override suspend fun getPostById(postId: String): Result<Post> = Result.Success(Post())
        override suspend fun getPostsByWinery(wineryName: String): Result<List<Post>> = Result.Success(emptyList())
        override suspend fun getPostsByUser(userId: String): Result<List<Post>> = Result.Success(emptyList())
        override suspend fun updatePost(post: Post): Result<Post> = Result.Success(post)
        override suspend fun deletePost(postId: String): Result<Unit> = Result.Success(Unit)
        override suspend fun uploadImage(imagePath: String): Result<String> = Result.Success("test_url")
    }

    val mockLocationRepository = object : LocationRepository {
        override suspend fun getCurrentLocation(): Result<Location> {
            return Result.Success(Location(31.7683, 35.2137, "Test Location"))
        }
        override suspend fun checkLocationPermission(): Result<Boolean> = Result.Success(true)
        override suspend fun requestLocationPermission(): Result<Boolean> = Result.Success(true)
        override suspend fun getNearbyWineries(location: Location, radiusKm: Double): Result<List<WineryInfo>> {
            return Result.Success(emptyList())
        }
    }
}