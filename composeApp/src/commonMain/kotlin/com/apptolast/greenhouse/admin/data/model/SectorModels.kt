package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Response DTO from the API representing a Sector.
 * Matches the SectorResponse structure from InvernaderosAPI.
 */
@Serializable
data class SectorResponse(
    val id: Long,
    val code: String,
    val tenantId: Long,
    val greenhouseId: Long,
    val greenhouseCode: String? = null,
    val name: String? = null
)

/**
 * Request DTO for creating a new Sector.
 */
@Serializable
data class SectorCreateRequest(
    val greenhouseId: Long,
    val name: String? = null
)

/**
 * Request DTO for updating an existing Sector.
 */
@Serializable
data class SectorUpdateRequest(
    val greenhouseId: Long? = null,
    val name: String? = null
)

/**
 * Domain model representing a Sector within a greenhouse.
 * Used internally in the app for business logic.
 */
@Serializable
data class Sector(
    val id: Long,
    val code: String,
    val tenantId: Long,
    val greenhouseId: Long,
    val greenhouseCode: String? = null,
    val name: String? = null
) {
    /**
     * Returns the first letter of the name as initial for avatars.
     */
    val initial: String
        get() = name?.firstOrNull()?.uppercaseChar()?.toString() ?: "S"

    /**
     * Returns the display name (name or a default).
     */
    val displayName: String
        get() = name ?: "Sector"
}

/**
 * Extension to convert SectorResponse to domain model.
 */
fun SectorResponse.toSector() = Sector(
    id = id,
    code = code,
    tenantId = tenantId,
    greenhouseId = greenhouseId,
    greenhouseCode = greenhouseCode,
    name = name
)
