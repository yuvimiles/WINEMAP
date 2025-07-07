package com.example.winemap.presentation.auth

import com.example.winemap.domain.models.User

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Sign In form
    val signInEmail: String = "",
    val signInPassword: String = "",

    // Sign Up form
    val signUpEmail: String = "",
    val signUpPassword: String = "",
    val signUpUsername: String = "",
    val signUpConfirmPassword: String = "",

    // Validation
    val isSignInValid: Boolean = false,
    val isSignUpValid: Boolean = false
)