package com.apptolast.greenhouse.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.greenhouse.admin.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Login screen following MVVM+MVI pattern.
 */
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    /**
     * Exposes immutable StateFlow for UI consumption.
     */
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Validate existing session with backend
        validateSession()
    }

    /**
     * Validates the current session with the backend.
     * If the token exists but is expired, the user will need to log in again.
     */
    private fun validateSession() {
        viewModelScope.launch {
            // First check if there's a token at all
            if (!authRepository.isAuthenticated()) {
                return@launch
            }

            // Token exists, validate with backend
            _uiState.update { it.copy(isValidatingSession = true) }

            val isValid = authRepository.validateSession()

            _uiState.update {
                it.copy(
                    isValidatingSession = false,
                    isLoginSuccessful = isValid
                )
            }
        }
    }

    /**
     * Handles all UI events following MVI pattern.
     * Single entry point for all user interactions.
     */
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnUsernameChanged -> updateUsername(event.username)
            is LoginEvent.OnPasswordChanged -> updatePassword(event.password)
            is LoginEvent.OnLoginClicked -> performLogin()
            is LoginEvent.OnTogglePasswordVisibility -> togglePasswordVisibility()
            is LoginEvent.DismissError -> dismissError()
            is LoginEvent.OnNavigationHandled -> handleNavigationComplete()
        }
    }

    private fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username, error = null) }
    }

    private fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    private fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun performLogin() {
        val currentState = _uiState.value
        if (!currentState.isLoginEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            authRepository.login(
                username = currentState.username.trim(),
                password = currentState.password
            )
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Login failed. Please try again."
                        )
                    }
                }
        }
    }

    private fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun handleNavigationComplete() {
        _uiState.update { it.copy(isLoginSuccessful = false) }
    }
}
