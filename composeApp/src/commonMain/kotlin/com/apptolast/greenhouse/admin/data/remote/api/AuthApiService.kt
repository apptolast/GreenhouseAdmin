package com.apptolast.greenhouse.admin.data.remote.api

import com.apptolast.greenhouse.admin.data.model.JwtResponse
import com.apptolast.greenhouse.admin.data.model.LoginRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * API service for authentication endpoints.
 * Uses the unified /api/v1/auth endpoints.
 */
class AuthApiService(private val httpClient: HttpClient) {

    /**
     * Authenticate user with username and password.
     * @return JWT response with token and user info
     * @throws Exception on network or authentication error
     */
    suspend fun login(username: String, password: String): JwtResponse {
        return httpClient.post("auth/login") {
            setBody(LoginRequest(username = username, password = password))
        }.body()
    }

    /**
     * Logout user from the server.
     * Invalidates the current session on the server side.
     */
    suspend fun logout() {
        httpClient.post("auth/logout")
    }

    /**
     * Validates the current token by making a lightweight authenticated request.
     * Uses GET /tenants?page=0&size=1 as a minimal authenticated endpoint.
     * Throws AuthenticationException if token is expired/invalid.
     */
    suspend fun validateToken() {
        // Use a minimal authenticated endpoint to validate token
        // The HttpResponseValidator will throw AuthenticationException if 401/403
        httpClient.get("tenants") {
            url.parameters.append("page", "0")
            url.parameters.append("size", "1")
        }
    }
}
