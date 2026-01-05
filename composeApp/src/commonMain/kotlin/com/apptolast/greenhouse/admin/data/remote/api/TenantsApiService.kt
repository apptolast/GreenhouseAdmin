package com.apptolast.greenhouse.admin.data.remote.api

import com.apptolast.greenhouse.admin.data.model.CreateTenantRequest
import com.apptolast.greenhouse.admin.data.model.TenantResponse
import com.apptolast.greenhouse.admin.data.model.UpdateTenantRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

/**
 * API service for tenant operations.
 * Communicates with the /tenants endpoint of InvernaderosAPI.
 * Note: Paths are relative (no leading slash) to append to base URL.
 */
class TenantsApiService(private val httpClient: HttpClient) {

    /**
     * Get all tenants with optional filtering.
     */
    suspend fun getAllTenants(
        search: String? = null,
        province: String? = null,
        isActive: Boolean? = null
    ): List<TenantResponse> {
        return httpClient.get("tenants") {
            search?.let { parameter("search", it) }
            province?.let { parameter("province", it) }
            isActive?.let { parameter("isActive", it) }
        }.body()
    }

    /**
     * Get a tenant by ID.
     */
    suspend fun getTenantById(id: String): TenantResponse {
        return httpClient.get("tenants/$id").body()
    }

    /**
     * Create a new tenant.
     */
    suspend fun createTenant(request: CreateTenantRequest): TenantResponse {
        return httpClient.post("tenants") {
            setBody(request)
        }.body()
    }

    /**
     * Update an existing tenant.
     */
    suspend fun updateTenant(id: String, request: UpdateTenantRequest): TenantResponse {
        return httpClient.put("tenants/$id") {
            setBody(request)
        }.body()
    }

    /**
     * Delete a tenant by ID.
     */
    suspend fun deleteTenant(id: String) {
        httpClient.delete("tenants/$id")
    }
}
