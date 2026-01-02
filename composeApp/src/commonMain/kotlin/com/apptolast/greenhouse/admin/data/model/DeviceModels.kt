package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a device connected to the greenhouse system.
 */
@Serializable
data class Device(
    val id: String,
    val name: String,
    val type: DeviceType,
    val status: DeviceStatus,
    val clientId: String
) {
    /**
     * Returns the initials of the device name for display in avatars.
     */
    val initials: String
        get() = name.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
}

/**
 * Type of device.
 */
@Serializable
enum class DeviceType {
    SENSOR,
    ACTUATOR
}

/**
 * Status of device connection.
 */
@Serializable
enum class DeviceStatus {
    ONLINE,
    OFFLINE
}
