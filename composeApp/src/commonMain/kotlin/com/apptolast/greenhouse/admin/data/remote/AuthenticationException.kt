package com.apptolast.greenhouse.admin.data.remote

/**
 * Exception thrown when authentication fails due to expired or invalid token.
 * This exception is thrown by the HTTP client when receiving 401 or 403 responses.
 */
class AuthenticationException(
    val statusCode: Int,
    message: String
) : Exception(message) {

    companion object {
        /**
         * Creates an exception for 401 Unauthorized responses.
         */
        fun unauthorized() = AuthenticationException(
            statusCode = 401,
            message = "Session expired. Please log in again."
        )

        /**
         * Creates an exception for 403 Forbidden responses.
         */
        fun forbidden() = AuthenticationException(
            statusCode = 403,
            message = "Session expired. Please log in again."
        )
    }
}
