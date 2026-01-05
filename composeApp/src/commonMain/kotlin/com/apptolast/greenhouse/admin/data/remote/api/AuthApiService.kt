package com.apptolast.greenhouse.admin.data.remote.api

import com.apptolast.greenhouse.admin.BuildKonfig
import com.apptolast.greenhouse.admin.data.model.JwtResponse
import com.apptolast.greenhouse.admin.data.model.LoginRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * API service for authentication endpoints.
 * Uses a separate base URL for auth endpoints.
 */
class AuthApiService(private val httpClient: HttpClient) {

    /**
     * Authenticate user with username and password.
     * @return JWT response with token and user info
     * @throws Exception on network or authentication error
     */
    suspend fun login(username: String, password: String): JwtResponse {
        return httpClient.post("${BuildKonfig.AUTH_BASE_URL}/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(username = username, password = password))
        }.body()
    }

    /**
     * Logout user from the server.
     * Invalidates the current session on the server side.
     */
    suspend fun logout() {
        httpClient.post("${BuildKonfig.AUTH_BASE_URL}/logout")
    }
}
