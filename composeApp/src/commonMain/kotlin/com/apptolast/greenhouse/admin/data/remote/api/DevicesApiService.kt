package com.apptolast.greenhouse.admin.data.remote.api

import com.apptolast.greenhouse.admin.data.model.DeviceCreateRequest
import com.apptolast.greenhouse.admin.data.model.DeviceResponse
import com.apptolast.greenhouse.admin.data.model.DeviceUpdateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

/**
 * API service for device operations within a tenant.
 * Communicates with the /tenants/{tenantId}/devices endpoints of InvernaderosAPI.
 * Note: Paths are relative (no leading slash) to append to base URL.
 */
class DevicesApiService(private val httpClient: HttpClient) {

    /**
     * Get all devices for a specific tenant.
     */
    suspend fun getDevicesByTenantId(tenantId: String): List<DeviceResponse> {
        return httpClient.get("tenants/$tenantId/devices").body()
    }

    /**
     * Get a specific device by ID within a tenant.
     */
    suspend fun getDeviceById(tenantId: String, deviceId: String): DeviceResponse {
        return httpClient.get("tenants/$tenantId/devices/$deviceId").body()
    }

    /**
     * Create a new device for a tenant.
     */
    suspend fun createDevice(tenantId: String, request: DeviceCreateRequest): DeviceResponse {
        return httpClient.post("tenants/$tenantId/devices") {
            setBody(request)
        }.body()
    }

    /**
     * Update an existing device within a tenant.
     */
    suspend fun updateDevice(tenantId: String, deviceId: String, request: DeviceUpdateRequest): DeviceResponse {
        return httpClient.put("tenants/$tenantId/devices/$deviceId") {
            setBody(request)
        }.body()
    }

    /**
     * Delete a device from a tenant.
     */
    suspend fun deleteDevice(tenantId: String, deviceId: String) {
        httpClient.delete("tenants/$tenantId/devices/$deviceId")
    }
}
