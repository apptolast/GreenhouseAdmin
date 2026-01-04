package com.apptolast.greenhouse.admin.data.remote.api

import com.apptolast.greenhouse.admin.data.model.GreenhouseCreateRequest
import com.apptolast.greenhouse.admin.data.model.GreenhouseResponse
import com.apptolast.greenhouse.admin.data.model.GreenhouseUpdateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

/**
 * API service for greenhouse operations within a tenant.
 * Communicates with the /tenants/{tenantId}/greenhouses endpoints of InvernaderosAPI.
 * Note: Paths are relative (no leading slash) to append to base URL.
 */
class GreenhousesApiService(private val httpClient: HttpClient) {

    /**
     * Get all greenhouses for a specific tenant.
     */
    suspend fun getGreenhousesByTenantId(tenantId: String): List<GreenhouseResponse> {
        return httpClient.get("tenants/$tenantId/greenhouses").body()
    }

    /**
     * Get a specific greenhouse by ID within a tenant.
     */
    suspend fun getGreenhouseById(tenantId: String, greenhouseId: String): GreenhouseResponse {
        return httpClient.get("tenants/$tenantId/greenhouses/$greenhouseId").body()
    }

    /**
     * Create a new greenhouse for a tenant.
     */
    suspend fun createGreenhouse(tenantId: String, request: GreenhouseCreateRequest): GreenhouseResponse {
        return httpClient.post("tenants/$tenantId/greenhouses") {
            setBody(request)
        }.body()
    }

    /**
     * Update an existing greenhouse within a tenant.
     */
    suspend fun updateGreenhouse(
        tenantId: String,
        greenhouseId: String,
        request: GreenhouseUpdateRequest
    ): GreenhouseResponse {
        return httpClient.put("tenants/$tenantId/greenhouses/$greenhouseId") {
            setBody(request)
        }.body()
    }

    /**
     * Delete a greenhouse from a tenant.
     */
    suspend fun deleteGreenhouse(tenantId: String, greenhouseId: String) {
        httpClient.delete("tenants/$tenantId/greenhouses/$greenhouseId")
    }
}
