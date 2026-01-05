package com.apptolast.greenhouse.admin.data.remote.api

import com.apptolast.greenhouse.admin.data.model.SectorCreateRequest
import com.apptolast.greenhouse.admin.data.model.SectorResponse
import com.apptolast.greenhouse.admin.data.model.SectorUpdateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

/**
 * API service for sector operations within a tenant.
 * Communicates with the /tenants/{tenantId}/sectors endpoints of InvernaderosAPI.
 * Note: Paths are relative (no leading slash) to append to base URL.
 */
class SectorsApiService(private val httpClient: HttpClient) {

    /**
     * Get all sectors for a specific tenant.
     */
    suspend fun getSectorsByTenantId(tenantId: String): List<SectorResponse> {
        return httpClient.get("tenants/$tenantId/sectors").body()
    }

    /**
     * Get a specific sector by ID within a tenant.
     */
    suspend fun getSectorById(tenantId: String, sectorId: String): SectorResponse {
        return httpClient.get("tenants/$tenantId/sectors/$sectorId").body()
    }

    /**
     * Create a new sector for a tenant.
     */
    suspend fun createSector(tenantId: String, request: SectorCreateRequest): SectorResponse {
        return httpClient.post("tenants/$tenantId/sectors") {
            setBody(request)
        }.body()
    }

    /**
     * Update an existing sector within a tenant.
     */
    suspend fun updateSector(tenantId: String, sectorId: String, request: SectorUpdateRequest): SectorResponse {
        return httpClient.put("tenants/$tenantId/sectors/$sectorId") {
            setBody(request)
        }.body()
    }

    /**
     * Delete a sector from a tenant.
     */
    suspend fun deleteSector(tenantId: String, sectorId: String) {
        httpClient.delete("tenants/$tenantId/sectors/$sectorId")
    }
}
