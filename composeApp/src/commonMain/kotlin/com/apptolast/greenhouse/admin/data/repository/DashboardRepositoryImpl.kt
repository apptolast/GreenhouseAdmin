package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.MenuIcon
import com.apptolast.greenhouse.admin.data.model.MenuItem
import com.apptolast.greenhouse.admin.data.model.StatCard
import com.apptolast.greenhouse.admin.data.model.StatCardIcon
import com.apptolast.greenhouse.admin.data.model.StatCardSubtitleColor
import com.apptolast.greenhouse.admin.domain.repository.DashboardRepository
import kotlinx.coroutines.delay

/**
 * Mock implementation of DashboardRepository.
 * Provides hardcoded data for development. Replace with real API calls later.
 */
class DashboardRepositoryImpl : DashboardRepository {

    override suspend fun getStatCards(): Result<List<StatCard>> = runCatching {
        // Simulate network delay
        delay(500)

        listOf(
            StatCard(
                id = "clients",
                title = "Total Clients",
                value = "34",
                subtitle = "+2 this month",
                subtitleColor = StatCardSubtitleColor.DEFAULT,
                icon = StatCardIcon.PEOPLE
            ),
            StatCard(
                id = "greenhouses",
                title = "Total Greenhouses",
                value = "128",
                subtitle = "98% Active",
                subtitleColor = StatCardSubtitleColor.SUCCESS,
                icon = StatCardIcon.GREENHOUSE
            ),
            StatCard(
                id = "devices",
                title = "Active Devices",
                value = "1,050",
                subtitle = "All online",
                subtitleColor = StatCardSubtitleColor.SUCCESS,
                icon = StatCardIcon.DEVICES
            ),
            StatCard(
                id = "alerts",
                title = "Active Alerts",
                value = "5",
                subtitle = "Requires Attention",
                subtitleColor = StatCardSubtitleColor.WARNING,
                icon = StatCardIcon.ALERT
            )
        )
    }

    override suspend fun getMenuItems(): Result<List<MenuItem>> = runCatching {
        listOf(
            MenuItem(
                id = "dashboard",
                title = "Dashboard",
                icon = MenuIcon.DASHBOARD,
                route = "dashboard"
            ),
            MenuItem(
                id = "clients",
                title = "Clients",
                icon = MenuIcon.CLIENTS,
                route = "clients"
            ),
            MenuItem(
                id = "settings",
                title = "Settings",
                icon = MenuIcon.SETTINGS,
                route = "settings"
            )
        )
    }

    override suspend fun getAlertCount(): Result<Int> = runCatching {
        delay(200)
        5
    }
}
