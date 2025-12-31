package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a client in the system.
 * MVP structure with simplified fields.
 */
@Serializable
data class Client(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val province: String,
    val country: String,
    val location: String,
    val createdAt: Long,
    val updatedAt: Long,
    val status: ClientStatus
) {
    val initials: String
        get() = name.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")

    val fullLocation: String
        get() = "$province, $country"
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
