package com.apptolast.greenhouse.admin.data.local

import kotlinx.browser.localStorage

/**
 * WasmJS implementation using browser localStorage.
 */
actual class TokenStorage actual constructor() {

    actual fun getAccessToken(): String? = localStorage.getItem(KEY_ACCESS_TOKEN)

    actual fun saveAccessToken(token: String) {
        localStorage.setItem(KEY_ACCESS_TOKEN, token)
    }

    actual fun getTokenType(): String? = localStorage.getItem(KEY_TOKEN_TYPE)

    actual fun saveTokenType(type: String) {
        localStorage.setItem(KEY_TOKEN_TYPE, type)
    }

    actual fun getUsername(): String? = localStorage.getItem(KEY_USERNAME)

    actual fun saveUsername(username: String) {
        localStorage.setItem(KEY_USERNAME, username)
    }

    actual fun getRoles(): List<String> {
        val rolesString = localStorage.getItem(KEY_ROLES) ?: return emptyList()
        return rolesString.split(",").filter { it.isNotBlank() }
    }

    actual fun saveRoles(roles: List<String>) {
        localStorage.setItem(KEY_ROLES, roles.joinToString(","))
    }

    actual fun clearTokens() {
        localStorage.removeItem(KEY_ACCESS_TOKEN)
        localStorage.removeItem(KEY_TOKEN_TYPE)
        localStorage.removeItem(KEY_USERNAME)
        localStorage.removeItem(KEY_ROLES)
    }

    actual fun isAuthenticated(): Boolean = getAccessToken() != null

    companion object {
        private const val KEY_ACCESS_TOKEN = "greenhouse_access_token"
        private const val KEY_TOKEN_TYPE = "greenhouse_token_type"
        private const val KEY_USERNAME = "greenhouse_username"
        private const val KEY_ROLES = "greenhouse_roles"
    }
}
