package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a user associated with a client.
 * MVP structure with simplified fields.
 */
@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val clientId: String
) {
    /**
     * Returns the initials from the user's name (max 2 letters).
     */
    val initials: String
        get() = name.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
}
