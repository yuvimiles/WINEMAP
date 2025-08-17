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
        if (!currentState.isSignInValid) {
            println("DEBUG AuthViewModel: Sign in form is not valid")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            println("DEBUG AuthViewModel: Starting sign in with email: ${currentState.signInEmail}")

            when (val result = userRepository.signIn(currentState.signInEmail, currentState.signInPassword)) {
                is Result.Success -> {
                    println("DEBUG AuthViewModel: Sign in SUCCESS! User: ${result.data}")
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = true,
                        currentUser = result.data,
                        isLoading = false,
                        signInEmail = "",
                        signInPassword = ""
                    )
                    println("DEBUG AuthViewModel: isAuthenticated set to: ${_uiState.value.isAuthenticated}")
                }
                is Result.Error -> {
                    println("DEBUG AuthViewModel: Sign in ERROR: ${result.exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message ?: "Login failed. Please check your credentials."
                    )
                }
                is Result.Loading -> {
                    println("DEBUG AuthViewModel: Sign in still loading...")
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
        if (!currentState.isSignUpValid) {
            println("DEBUG AuthViewModel: Sign up form is not valid")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            println("DEBUG AuthViewModel: Starting sign up process...")

            try {
                when (val result = userRepository.signUp(
                    currentState.signUpEmail,
                    currentState.signUpPassword,
                    currentState.signUpUsername
                )) {
                    is Result.Success -> {
                        println("DEBUG AuthViewModel: Sign up SUCCESS! User: ${result.data}")
                        _uiState.value = _uiState.value.copy(
                            isAuthenticated = true,
                            currentUser = result.data,
                            isLoading = false,
                            signUpEmail = "",
                            signUpPassword = "",
                            signUpUsername = "",
                            signUpConfirmPassword = ""
                        )
                        println("DEBUG AuthViewModel: isAuthenticated set to: ${_uiState.value.isAuthenticated}")
                    }
                    is Result.Error -> {
                        println("DEBUG AuthViewModel: Sign up ERROR: ${result.exception}")
                        println("DEBUG AuthViewModel: Exception message: ${result.exception.message}")

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.exception.message ?: "Registration failed. Please try again."
                        )
                    }
                    is Result.Loading -> {
                        println("DEBUG AuthViewModel: Sign up still loading...")
                    }
                }
            } catch (e: Exception) {
                println("DEBUG AuthViewModel: Unexpected exception: ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Registration failed. Please try again."
                )
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

    // Forgot Password Dialog Actions
    fun showForgotPasswordDialog() {
        _uiState.value = _uiState.value.copy(
            showForgotPasswordDialog = true,
            forgotPasswordEmail = "",
            forgotPasswordMessage = null
        )
    }

    fun hideForgotPasswordDialog() {
        _uiState.value = _uiState.value.copy(
            showForgotPasswordDialog = false,
            forgotPasswordEmail = "",
            isForgotPasswordLoading = false,
            forgotPasswordMessage = null
        )
    }

    fun updateForgotPasswordEmail(email: String) {
        _uiState.value = _uiState.value.copy(forgotPasswordEmail = email)
    }

    override fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun sendPasswordReset() {
        val email = _uiState.value.forgotPasswordEmail
        if (!NetworkUtils.validateEmail(email)) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isForgotPasswordLoading = true)

            // Simulate API call (replace with real Firebase call later)
            kotlinx.coroutines.delay(1500)

            _uiState.value = _uiState.value.copy(
                isForgotPasswordLoading = false,
                forgotPasswordMessage = "Password reset link sent to your email!"
            )

            // Auto close dialog after 2 seconds
            kotlinx.coroutines.delay(2000)
            hideForgotPasswordDialog()
        }
    }

}