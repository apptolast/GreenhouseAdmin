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
 * ViewModel for the Settings screen following MVVM+MVI pattern.
 */
class SettingsViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())

    /**
     * Exposes immutable StateFlow for UI consumption.
     */
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // Load current user info
        loadUserInfo()
    }

    /**
     * Handles all UI events following MVI pattern.
     */
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnLogoutClicked -> showLogoutConfirmation()
            is SettingsEvent.OnConfirmLogout -> performLogout()
            is SettingsEvent.OnCancelLogout -> cancelLogout()
            is SettingsEvent.OnLogoutComplete -> handleLogoutComplete()
        }
    }

    private fun loadUserInfo() {
        val session = authRepository.getCurrentSession()
        _uiState.update {
            it.copy(
                username = session?.username ?: "",
                roles = session?.roles ?: emptyList()
            )
        }
    }

    private fun showLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutConfirmation = true) }
    }

    private fun cancelLogout() {
        _uiState.update { it.copy(showLogoutConfirmation = false) }
    }

    private fun performLogout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true, showLogoutConfirmation = false) }

            authRepository.logout()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            isLogoutSuccessful = true
                        )
                    }
                }
                .onFailure {
                    // Even on failure, we should logout locally
                    authRepository.clearLocalSession()
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            isLogoutSuccessful = true
                        )
                    }
                }
        }
    }

    private fun handleLogoutComplete() {
        _uiState.update { it.copy(isLogoutSuccessful = false) }
    }
}

/**
 * UI state for the Settings screen.
 */
data class SettingsUiState(
    val username: String = "",
    val roles: List<String> = emptyList(),
    val showLogoutConfirmation: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isLogoutSuccessful: Boolean = false
)

/**
 * Events for the Settings screen.
 */
sealed interface SettingsEvent {
    data object OnLogoutClicked : SettingsEvent
    data object OnConfirmLogout : SettingsEvent
    data object OnCancelLogout : SettingsEvent
    data object OnLogoutComplete : SettingsEvent
}
