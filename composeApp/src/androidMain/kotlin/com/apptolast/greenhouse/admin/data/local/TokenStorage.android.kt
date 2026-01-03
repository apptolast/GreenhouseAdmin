package com.apptolast.greenhouse.admin.data.local

/**
 * Android implementation using in-memory storage.
 * For production, use EncryptedSharedPreferences with proper context initialization.
 */
actual class TokenStorage actual constructor() {
    private var accessToken: String? = null
    private var tokenType: String? = null
    private var username: String? = null
    private var roles: List<String> = emptyList()

    actual fun getAccessToken(): String? = accessToken

    actual fun saveAccessToken(token: String) {
        accessToken = token
    }

    actual fun getTokenType(): String? = tokenType

    actual fun saveTokenType(type: String) {
        tokenType = type
    }

    actual fun getUsername(): String? = username

    actual fun saveUsername(username: String) {
        this.username = username
    }

    actual fun getRoles(): List<String> = roles

    actual fun saveRoles(roles: List<String>) {
        this.roles = roles
    }

    actual fun clearTokens() {
        accessToken = null
        tokenType = null
        username = null
        roles = emptyList()
    }

    actual fun isAuthenticated(): Boolean = accessToken != null
}
