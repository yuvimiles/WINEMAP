package com.example.winemap.data.remote

import com.example.winemap.domain.models.User
import com.example.winemap.utils.Constants
import com.example.winemap.utils.Result
import com.example.winemap.utils.ApiException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.DocumentSnapshot

class UserService {

    private val auth = FirebaseClient.auth
    private val firestore = FirebaseClient.firestore
    private val storage = FirebaseClient.storage

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password)
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val user = getUserFromFirestore(firebaseUser.uid)
                if (user != null) {
                    Result.Success(user)
                } else {
                    Result.Error(ApiException.NotFoundException)
                }
            } else {
                Result.Error(ApiException.AuthException)
            }
        } catch (e: Exception) {
            Result.Error(ApiException.AuthException)
        }
    }

    suspend fun signUp(email: String, password: String, username: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password)
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    username = username,
                    email = email,
                    profileImageUrl = "",
                    bio = ""
                )

                // Save user to Firestore
                firestore.collection(Constants.USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .set(user)

                Result.Success(user)
            } else {
                Result.Error(ApiException.AuthException)
            }
        } catch (e: Exception) {
            Result.Error(ApiException.AuthException)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Sign out failed"))
        }
    }

    suspend fun getCurrentUser(): Result<User?> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val user = getUserFromFirestore(currentUser.uid)
                Result.Success(user)
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Get user failed"))
        }
    }

    suspend fun updateUser(user: User): Result<User> {
        return try {
            firestore.collection(Constants.USERS_COLLECTION)
                .document(user.id)
                .set(user)

            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Update user failed"))
        }
    }

    suspend fun uploadProfileImage(imagePath: String): Result<String> {
        return try {
            val currentUserId = FirebaseClient.getCurrentUserId()
                ?: return Result.Error(ApiException.AuthException)

            val imageRef = storage.reference
                .child("${Constants.PROFILE_IMAGES_PATH}/$currentUserId/profile.jpg")

            // TODO: Implement actual image upload
            // This is a placeholder - actual implementation will depend on platform

            Result.Success("https://placeholder-image-url.com")
        } catch (e: Exception) {
            Result.Error(ApiException.UnknownException(e.message ?: "Upload failed"))
        }
    }

    private suspend fun getUserFromFirestore(userId: String): User? {
        return try {
            val document = firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()

            if (document.exists) {
                document.data<User>()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}