package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a greenhouse associated with a client.
 * MVP structure with simplified fields.
 */
@Serializable
data class Greenhouse(
    val id: String,
    val name: String,
    val description: String,
    val status: GreenhouseStatus,
    val clientId: String
) {
    /**
     * Returns the initials from the greenhouse name (max 2 letters).
     */
    val initials: String
        get() = name.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
}

/**
 * Status of a greenhouse.
 */
@Serializable
enum class GreenhouseStatus {
    ACTIVE,
    INACTIVE
}
