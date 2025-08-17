package com.example.winemap.domain.repository

import com.example.winemap.domain.models.User
import com.example.winemap.utils.Result

interface UserRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, username: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun updateUser(user: User): Result<User>
    suspend fun uploadProfileImage(imagePath: String): Result<String>
}