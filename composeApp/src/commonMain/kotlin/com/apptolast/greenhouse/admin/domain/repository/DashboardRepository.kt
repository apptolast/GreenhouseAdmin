package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.DashboardStats
import com.apptolast.greenhouse.admin.data.model.MenuItem
import com.apptolast.greenhouse.admin.data.model.RecentAlert
import com.apptolast.greenhouse.admin.data.model.RecentClient
import com.apptolast.greenhouse.admin.data.model.StatCard

/**
 * Repository interface for dashboard data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface DashboardRepository {
    /**
     * Fetches dashboard statistics as stat cards.
     * @return Result containing list of StatCard or error
     */
    suspend fun getStatCards(): Result<List<StatCard>>

    /**
     * Fetches menu items for sidebar navigation.
     * @return Result containing list of MenuItem or error
     */
    suspend fun getMenuItems(): Result<List<MenuItem>>

    /**
     * Fetches aggregated dashboard statistics from all tenants.
     * @return Result containing DashboardStats or error
     */
    suspend fun getDashboardStats(): Result<DashboardStats>

    /**
     * Fetches recent unresolved alerts across all tenants.
     * @param limit Maximum number of alerts to return
     * @return Result containing list of RecentAlert or error
     */
    suspend fun getRecentAlerts(limit: Int = 5): Result<List<RecentAlert>>

    /**
     * Fetches recent clients.
     * @param limit Maximum number of clients to return
     * @return Result containing list of RecentClient or error
     */
    suspend fun getRecentClients(limit: Int = 5): Result<List<RecentClient>>
}
