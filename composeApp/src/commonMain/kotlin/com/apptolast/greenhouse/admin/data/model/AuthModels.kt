package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Request body for login endpoint.
 */
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * Response from login endpoint containing JWT token and user info.
 */
@Serializable
data class JwtResponse(
    val token: String,
    val type: String = "Bearer",
    val username: String,
    val roles: List<String> = emptyList()
)

/**
 * Represents the authenticated user session.
 */
data class UserSession(
    val token: String,
    val tokenType: String,
    val username: String,
    val roles: List<String>
) {
    val isAdmin: Boolean
        get() = roles.any { it.equals("ROLE_ADMIN", ignoreCase = true) }
}

/**
 * Extension to convert JwtResponse to UserSession.
 */
fun JwtResponse.toUserSession(): UserSession = UserSession(
    token = token,
    tokenType = type,
    username = username,
    roles = roles
)
