package com.apptolast.greenhouse.admin.data.local

import java.util.prefs.Preferences

/**
 * JVM (Desktop) implementation using Java Preferences API.
 */
actual class TokenStorage actual constructor() {
    private val prefs = Preferences.userNodeForPackage(TokenStorage::class.java)

    actual fun getAccessToken(): String? = prefs.get(KEY_ACCESS_TOKEN, null)

    actual fun saveAccessToken(token: String) {
        prefs.put(KEY_ACCESS_TOKEN, token)
    }

    actual fun getTokenType(): String? = prefs.get(KEY_TOKEN_TYPE, null)

    actual fun saveTokenType(type: String) {
        prefs.put(KEY_TOKEN_TYPE, type)
    }

    actual fun getUsername(): String? = prefs.get(KEY_USERNAME, null)

    actual fun saveUsername(username: String) {
        prefs.put(KEY_USERNAME, username)
    }

    actual fun getRoles(): List<String> {
        val rolesString = prefs.get(KEY_ROLES, null) ?: return emptyList()
        return rolesString.split(",").filter { it.isNotBlank() }
    }

    actual fun saveRoles(roles: List<String>) {
        prefs.put(KEY_ROLES, roles.joinToString(","))
    }

    actual fun clearTokens() {
        prefs.remove(KEY_ACCESS_TOKEN)
        prefs.remove(KEY_TOKEN_TYPE)
        prefs.remove(KEY_USERNAME)
        prefs.remove(KEY_ROLES)
    }

    actual fun isAuthenticated(): Boolean = getAccessToken() != null

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_TOKEN_TYPE = "token_type"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLES = "roles"
    }
}
