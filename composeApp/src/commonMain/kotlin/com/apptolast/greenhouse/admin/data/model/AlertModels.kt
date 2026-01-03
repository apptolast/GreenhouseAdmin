package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Represents an alert in the system.
 */
@Serializable
data class Alert(
    val id: String,
    val title: String,
    val severity: AlertSeverity,
    val status: AlertStatus,
    val createdAt: Long,
    val clientId: String
) {
    /**
     * Returns the initials (first two letters of title) for avatar display.
     */
    val initials: String
        get() = title.take(2).uppercase()
}

/**
 * Severity levels for alerts.
 */
@Serializable
enum class AlertSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Status options for alerts.
 */
@Serializable
enum class AlertStatus {
    UNREAD,
    READ,
    DISMISSED
}
