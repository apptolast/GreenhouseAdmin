package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.local.TokenStorage
import com.apptolast.greenhouse.admin.data.model.UserSession
import com.apptolast.greenhouse.admin.data.model.toUserSession
import com.apptolast.greenhouse.admin.data.remote.api.AuthApiService
import com.apptolast.greenhouse.admin.domain.repository.AuthRepository

/**
 * Implementation of AuthRepository using API service and local token storage.
 */
class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<UserSession> {
        return runCatching {
            val response = authApiService.login(username, password)

            // Store tokens
            tokenStorage.saveAccessToken(response.token)
            tokenStorage.saveTokenType(response.type)
            tokenStorage.saveUsername(response.username)
            tokenStorage.saveRoles(response.roles)

            response.toUserSession()
        }
    }

    override fun logout() {
        tokenStorage.clearTokens()
    }

    override fun isAuthenticated(): Boolean {
        return tokenStorage.isAuthenticated()
    }

    override fun getCurrentSession(): UserSession? {
        val token = tokenStorage.getAccessToken() ?: return null
        return UserSession(
            token = token,
            tokenType = tokenStorage.getTokenType() ?: "Bearer",
            username = tokenStorage.getUsername() ?: "",
            roles = tokenStorage.getRoles()
        )
    }
}
