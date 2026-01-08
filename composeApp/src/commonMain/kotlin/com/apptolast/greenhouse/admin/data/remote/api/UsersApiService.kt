package com.apptolast.greenhouse.admin.data.remote.api

import com.apptolast.greenhouse.admin.data.model.UserCreateRequest
import com.apptolast.greenhouse.admin.data.model.UserResponse
import com.apptolast.greenhouse.admin.data.model.UserUpdateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

/**
 * API service for user operations within a tenant.
 * Communicates with the /tenants/{tenantId}/users endpoints of InvernaderosAPI.
 * Note: Paths are relative (no leading slash) to append to base URL.
 */
class UsersApiService(private val httpClient: HttpClient) {

    /**
     * Get all users for a specific tenant.
     */
    suspend fun getUsersByTenantId(tenantId: Long): List<UserResponse> {
        return httpClient.get("tenants/$tenantId/users").body()
    }

    /**
     * Get a specific user by ID within a tenant.
     */
    suspend fun getUserById(tenantId: Long, userId: Long): UserResponse {
        return httpClient.get("tenants/$tenantId/users/$userId").body()
    }

    /**
     * Create a new user for a tenant.
     */
    suspend fun createUser(tenantId: Long, request: UserCreateRequest): UserResponse {
        return httpClient.post("tenants/$tenantId/users") {
            setBody(request)
        }.body()
    }

    /**
     * Update an existing user within a tenant.
     */
    suspend fun updateUser(tenantId: Long, userId: Long, request: UserUpdateRequest): UserResponse {
        return httpClient.put("tenants/$tenantId/users/$userId") {
            setBody(request)
        }.body()
    }

    /**
     * Delete a user from a tenant.
     */
    suspend fun deleteUser(tenantId: Long, userId: Long) {
        httpClient.delete("tenants/$tenantId/users/$userId")
    }
}
