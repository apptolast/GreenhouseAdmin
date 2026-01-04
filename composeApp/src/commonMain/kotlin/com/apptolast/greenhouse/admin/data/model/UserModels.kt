package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * API response DTO for user data from the backend.
 */
@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val tenantId: String,
    val isActive: Boolean,
    val lastLogin: String? = null,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Request DTO for creating a new user.
 */
@Serializable
data class UserCreateRequest(
    val username: String,
    val email: String,
    val passwordRaw: String,
    val role: String,
    val isActive: Boolean = true
)

/**
 * Request DTO for updating an existing user.
 * All fields are optional for partial updates.
 */
@Serializable
data class UserUpdateRequest(
    val username: String? = null,
    val email: String? = null,
    val passwordRaw: String? = null,
    val role: String? = null,
    val isActive: Boolean? = null
)

/**
 * Domain model representing a user in the application.
 */
@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: UserRole,
    val tenantId: String,
    val isActive: Boolean = true,
    val lastLogin: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    /**
     * Returns the first letter of the username in uppercase for avatar display.
     */
    val initial: String
        get() = username.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
}

/**
 * User roles supported by the system.
 */
enum class UserRole {
    ADMIN,
    OPERATOR,
    VIEWER;

    /**
     * Display name for the role (e.g., "Admin", "Operator", "Viewer").
     */
    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }

    companion object {
        /**
         * Safely parse a role string to UserRole, defaulting to VIEWER if unknown.
         */
        fun fromString(value: String): UserRole {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: VIEWER
        }
    }
}

/**
 * Extension function to convert API response to domain model.
 */
fun UserResponse.toUser() = User(
    id = id,
    username = username,
    email = email,
    role = UserRole.fromString(role),
    tenantId = tenantId,
    isActive = isActive,
    lastLogin = lastLogin,
    createdAt = createdAt,
    updatedAt = updatedAt
)
