package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Response DTO from the API representing a Sector.
 * Matches the SectorResponse structure from InvernaderosAPI.
 */
@Serializable
data class SectorResponse(
    val id: Long,
    val greenhouseId: Long,
    val variety: String? = null
)

/**
 * Request DTO for creating a new Sector.
 */
@Serializable
data class SectorCreateRequest(
    val greenhouseId: Long,
    val variety: String? = null
)

/**
 * Request DTO for updating an existing Sector.
 */
@Serializable
data class SectorUpdateRequest(
    val variety: String? = null
)

/**
 * Domain model representing a Sector within a greenhouse.
 * Used internally in the app for business logic.
 */
@Serializable
data class Sector(
    val id: Long,
    val greenhouseId: Long,
    val variety: String? = null
) {
    /**
     * Returns the first letter of the variety as initial for avatars.
     */
    val initial: String
        get() = variety?.firstOrNull()?.uppercaseChar()?.toString() ?: "S"

    /**
     * Returns the display name (variety or a default).
     */
    val displayName: String
        get() = variety ?: "Sector"
}

/**
 * Extension to convert SectorResponse to domain model.
 */
fun SectorResponse.toSector() = Sector(
    id = id,
    greenhouseId = greenhouseId,
    variety = variety
)
