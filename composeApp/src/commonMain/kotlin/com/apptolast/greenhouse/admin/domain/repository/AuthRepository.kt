package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.UserSession

/**
 * Repository interface for authentication operations.
 * All methods return Result<T> for consistent error handling.
 */
interface AuthRepository {
    /**
     * Authenticate user with username and password.
     * Stores the token on successful login.
     * @param username The user's username or email
     * @param password The user's password
     * @return Result containing UserSession or error
     */
    suspend fun login(username: String, password: String): Result<UserSession>

    /**
     * Log out the current user.
     * Calls the server logout endpoint and clears stored tokens.
     * @return Result indicating success or failure
     */
    suspend fun logout(): Result<Unit>

    /**
     * Clear local tokens without calling the server.
     * Used for immediate local logout.
     */
    fun clearLocalSession()

    /**
     * Check if user is currently authenticated.
     * @return true if user has a valid token stored
     */
    fun isAuthenticated(): Boolean

    /**
     * Get the current user session if authenticated.
     * @return UserSession if authenticated, null otherwise
     */
    fun getCurrentSession(): UserSession?
}
