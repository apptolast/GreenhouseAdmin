package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.MenuItem
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
     * Fetches current alert count for header badge.
     * @return Result containing alert count or error
     */
    suspend fun getAlertCount(): Result<Int>
}
