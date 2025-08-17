package com.example.winemap.utils

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

sealed class ApiException(message: String) : Exception(message) {
    object NetworkException : ApiException("Network error - check your connection")
    object AuthException : ApiException("Authentication error - please login again")
    object ValidationException : ApiException("Invalid data")
    object NotFoundException : ApiException("Data not found")
    class UnknownException(message: String) : ApiException(message)
}

object NetworkUtils {

    suspend fun <T> safeApiCall(action: suspend () -> T): Result<T> {
        return try {
            Result.Success(action())
        } catch (e: Exception) {
            Result.Error(
                when (e) {
                    is ApiException -> e
                    else -> ApiException.UnknownException(e.message ?: "Unknown error")
                }
            )
        }
    }

    fun validateEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }

    fun validateUsername(username: String): Boolean {
        return username.length >= Constants.MIN_USERNAME_LENGTH &&
                username.length <= Constants.MAX_USERNAME_LENGTH &&
                username.matches("^[a-zA-Z0-9_]+$".toRegex())
    }

    fun validatePostContent(content: String): Boolean {
        return content.length >= Constants.MIN_POST_CONTENT_LENGTH &&
                content.length <= Constants.MAX_POST_CONTENT_LENGTH
    }

    fun validateRating(rating: Int): Boolean {
        return rating in Constants.MIN_RATING..Constants.MAX_RATING
    }
}