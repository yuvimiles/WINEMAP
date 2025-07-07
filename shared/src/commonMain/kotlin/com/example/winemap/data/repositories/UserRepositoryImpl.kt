package com.example.winemap.data.repositories

import com.example.winemap.domain.models.User
import com.example.winemap.domain.repository.UserRepository
import com.example.winemap.data.remote.UserService
import com.example.winemap.utils.Result

class UserRepositoryImpl(
    private val userService: UserService
) : UserRepository {

    override suspend fun signIn(email: String, password: String): Result<User> {
        return userService.signIn(email, password)
    }

    override suspend fun signUp(email: String, password: String, username: String): Result<User> {
        return userService.signUp(email, password, username)
    }

    override suspend fun signOut(): Result<Unit> {
        return userService.signOut()
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return userService.getCurrentUser()
    }

    override suspend fun updateUser(user: User): Result<User> {
        return userService.updateUser(user)
    }

    override suspend fun uploadProfileImage(imagePath: String): Result<String> {
        return userService.uploadProfileImage(imagePath)
    }
}