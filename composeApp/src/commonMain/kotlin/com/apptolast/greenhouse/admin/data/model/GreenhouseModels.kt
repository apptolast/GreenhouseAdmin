package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Response DTO from the API representing a Greenhouse.
 * Matches the GreenhouseResponse structure from InvernaderosAPI.
 */
@Serializable
data class GreenhouseResponse(
    val id: String,
    val name: String,
    val tenantId: String,
    val location: Location? = null,
    val areaM2: Double? = null,
    val timezone: String? = null,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Request DTO for creating a new Greenhouse.
 */
@Serializable
data class GreenhouseCreateRequest(
    val name: String,
    val location: Location? = null,
    val areaM2: Double? = null,
    val timezone: String? = "Europe/Madrid",
    val isActive: Boolean? = true
)

/**
 * Request DTO for updating an existing Greenhouse.
 * All fields are optional for partial updates.
 */
@Serializable
data class GreenhouseUpdateRequest(
    val name: String? = null,
    val location: Location? = null,
    val areaM2: Double? = null,
    val timezone: String? = null,
    val isActive: Boolean? = null
)

/**
 * Domain model representing a Greenhouse.
 * Used internally in the app for business logic.
 */
@Serializable
data class Greenhouse(
    val id: String,
    val name: String,
    val tenantId: String,
    val location: Location? = null,
    val areaM2: Double? = null,
    val timezone: String? = "Europe/Madrid",
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    /**
     * Returns the first letter of the greenhouse name as initial.
     */
    val initial: String
        get() = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    /**
     * Returns the formatted area string or "-" if not set.
     */
    val areaDisplay: String
        get() = areaM2?.let { "$it m²" } ?: "-"

    /**
     * Returns the location display string or "-" if not set.
     */
    val locationDisplay: String
        get() = location?.displayString?.takeIf { it.isNotBlank() } ?: "-"
}

/**
 * Status of a greenhouse - derived from isActive.
 */
@Serializable
enum class GreenhouseStatus {
    ACTIVE,
    INACTIVE;

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}

/**
 * Extension to get GreenhouseStatus from Greenhouse.
 */
val Greenhouse.status: GreenhouseStatus
    get() = if (isActive) GreenhouseStatus.ACTIVE else GreenhouseStatus.INACTIVE

/**
 * Extension to convert GreenhouseResponse to domain model.
 */
fun GreenhouseResponse.toGreenhouse() = Greenhouse(
    id = id,
    name = name,
    tenantId = tenantId,
    location = location,
    areaM2 = areaM2,
    timezone = timezone,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)
