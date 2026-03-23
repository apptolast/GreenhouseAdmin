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
    val id: Long,
    val code: String,
    val tenantId: Long,
    val sectorId: Long,
    val sectorCode: String? = null,
    val clientName: String? = null,
    val alertTypeId: Short? = null,
    val alertTypeName: String? = null,
    val severityId: Short? = null,
    val severityName: String? = null,
    val severityLevel: Short? = null,
    val message: String? = null,
    val description: String? = null,
    val isResolved: Boolean = false,
    val resolvedAt: String? = null,
    val resolvedByUserId: Long? = null,
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
    val sectorId: Long,
    val clientName: String? = null,
    val alertTypeId: Short? = null,
    val severityId: Short? = null,
    val message: String? = null,
    val description: String? = null
)

/**
 * Request DTO for updating an alert.
 */
@Serializable
data class AlertUpdateRequest(
    val sectorId: Long? = null,
    val clientName: String? = null,
    val alertTypeId: Short? = null,
    val severityId: Short? = null,
    val message: String? = null,
    val description: String? = null
)

/**
 * Request DTO for resolving an alert.
 */
@Serializable
data class AlertResolveRequest(
    val resolvedByUserId: Long? = null
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
    val id: Long,
    val code: String,
    val tenantId: Long,
    val sectorId: Long,
    val sectorCode: String?,
    val clientName: String? = null,
    val alertTypeId: Short?,
    val alertTypeName: String?,
    val severityId: Short?,
    val severityName: String?,
    val severityLevel: Short?,
    val message: String?,
    val description: String?,
    val isResolved: Boolean,
    val resolvedAt: String?,
    val resolvedByUserName: String?,
    val createdAt: String
) {
    /**
     * Returns the initials (first two letters of message or description) for avatar display.
     */
    val initials: String
        get() = (message ?: description)?.take(2)?.uppercase() ?: "AL"

    /**
     * Returns the display text (message or description).
     */
    val displayText: String
        get() = message ?: description ?: ""
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
    code = code,
    tenantId = tenantId,
    sectorId = sectorId,
    sectorCode = sectorCode,
    clientName = clientName,
    alertTypeId = alertTypeId,
    alertTypeName = alertTypeName,
    severityId = severityId,
    severityName = severityName,
    severityLevel = severityLevel,
    message = message,
    description = description,
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
 * Note: ID is auto-generated by the backend.
 */
@Serializable
data class AlertTypeCreateRequest(
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
 * Note: ID is auto-generated by the backend.
 */
@Serializable
data class AlertSeverityCreateRequest(
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
