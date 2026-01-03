package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Domain model for Setting entity.
 * Represents a client-specific configuration setting.
 */
@Serializable
data class Setting(
    val id: String,
    val key: String,
    val value: String,
    val description: String,
    val clientId: String
) {
    /**
     * Returns the first two letters of the key (uppercase) for avatar display.
     */
    val initials: String
        get() = key.take(2).uppercase()
}
