package com.apptolast.greenhouse.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.greenhouse.admin.data.model.DashboardStats
import com.apptolast.greenhouse.admin.data.model.DeviceBreakdown
import com.apptolast.greenhouse.admin.data.model.StatCard
import com.apptolast.greenhouse.admin.data.model.StatCardIcon
import com.apptolast.greenhouse.admin.data.model.StatCardSubtitleColor
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
            is DashboardEvent.DismissError -> dismissError()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Call getDashboardStats() ONCE - this is the expensive call that was being called 3x before
            val dashboardStatsResult = repository.getDashboardStats()

            // These calls are cheap (static or use cache)
            val menuDeferred = async { repository.getMenuItems() }
            val recentAlertsDeferred = async { repository.getRecentAlerts() } // Uses cached alerts
            val recentClientsDeferred = async { repository.getRecentClients() }

            val menuResult = menuDeferred.await()
            val recentAlertsResult = recentAlertsDeferred.await()
            val recentClientsResult = recentClientsDeferred.await()

            val dashboardStats = dashboardStatsResult.getOrNull()

            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    // Derive statCards from dashboardStats instead of separate API call
                    statCards = dashboardStats?.toStatCards() ?: emptyList(),
                    menuItems = menuResult.getOrDefault(emptyList()),
                    dashboardStats = dashboardStats,
                    recentAlerts = recentAlertsResult.getOrDefault(emptyList()),
                    recentClients = recentClientsResult.getOrDefault(emptyList()),
                    deviceBreakdown = dashboardStats?.let {
                        DeviceBreakdown(
                            sensors = it.sensorCount,
                            actuators = it.actuatorCount
                        )
                    } ?: DeviceBreakdown(),
                    error = dashboardStatsResult.exceptionOrNull()?.message
                        ?: menuResult.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun refreshDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            // Call getDashboardStats() ONCE - derive statCards and alertCount from it
            val dashboardStatsResult = repository.getDashboardStats()

            // These calls are cheap (use cache)
            val recentAlertsDeferred = async { repository.getRecentAlerts() }
            val recentClientsDeferred = async { repository.getRecentClients() }

            val recentAlertsResult = recentAlertsDeferred.await()
            val recentClientsResult = recentClientsDeferred.await()

            val dashboardStats = dashboardStatsResult.getOrNull()

            _uiState.update { currentState ->
                currentState.copy(
                    isRefreshing = false,
                    statCards = dashboardStats?.toStatCards() ?: currentState.statCards,
                    dashboardStats = dashboardStats ?: currentState.dashboardStats,
                    recentAlerts = recentAlertsResult.getOrDefault(currentState.recentAlerts),
                    recentClients = recentClientsResult.getOrDefault(currentState.recentClients),
                    deviceBreakdown = dashboardStats?.let {
                        DeviceBreakdown(
                            sensors = it.sensorCount,
                            actuators = it.actuatorCount
                        )
                    } ?: currentState.deviceBreakdown,
                    error = dashboardStatsResult.exceptionOrNull()?.message
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

    private fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Converts DashboardStats to a list of StatCards for UI display.
     * This avoids a separate API call since we already have the stats.
     */
    private fun DashboardStats.toStatCards(): List<StatCard> = listOf(
        StatCard(
            id = "clients",
            title = "Total Clients",
            value = totalClients.toString(),
            subtitle = "$activeClients activos",
            subtitleColor = StatCardSubtitleColor.SUCCESS,
            icon = StatCardIcon.PEOPLE
        ),
        StatCard(
            id = "greenhouses",
            title = "Total Greenhouses",
            value = totalGreenhouses.toString(),
            subtitle = if (totalGreenhouses > 0) {
                val percentage = (activeGreenhouses * 100) / totalGreenhouses
                "$percentage% Active"
            } else "0% Active",
            subtitleColor = StatCardSubtitleColor.SUCCESS,
            icon = StatCardIcon.GREENHOUSE
        ),
        StatCard(
            id = "devices",
            title = "Active Devices",
            value = totalDevices.toString(),
            subtitle = "$sensorCount sensors, $actuatorCount actuators",
            subtitleColor = StatCardSubtitleColor.SUCCESS,
            icon = StatCardIcon.DEVICES
        ),
        StatCard(
            id = "alerts",
            title = "Active Alerts",
            value = activeAlerts.toString(),
            subtitle = if (criticalAlerts > 0) {
                "$criticalAlerts critical"
            } else "No critical alerts",
            subtitleColor = if (criticalAlerts > 0) {
                StatCardSubtitleColor.WARNING
            } else StatCardSubtitleColor.SUCCESS,
            icon = StatCardIcon.ALERT
        )
    )
}
