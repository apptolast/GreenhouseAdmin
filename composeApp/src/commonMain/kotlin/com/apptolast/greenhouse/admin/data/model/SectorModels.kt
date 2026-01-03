package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a sector within a greenhouse.
 * A sector is a subdivision of a greenhouse with a defined area.
 */
@Serializable
data class Sector(
    val id: String,
    val name: String,
    val greenhouseId: String,
    val greenhouseName: String = "", // For display purposes
    val area: Double, // In square meters (m²)
    val clientId: String
) {
    /**
     * Returns the initials of the sector name for display in avatars.
     */
    val initials: String
        get() = name.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")

    /**
     * Returns the area formatted with unit.
     */
    val formattedArea: String
        get() = "$area m²"
}
