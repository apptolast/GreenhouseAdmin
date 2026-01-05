package com.apptolast.greenhouse.admin.data.local

/**
 * Platform-specific token storage for JWT tokens.
 * Implementations should store tokens securely (e.g., SharedPreferences on Android,
 * Keychain on iOS, localStorage on Web).
 */
expect class TokenStorage() {
    /**
     * Get the current access token, or null if not authenticated.
     */
    fun getAccessToken(): String?

    /**
     * Save the access token.
     */
    fun saveAccessToken(token: String)

    /**
     * Get the token type (e.g., "Bearer").
     */
    fun getTokenType(): String?

    /**
     * Save the token type.
     */
    fun saveTokenType(type: String)

    /**
     * Get the authenticated username.
     */
    fun getUsername(): String?

    /**
     * Save the username.
     */
    fun saveUsername(username: String)

    /**
     * Get user roles.
     */
    fun getRoles(): List<String>

    /**
     * Save user roles.
     */
    fun saveRoles(roles: List<String>)

    /**
     * Clear all stored tokens (logout).
     */
    fun clearTokens()

    /**
     * Check if user is authenticated.
     */
    fun isAuthenticated(): Boolean
}
