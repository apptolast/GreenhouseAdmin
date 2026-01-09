package com.apptolast.greenhouse.admin.presentation.viewmodel

/**
 * UI State for the Login screen.
 * Immutable data class representing the current state of the login form.
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isValidatingSession: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
) {
    /**
     * Whether the login button should be enabled.
     */
    val isLoginEnabled: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && !isLoading && !isValidatingSession
}
