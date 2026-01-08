package com.apptolast.greenhouse.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.greenhouse.admin.data.model.DeviceBreakdown
import com.apptolast.greenhouse.admin.domain.repository.DashboardRepository
import kotlinx.coroutines.async
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

            // Load all data in parallel
            val statsDeferred = async { repository.getStatCards() }
            val menuDeferred = async { repository.getMenuItems() }
            val alertCountDeferred = async { repository.getAlertCount() }
            val dashboardStatsDeferred = async { repository.getDashboardStats() }
            val recentAlertsDeferred = async { repository.getRecentAlerts() }
            val recentClientsDeferred = async { repository.getRecentClients() }

            val statsResult = statsDeferred.await()
            val menuResult = menuDeferred.await()
            val alertCountResult = alertCountDeferred.await()
            val dashboardStatsResult = dashboardStatsDeferred.await()
            val recentAlertsResult = recentAlertsDeferred.await()
            val recentClientsResult = recentClientsDeferred.await()

            val dashboardStats = dashboardStatsResult.getOrNull()

            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    statCards = statsResult.getOrDefault(emptyList()),
                    menuItems = menuResult.getOrDefault(emptyList()),
                    alertCount = alertCountResult.getOrDefault(0),
                    dashboardStats = dashboardStats,
                    recentAlerts = recentAlertsResult.getOrDefault(emptyList()),
                    recentClients = recentClientsResult.getOrDefault(emptyList()),
                    deviceBreakdown = dashboardStats?.let {
                        DeviceBreakdown(
                            sensors = it.sensorCount,
                            actuators = it.actuatorCount
                        )
                    } ?: DeviceBreakdown(),
                    error = statsResult.exceptionOrNull()?.message
                        ?: menuResult.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun refreshDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            // Refresh all data in parallel
            val statsDeferred = async { repository.getStatCards() }
            val alertCountDeferred = async { repository.getAlertCount() }
            val dashboardStatsDeferred = async { repository.getDashboardStats() }
            val recentAlertsDeferred = async { repository.getRecentAlerts() }
            val recentClientsDeferred = async { repository.getRecentClients() }

            val statsResult = statsDeferred.await()
            val alertCountResult = alertCountDeferred.await()
            val dashboardStatsResult = dashboardStatsDeferred.await()
            val recentAlertsResult = recentAlertsDeferred.await()
            val recentClientsResult = recentClientsDeferred.await()

            val dashboardStats = dashboardStatsResult.getOrNull()

            _uiState.update { currentState ->
                currentState.copy(
                    isRefreshing = false,
                    statCards = statsResult.getOrDefault(currentState.statCards),
                    alertCount = alertCountResult.getOrDefault(currentState.alertCount),
                    dashboardStats = dashboardStats ?: currentState.dashboardStats,
                    recentAlerts = recentAlertsResult.getOrDefault(currentState.recentAlerts),
                    recentClients = recentClientsResult.getOrDefault(currentState.recentClients),
                    deviceBreakdown = dashboardStats?.let {
                        DeviceBreakdown(
                            sensors = it.sensorCount,
                            actuators = it.actuatorCount
                        )
                    } ?: currentState.deviceBreakdown,
                    error = statsResult.exceptionOrNull()?.message
                )
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
