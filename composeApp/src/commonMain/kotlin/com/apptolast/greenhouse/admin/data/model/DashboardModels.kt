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
    ALERT
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
