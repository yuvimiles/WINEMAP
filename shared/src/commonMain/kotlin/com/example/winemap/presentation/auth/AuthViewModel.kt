package com.example.winemap.presentation.auth

import com.example.winemap.domain.repository.UserRepository
import com.example.winemap.presentation.BaseViewModel
import com.example.winemap.utils.NetworkUtils
import com.example.winemap.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            setLoading(true)
            when (val result = userRepository.getCurrentUser()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = result.data != null,
                        currentUser = result.data,
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = false,
                        currentUser = null,
                        isLoading = false
                    )
                }
                is Result.Loading -> {
                    // Stay loading
                }
            }
        }
    }

    // Sign In Actions
    fun updateSignInEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            signInEmail = email,
            isSignInValid = validateSignInForm(email, _uiState.value.signInPassword)
        )
    }

    fun updateSignInPassword(password: String) {
        _uiState.value = _uiState.value.copy(
            signInPassword = password,
            isSignInValid = validateSignInForm(_uiState.value.signInEmail, password)
        )
    }

    fun signIn() {
        val currentState = _uiState.value
        if (!currentState.isSignInValid) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = userRepository.signIn(currentState.signInEmail, currentState.signInPassword)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = true,
                        currentUser = result.data,
                        isLoading = false,
                        signInEmail = "",
                        signInPassword = ""
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
                is Result.Loading -> {
                    // Stay loading
                }
            }
        }
    }

    // Sign Up Actions
    fun updateSignUpEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            signUpEmail = email,
            isSignUpValid = validateSignUpForm(
                email,
                _uiState.value.signUpPassword,
                _uiState.value.signUpUsername,
                _uiState.value.signUpConfirmPassword
            )
        )
    }

    fun updateSignUpPassword(password: String) {
        _uiState.value = _uiState.value.copy(
            signUpPassword = password,
            isSignUpValid = validateSignUpForm(
                _uiState.value.signUpEmail,
                password,
                _uiState.value.signUpUsername,
                _uiState.value.signUpConfirmPassword
            )
        )
    }

    fun updateSignUpUsername(username: String) {
        _uiState.value = _uiState.value.copy(
            signUpUsername = username,
            isSignUpValid = validateSignUpForm(
                _uiState.value.signUpEmail,
                _uiState.value.signUpPassword,
                username,
                _uiState.value.signUpConfirmPassword
            )
        )
    }

    fun updateSignUpConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            signUpConfirmPassword = confirmPassword,
            isSignUpValid = validateSignUpForm(
                _uiState.value.signUpEmail,
                _uiState.value.signUpPassword,
                _uiState.value.signUpUsername,
                confirmPassword
            )
        )
    }

    fun signUp() {
        val currentState = _uiState.value
        if (!currentState.isSignUpValid) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = userRepository.signUp(
                currentState.signUpEmail,
                currentState.signUpPassword,
                currentState.signUpUsername
            )) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = true,
                        currentUser = result.data,
                        isLoading = false,
                        signUpEmail = "",
                        signUpPassword = "",
                        signUpUsername = "",
                        signUpConfirmPassword = ""
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
                is Result.Loading -> {
                    // Stay loading
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            when (userRepository.signOut()) {
                is Result.Success -> {
                    _uiState.value = AuthUiState() // Reset to initial state
                }
                is Result.Error -> {
                    setError("Sign out failed")
                }
                is Result.Loading -> {
                    // Stay current state
                }
            }
        }
    }



    // Validation helpers
    private fun validateSignInForm(email: String, password: String): Boolean {
        return NetworkUtils.validateEmail(email) && password.length >= 6
    }

    private fun validateSignUpForm(email: String, password: String, username: String, confirmPassword: String): Boolean {
        return NetworkUtils.validateEmail(email) &&
                password.length >= 6 &&
                NetworkUtils.validateUsername(username) &&
                password == confirmPassword
    }
}