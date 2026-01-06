package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

// ============================================
// API Response DTOs - Catalog
// ============================================

/**
 * Response DTO for alert types from /catalog/alert-types endpoint.
 */
@Serializable
data class AlertTypeResponse(
    val id: Short,
    val name: String,
    val description: String? = null
)

/**
 * Response DTO for alert severities from /catalog/alert-severities endpoint.
 */
@Serializable
data class AlertSeverityResponse(
    val id: Short,
    val name: String,
    val level: Short,
    val description: String? = null,
    val color: String? = null,
    val requiresAction: Boolean = false,
    val notificationDelayMinutes: Int = 0
)

// ============================================
// API Response DTOs - Alerts
// ============================================

/**
 * Response DTO for alerts from /tenants/{tenantId}/alerts endpoint.
 */
@Serializable
data class AlertResponse(
    val id: String,
    val tenantId: String,
    val greenhouseId: String,
    val greenhouseName: String? = null,
    val alertTypeId: Short? = null,
    val alertTypeName: String? = null,
    val severityId: Short? = null,
    val severityName: String? = null,
    val severityLevel: Short? = null,
    val message: String,
    val isResolved: Boolean = false,
    val resolvedAt: String? = null,
    val resolvedByUserId: String? = null,
    val resolvedByUserName: String? = null,
    val createdAt: String,
    val updatedAt: String? = null
)

// ============================================
// API Request DTOs
// ============================================

/**
 * Request DTO for creating a new alert.
 */
@Serializable
data class AlertCreateRequest(
    val greenhouseId: String,
    val alertTypeId: Short? = null,
    val severityId: Short? = null,
    val message: String
)

/**
 * Request DTO for updating an alert.
 */
@Serializable
data class AlertUpdateRequest(
    val alertTypeId: Short? = null,
    val severityId: Short? = null,
    val message: String? = null
)

/**
 * Request DTO for resolving an alert.
 */
@Serializable
data class AlertResolveRequest(
    val resolvedByUserId: String? = null
)

// ============================================
// Domain Models
// ============================================

/**
 * Domain model for alert type catalog.
 */
data class AlertType(
    val id: Short,
    val name: String,
    val description: String?
)

/**
 * Domain model for alert severity catalog.
 */
data class AlertSeverityCatalog(
    val id: Short,
    val name: String,
    val level: Short,
    val description: String?,
    val color: String?,
    val requiresAction: Boolean
)

/**
 * Domain model for an alert.
 */
data class Alert(
    val id: String,
    val tenantId: String,
    val greenhouseId: String,
    val greenhouseName: String?,
    val alertTypeId: Short?,
    val alertTypeName: String?,
    val severityId: Short?,
    val severityName: String?,
    val severityLevel: Short?,
    val message: String,
    val isResolved: Boolean,
    val resolvedAt: String?,
    val resolvedByUserName: String?,
    val createdAt: String
) {
    /**
     * Returns the initials (first two letters of message) for avatar display.
     */
    val initials: String
        get() = message.take(2).uppercase()
}

// ============================================
// Extension Functions - Mappers
// ============================================

/**
 * Converts AlertTypeResponse DTO to domain model.
 */
fun AlertTypeResponse.toDomain() = AlertType(
    id = id,
    name = name,
    description = description
)

/**
 * Converts AlertSeverityResponse DTO to domain model.
 */
fun AlertSeverityResponse.toDomain() = AlertSeverityCatalog(
    id = id,
    name = name,
    level = level,
    description = description,
    color = color,
    requiresAction = requiresAction
)

/**
 * Converts AlertResponse DTO to domain model.
 */
fun AlertResponse.toDomain() = Alert(
    id = id,
    tenantId = tenantId,
    greenhouseId = greenhouseId,
    greenhouseName = greenhouseName,
    alertTypeId = alertTypeId,
    alertTypeName = alertTypeName,
    severityId = severityId,
    severityName = severityName,
    severityLevel = severityLevel,
    message = message,
    isResolved = isResolved,
    resolvedAt = resolvedAt,
    resolvedByUserName = resolvedByUserName,
    createdAt = createdAt
)

// ============================================
// Catalog Request DTOs
// ============================================

/**
 * Request DTO for creating an Alert Type.
 * Note: Backend requires ID to be provided manually.
 */
@Serializable
data class AlertTypeCreateRequest(
    val id: Short,
    val name: String,
    val description: String? = null
)

/**
 * Request DTO for updating an Alert Type.
 */
@Serializable
data class AlertTypeUpdateRequest(
    val name: String? = null,
    val description: String? = null
)

/**
 * Request DTO for creating an Alert Severity.
 * Note: Backend requires ID to be provided manually.
 */
@Serializable
data class AlertSeverityCreateRequest(
    val id: Short,
    val name: String,
    val level: Short,
    val description: String? = null,
    val color: String? = null,
    val requiresAction: Boolean = false,
    val notificationDelayMinutes: Int = 0
)

/**
 * Request DTO for updating an Alert Severity.
 */
@Serializable
data class AlertSeverityUpdateRequest(
    val name: String? = null,
    val level: Short? = null,
    val description: String? = null,
    val color: String? = null,
    val requiresAction: Boolean? = null,
    val notificationDelayMinutes: Int? = null
)
