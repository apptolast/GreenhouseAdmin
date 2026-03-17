package com.apptolast.greenhouse.admin.data.remote.api

import com.apptolast.greenhouse.admin.data.model.ActuatorStateResponse
import com.apptolast.greenhouse.admin.data.model.DataTypeResponse
import com.apptolast.greenhouse.admin.data.model.PeriodResponse
import com.apptolast.greenhouse.admin.data.model.SettingCreateRequest
import com.apptolast.greenhouse.admin.data.model.SettingResponse
import com.apptolast.greenhouse.admin.data.model.SettingUpdateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

/**
 * API service for setting operations.
 * Communicates with the /tenants/{tenantId}/settings endpoints and /catalog endpoints of InvernaderosAPI.
 * Note: Paths are relative (no leading slash) to append to base URL.
 */
class SettingsApiService(private val httpClient: HttpClient) {

    // ==================== CATALOG ENDPOINTS (no tenantId) ====================

    /**
     * Get all periods from the catalog.
     * Periods define when a setting applies: DAY, NIGHT, or ALL (24h).
     */
    suspend fun getPeriods(): List<PeriodResponse> {
        return httpClient.get("catalog/periods").body()
    }

    /**
     * Get all actuator states from the catalog.
     * Actuator states define the state configuration (ON, OFF, AUTO, etc.).
     */
    suspend fun getActuatorStates(): List<ActuatorStateResponse> {
        return httpClient.get("catalog/actuator-states").body()
    }

    /**
     * Get all data types from the catalog.
     * Data types define the kind of data associated with setpoints.
     */
    suspend fun getDataTypes(): List<DataTypeResponse> {
        return httpClient.get("catalog/data-types").body()
    }

    // ==================== CRUD ENDPOINTS (with tenantId) ====================

    /**
     * Get all settings for a specific tenant.
     */
    suspend fun getSettings(tenantId: Long): List<SettingResponse> {
        return httpClient.get("tenants/$tenantId/settings").body()
    }

    /**
     * Get a specific setting by ID within a tenant.
     */
    suspend fun getSetting(tenantId: Long, settingId: Long): SettingResponse {
        return httpClient.get("tenants/$tenantId/settings/$settingId").body()
    }

    /**
     * Create a new setting for a tenant.
     */
    suspend fun createSetting(tenantId: Long, request: SettingCreateRequest): SettingResponse {
        return httpClient.post("tenants/$tenantId/settings") {
            setBody(request)
        }.body()
    }

    /**
     * Update an existing setting within a tenant.
     */
    suspend fun updateSetting(tenantId: Long, settingId: Long, request: SettingUpdateRequest): SettingResponse {
        return httpClient.put("tenants/$tenantId/settings/$settingId") {
            setBody(request)
        }.body()
    }

    /**
     * Delete a setting from a tenant.
     */
    suspend fun deleteSetting(tenantId: Long, settingId: Long) {
        httpClient.delete("tenants/$tenantId/settings/$settingId")
    }

    // ==================== FILTER ENDPOINTS ====================

    /**
     * Get settings for a specific sector.
     */
    suspend fun getSettingsBySector(tenantId: Long, sectorId: Long): List<SettingResponse> {
        return httpClient.get("tenants/$tenantId/settings/sector/$sectorId").body()
    }

    /**
     * Get active settings for a specific sector.
     */
    suspend fun getActiveSettingsBySector(tenantId: Long, sectorId: Long): List<SettingResponse> {
        return httpClient.get("tenants/$tenantId/settings/sector/$sectorId/active").body()
    }

    /**
     * Get settings for a specific sector and parameter.
     */
    suspend fun getSettingsBySectorAndParameter(
        tenantId: Long,
        sectorId: Long,
        parameterId: Short
    ): List<SettingResponse> {
        return httpClient.get("tenants/$tenantId/settings/sector/$sectorId/parameter/$parameterId").body()
    }

    /**
     * Get settings for a specific sector and actuator state.
     */
    suspend fun getSettingsBySectorAndActuatorState(
        tenantId: Long,
        sectorId: Long,
        actuatorStateId: Short
    ): List<SettingResponse> {
        return httpClient.get("tenants/$tenantId/settings/sector/$sectorId/actuator-state/$actuatorStateId")
            .body()
    }

    /**
     * Get a specific setting by sector, parameter, and actuator state combination.
     */
    suspend fun getSettingBySectorParameterActuatorState(
        tenantId: Long,
        sectorId: Long,
        parameterId: Short,
        actuatorStateId: Short
    ): SettingResponse {
        return httpClient.get(
            "tenants/$tenantId/settings/sector/$sectorId/parameter/$parameterId/actuator-state/$actuatorStateId"
        ).body()
    }
}
