package com.apptolast.greenhouse.admin.presentation.viewmodel

import com.apptolast.greenhouse.admin.data.model.DashboardStats
import com.apptolast.greenhouse.admin.data.model.DeviceBreakdown
import com.apptolast.greenhouse.admin.data.model.MenuItem
import com.apptolast.greenhouse.admin.data.model.RecentAlert
import com.apptolast.greenhouse.admin.data.model.RecentClient
import com.apptolast.greenhouse.admin.data.model.StatCard

/**
 * Represents the complete UI state for the Dashboard screen.
 * Follows MVI pattern with immutable state.
 */
data class DashboardUiState(
    // Loading states
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,

    // Error state
    val error: String? = null,

    // Data states
    val statCards: List<StatCard> = emptyList(),
    val menuItems: List<MenuItem> = emptyList(),
    val alertCount: Int = 0,

    // Additional dashboard data
    val dashboardStats: DashboardStats? = null,
    val recentAlerts: List<RecentAlert> = emptyList(),
    val recentClients: List<RecentClient> = emptyList(),
    val deviceBreakdown: DeviceBreakdown = DeviceBreakdown(),

    // Search state
    val searchQuery: String = "",

    // Navigation state
    val selectedMenuId: String = "dashboard"
) {
    /**
     * Returns true if any content is available to display.
     */
    val hasContent: Boolean
        get() = statCards.isNotEmpty()

    /**
     * Returns true if in error state with no content.
     */
    val isError: Boolean
        get() = error != null && !hasContent

    /**
     * Returns the total users count from stats.
     */
    val totalUsers: Int
        get() = dashboardStats?.totalUsers ?: 0

    /**
     * Returns the count of critical alerts.
     */
    val criticalAlerts: Int
        get() = dashboardStats?.criticalAlerts ?: 0
}
