package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.DashboardStats
import com.apptolast.greenhouse.admin.data.model.MenuIcon
import com.apptolast.greenhouse.admin.data.model.MenuItem
import com.apptolast.greenhouse.admin.data.model.RecentAlert
import com.apptolast.greenhouse.admin.data.model.RecentClient
import com.apptolast.greenhouse.admin.data.model.StatCard
import com.apptolast.greenhouse.admin.data.model.StatCardIcon
import com.apptolast.greenhouse.admin.data.model.StatCardSubtitleColor
import com.apptolast.greenhouse.admin.data.remote.api.AlertsApiService
import com.apptolast.greenhouse.admin.data.remote.api.DevicesApiService
import com.apptolast.greenhouse.admin.data.remote.api.GreenhousesApiService
import com.apptolast.greenhouse.admin.data.remote.api.TenantsApiService
import com.apptolast.greenhouse.admin.data.remote.api.UsersApiService
import com.apptolast.greenhouse.admin.domain.repository.DashboardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

/**
 * Implementation of DashboardRepository that fetches real data from APIs.
 * Aggregates data across all tenants for global statistics.
 */
class DashboardRepositoryImpl(
    private val tenantsApi: TenantsApiService,
    private val greenhousesApi: GreenhousesApiService,
    private val devicesApi: DevicesApiService,
    private val alertsApi: AlertsApiService,
    private val usersApi: UsersApiService
) : DashboardRepository {

    // Category IDs for device classification
    private companion object {
        const val SENSOR_CATEGORY_ID: Short = 1
        const val ACTUATOR_CATEGORY_ID: Short = 2
        const val CRITICAL_SEVERITY_THRESHOLD: Short = 3 // Levels >= 3 are critical
    }

    override suspend fun getStatCards(): Result<List<StatCard>> = runCatching {
        val stats = getDashboardStats().getOrThrow()

        listOf(
            StatCard(
                id = "clients",
                title = "Total Clients",
                value = stats.totalClients.toString(),
                subtitle = "${stats.activeClients} activos",
                subtitleColor = StatCardSubtitleColor.SUCCESS,
                icon = StatCardIcon.PEOPLE
            ),
            StatCard(
                id = "greenhouses",
                title = "Total Greenhouses",
                value = stats.totalGreenhouses.toString(),
                subtitle = if (stats.totalGreenhouses > 0) {
                    val percentage = (stats.activeGreenhouses * 100) / stats.totalGreenhouses
                    "$percentage% Active"
                } else "0% Active",
                subtitleColor = StatCardSubtitleColor.SUCCESS,
                icon = StatCardIcon.GREENHOUSE
            ),
            StatCard(
                id = "devices",
                title = "Active Devices",
                value = stats.totalDevices.toString(),
                subtitle = "${stats.sensorCount} sensors, ${stats.actuatorCount} actuators",
                subtitleColor = StatCardSubtitleColor.SUCCESS,
                icon = StatCardIcon.DEVICES
            ),
            StatCard(
                id = "alerts",
                title = "Active Alerts",
                value = stats.activeAlerts.toString(),
                subtitle = if (stats.criticalAlerts > 0) {
                    "${stats.criticalAlerts} critical"
                } else "No critical alerts",
                subtitleColor = if (stats.criticalAlerts > 0) {
                    StatCardSubtitleColor.WARNING
                } else StatCardSubtitleColor.SUCCESS,
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
        val stats = getDashboardStats().getOrThrow()
        stats.activeAlerts
    }

    override suspend fun getDashboardStats(): Result<DashboardStats> = runCatching {
        // Get all tenants first
        val tenants = tenantsApi.getAllTenants()

        // Initialize counters
        var totalGreenhouses = 0
        var activeGreenhouses = 0
        var totalDevices = 0
        var sensorCount = 0
        var actuatorCount = 0
        var activeAlerts = 0
        var criticalAlerts = 0
        var totalUsers = 0

        // Count active tenants
        val activeClients = tenants.count { it.isActive == true }

        // Aggregate data from all tenants in parallel using supervisorScope
        // to continue even if some tenants fail
        supervisorScope {
            val jobs = tenants.map { tenant ->
                async {
                    try {
                        // Fetch data for this tenant
                        val greenhouses = greenhousesApi.getGreenhousesByTenantId(tenant.id)
                        val devices = devicesApi.getDevicesByTenantId(tenant.id)
                        val alerts = alertsApi.getAlerts(tenant.id)
                        val users = usersApi.getUsersByTenantId(tenant.id)

                        TenantData(
                            greenhouseCount = greenhouses.size,
                            activeGreenhouseCount = greenhouses.count { it.isActive },
                            deviceCount = devices.size,
                            sensors = devices.count { it.categoryId == SENSOR_CATEGORY_ID },
                            actuators = devices.count { it.categoryId == ACTUATOR_CATEGORY_ID },
                            unresolvedAlerts = alerts.count { !it.isResolved },
                            criticalAlerts = alerts.count {
                                !it.isResolved && (it.severityLevel ?: 0) >= CRITICAL_SEVERITY_THRESHOLD
                            },
                            userCount = users.size
                        )
                    } catch (e: Exception) {
                        // If one tenant fails, return zeros and continue
                        TenantData()
                    }
                }
            }

            // Aggregate results
            jobs.awaitAll().forEach { data ->
                totalGreenhouses += data.greenhouseCount
                activeGreenhouses += data.activeGreenhouseCount
                totalDevices += data.deviceCount
                sensorCount += data.sensors
                actuatorCount += data.actuators
                activeAlerts += data.unresolvedAlerts
                criticalAlerts += data.criticalAlerts
                totalUsers += data.userCount
            }
        }

        DashboardStats(
            totalClients = tenants.size,
            activeClients = activeClients,
            totalGreenhouses = totalGreenhouses,
            activeGreenhouses = activeGreenhouses,
            totalDevices = totalDevices,
            sensorCount = sensorCount,
            actuatorCount = actuatorCount,
            activeAlerts = activeAlerts,
            criticalAlerts = criticalAlerts,
            totalUsers = totalUsers
        )
    }

    override suspend fun getRecentAlerts(limit: Int): Result<List<RecentAlert>> = runCatching {
        val tenants = tenantsApi.getAllTenants()
        val tenantMap = tenants.associateBy { it.id }

        // Collect alerts from all tenants
        val allAlerts = mutableListOf<RecentAlert>()

        supervisorScope {
            val jobs = tenants.map { tenant ->
                async {
                    try {
                        alertsApi.getAlerts(tenant.id)
                            .filter { !it.isResolved }
                            .map { alert ->
                                RecentAlert(
                                    id = alert.id,
                                    tenantId = alert.tenantId,
                                    tenantName = tenantMap[alert.tenantId]?.name ?: "Unknown",
                                    greenhouseName = alert.greenhouseName,
                                    message = alert.message,
                                    severityName = alert.severityName,
                                    severityLevel = alert.severityLevel,
                                    createdAt = alert.createdAt
                                )
                            }
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }

            jobs.awaitAll().forEach { alerts ->
                allAlerts.addAll(alerts)
            }
        }

        // Sort by createdAt descending and take limit
        allAlerts
            .sortedByDescending { it.createdAt }
            .take(limit)
    }

    override suspend fun getRecentClients(limit: Int): Result<List<RecentClient>> = runCatching {
        val tenants = tenantsApi.getAllTenants()

        // Map to RecentClient and take most recent
        tenants
            .map { tenant ->
                RecentClient(
                    id = tenant.id,
                    name = tenant.name,
                    province = tenant.province,
                    isActive = tenant.isActive ?: true
                )
            }
            .take(limit)
    }

    /**
     * Internal data class to hold aggregated data per tenant.
     */
    private data class TenantData(
        val greenhouseCount: Int = 0,
        val activeGreenhouseCount: Int = 0,
        val deviceCount: Int = 0,
        val sensors: Int = 0,
        val actuators: Int = 0,
        val unresolvedAlerts: Int = 0,
        val criticalAlerts: Int = 0,
        val userCount: Int = 0
    )
}
