package com.apptolast.greenhouse.admin.data.remote

/**
 * Exception thrown by the HTTP client for 401/403 responses.
 *
 * 401 indicates the session is expired/invalid (caller should sign out);
 * 403 indicates the authenticated user lacks permission for this specific resource
 * (caller should surface or skip, but NOT sign out).
 */
class AuthenticationException(
    val statusCode: Int,
    message: String
) : Exception(message) {

    companion object {
        fun unauthorized() = AuthenticationException(
            statusCode = 401,
            message = "Session expired. Please log in again."
        )

        fun forbidden() = AuthenticationException(
            statusCode = 403,
            message = "Permission denied for this resource."
        )
    }
}
