package com.apptolast.greenhouse.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.greenhouse.admin.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Dashboard screen following MVVM+MVI pattern.
 */
class DashboardViewModel(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())

    /**
     * Exposes immutable StateFlow for UI consumption.
     */
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        onEvent(DashboardEvent.LoadDashboard)
    }

    /**
     * Handles all UI events following MVI pattern.
     * Single entry point for all user interactions.
     */
    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.LoadDashboard -> loadDashboard()
            is DashboardEvent.RefreshDashboard -> refreshDashboard()
            is DashboardEvent.OnSearchQueryChanged -> updateSearchQuery(event.query)
            is DashboardEvent.OnMenuItemSelected -> selectMenuItem(event.itemId)
            is DashboardEvent.OnAlertIconClicked -> handleAlertClick()
            is DashboardEvent.DismissError -> dismissError()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load all data
            val statsResult = repository.getStatCards()
            val menuResult = repository.getMenuItems()
            val alertResult = repository.getAlertCount()

            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    statCards = statsResult.getOrDefault(emptyList()),
                    menuItems = menuResult.getOrDefault(emptyList()),
                    alertCount = alertResult.getOrDefault(0),
                    error = statsResult.exceptionOrNull()?.message
                        ?: menuResult.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun refreshDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            repository.getStatCards()
                .onSuccess { stats ->
                    _uiState.update { it.copy(statCards = stats, isRefreshing = false, error = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isRefreshing = false) }
                }

            repository.getAlertCount()
                .onSuccess { count ->
                    _uiState.update { it.copy(alertCount = count) }
                }
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun selectMenuItem(itemId: String) {
        _uiState.update { it.copy(selectedMenuId = itemId) }
    }

    private fun handleAlertClick() {
        // Navigation to alerts will be handled by observing selectedMenuId
        // or through a separate navigation event channel if needed
    }

    private fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
