package com.apptolast.greenhouse.admin.data.model

/**
 * Represents a single statistic card displayed on the dashboard.
 */
data class StatCard(
    val id: String,
    val title: String,
    val value: String,
    val subtitle: String,
    val subtitleColor: StatCardSubtitleColor = StatCardSubtitleColor.DEFAULT,
    val icon: StatCardIcon
)

/**
 * Color variants for stat card subtitles.
 */
enum class StatCardSubtitleColor {
    DEFAULT,    // Primary green for positive changes
    SUCCESS,    // Green for good status indicators
    WARNING     // Orange/Yellow for alerts requiring attention
}

/**
 * Icons available for stat cards.
 */
enum class StatCardIcon {
    PEOPLE,
    GREENHOUSE,
    DEVICES,
    ALERT,
    USERS
}

/**
 * Represents a menu item in the sidebar navigation.
 */
data class MenuItem(
    val id: String,
    val title: String,
    val icon: MenuIcon,
    val route: String
)

/**
 * Icons available for menu items.
 */
enum class MenuIcon {
    DASHBOARD,
    CLIENTS,
    SETTINGS
}

// ============================================
// Dashboard Statistics Models
// ============================================

/**
 * Aggregated dashboard statistics from all tenants.
 */
data class DashboardStats(
    val totalClients: Int,
    val activeClients: Int,
    val totalGreenhouses: Int,
    val activeGreenhouses: Int,
    val totalDevices: Int,
    val sensorCount: Int,
    val actuatorCount: Int,
    val activeAlerts: Int,
    val criticalAlerts: Int,
    val totalUsers: Int
)

/**
 * Breakdown of devices by category.
 */
data class DeviceBreakdown(
    val sensors: Int = 0,
    val actuators: Int = 0
) {
    val total: Int get() = sensors + actuators
}

/**
 * Represents a recent alert for dashboard display.
 */
data class RecentAlert(
    val id: Long,
    val code: String,
    val tenantId: Long,
    val tenantName: String,
    val greenhouseName: String?,
    val message: String,
    val severityName: String?,
    val severityLevel: Short?,
    val createdAt: String
)

/**
 * Represents a recent client for dashboard display.
 */
data class RecentClient(
    val id: Long,
    val code: String,
    val name: String,
    val province: String?,
    val isActive: Boolean
)
