package com.apptolast.greenhouse.admin.data.remote.api

import com.apptolast.greenhouse.admin.data.model.DeviceCategoryResponse
import com.apptolast.greenhouse.admin.data.model.DeviceCreateRequest
import com.apptolast.greenhouse.admin.data.model.DeviceResponse
import com.apptolast.greenhouse.admin.data.model.DeviceTypeResponse
import com.apptolast.greenhouse.admin.data.model.DeviceUnitResponse
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
    suspend fun getDevicesByTenantId(tenantId: Long): List<DeviceResponse> {
        return httpClient.get("tenants/$tenantId/devices").body()
    }

    /**
     * Get a specific device by ID within a tenant.
     */
    suspend fun getDeviceById(tenantId: Long, deviceId: Long): DeviceResponse {
        return httpClient.get("tenants/$tenantId/devices/$deviceId").body()
    }

    /**
     * Create a new device for a tenant.
     */
    suspend fun createDevice(tenantId: Long, request: DeviceCreateRequest): DeviceResponse {
        return httpClient.post("tenants/$tenantId/devices") {
            setBody(request)
        }.body()
    }

    /**
     * Update an existing device within a tenant.
     */
    suspend fun updateDevice(tenantId: Long, deviceId: Long, request: DeviceUpdateRequest): DeviceResponse {
        return httpClient.put("tenants/$tenantId/devices/$deviceId") {
            setBody(request)
        }.body()
    }

    /**
     * Delete a device from a tenant.
     */
    suspend fun deleteDevice(tenantId: Long, deviceId: Long) {
        httpClient.delete("tenants/$tenantId/devices/$deviceId")
    }

    // ==================== CATALOG ENDPOINTS ====================

    /**
     * Get all device categories from the catalog.
     */
    suspend fun getDeviceCategories(): List<DeviceCategoryResponse> {
        return httpClient.get("catalog/device-categories").body()
    }

    /**
     * Get device types from the catalog, optionally filtered by category.
     */
    suspend fun getDeviceTypes(categoryId: Short? = null): List<DeviceTypeResponse> {
        val url = if (categoryId != null) {
            "catalog/device-types?categoryId=$categoryId"
        } else {
            "catalog/device-types"
        }
        return httpClient.get(url).body()
    }

    /**
     * Get all units from the catalog.
     */
    suspend fun getUnits(): List<DeviceUnitResponse> {
        return httpClient.get("catalog/units").body()
    }
}
