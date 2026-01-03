package com.apptolast.greenhouse.admin.data.local

import platform.Foundation.NSUserDefaults

/**
 * iOS implementation using NSUserDefaults.
 * For production, consider using Keychain for sensitive data.
 */
actual class TokenStorage actual constructor() {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun getAccessToken(): String? = defaults.stringForKey(KEY_ACCESS_TOKEN)

    actual fun saveAccessToken(token: String) {
        defaults.setObject(token, KEY_ACCESS_TOKEN)
    }

    actual fun getTokenType(): String? = defaults.stringForKey(KEY_TOKEN_TYPE)

    actual fun saveTokenType(type: String) {
        defaults.setObject(type, KEY_TOKEN_TYPE)
    }

    actual fun getUsername(): String? = defaults.stringForKey(KEY_USERNAME)

    actual fun saveUsername(username: String) {
        defaults.setObject(username, KEY_USERNAME)
    }

    actual fun getRoles(): List<String> {
        val rolesArray = defaults.arrayForKey(KEY_ROLES) ?: return emptyList()
        return rolesArray.mapNotNull { it as? String }
    }

    actual fun saveRoles(roles: List<String>) {
        defaults.setObject(roles, KEY_ROLES)
    }

    actual fun clearTokens() {
        defaults.removeObjectForKey(KEY_ACCESS_TOKEN)
        defaults.removeObjectForKey(KEY_TOKEN_TYPE)
        defaults.removeObjectForKey(KEY_USERNAME)
        defaults.removeObjectForKey(KEY_ROLES)
    }

    actual fun isAuthenticated(): Boolean = getAccessToken() != null

    companion object {
        private const val KEY_ACCESS_TOKEN = "greenhouse_access_token"
        private const val KEY_TOKEN_TYPE = "greenhouse_token_type"
        private const val KEY_USERNAME = "greenhouse_username"
        private const val KEY_ROLES = "greenhouse_roles"
    }
}
