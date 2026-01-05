package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Represents geographic coordinates.
 * Matches the JSONB location field in the database: {lat: number, lon: number}
 */
@Serializable
data class Location(
    val lat: Double? = null,
    val lon: Double? = null
) {
    /**
     * Returns true if both coordinates are set.
     */
    val isValid: Boolean
        get() = lat != null && lon != null

    /**
     * Returns a formatted string of coordinates or empty string if not valid.
     */
    val displayString: String
        get() = if (isValid) "$lat, $lon" else ""
}

/**
 * Represents a client in the system.
 * Matches the TenantResponse structure from InvernaderosAPI.
 */
@Serializable
data class Client(
    val id: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val province: String = "",
    val country: String = "",
    val location: Location? = null,
    val isActive: Boolean? = true,
    val status: ClientStatus = ClientStatus.ACTIVE
) {
    val initials: String
        get() = name.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")

    val fullLocation: String
        get() = buildString {
            if (province.isNotBlank()) append(province)
            if (province.isNotBlank() && country.isNotBlank()) append(", ")
            if (country.isNotBlank()) append(country)
        }.ifBlank { "-" }
}

/**
 * Status of a client account.
 */
@Serializable
enum class ClientStatus {
    ACTIVE,
    PENDING,
    INACTIVE
}

/**
 * Convert ClientStatus to isActive Boolean for API.
 * Maps: ACTIVE -> true, INACTIVE -> false, PENDING -> null
 */
fun ClientStatus.toIsActive(): Boolean? = when (this) {
    ClientStatus.ACTIVE -> true
    ClientStatus.INACTIVE -> false
    ClientStatus.PENDING -> null
}

/**
 * Filter options for client status.
 */
enum class ClientStatusFilter {
    ALL,
    ACTIVE,
    PENDING,
    INACTIVE;

    fun matches(status: ClientStatus): Boolean = when (this) {
        ALL -> true
        ACTIVE -> status == ClientStatus.ACTIVE
        PENDING -> status == ClientStatus.PENDING
        INACTIVE -> status == ClientStatus.INACTIVE
    }
}

/**
 * Pagination information for client list.
 */
data class PaginationInfo(
    val currentPage: Int = 0,
    val pageSize: Int = 10,
    val totalItems: Int = 0
) {
    val totalPages: Int
        get() = if (totalItems == 0) 0 else (totalItems + pageSize - 1) / pageSize

    val startIndex: Int
        get() = currentPage * pageSize

    val endIndex: Int
        get() = minOf(startIndex + pageSize, totalItems)

    val hasNextPage: Boolean
        get() = currentPage < totalPages - 1

    val hasPreviousPage: Boolean
        get() = currentPage > 0

    val displayRange: String
        get() = if (totalItems == 0) "0-0" else "${startIndex + 1}-$endIndex"
}
