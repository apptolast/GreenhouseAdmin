package com.apptolast.greenhouse.admin.data.remote.api

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

    // ==================== CRUD ENDPOINTS (with tenantId) ====================

    /**
     * Get all settings for a specific tenant.
     */
    suspend fun getSettings(tenantId: String): List<SettingResponse> {
        return httpClient.get("tenants/$tenantId/settings").body()
    }

    /**
     * Get a specific setting by ID within a tenant.
     */
    suspend fun getSetting(tenantId: String, settingId: String): SettingResponse {
        return httpClient.get("tenants/$tenantId/settings/$settingId").body()
    }

    /**
     * Create a new setting for a tenant.
     */
    suspend fun createSetting(tenantId: String, request: SettingCreateRequest): SettingResponse {
        return httpClient.post("tenants/$tenantId/settings") {
            setBody(request)
        }.body()
    }

    /**
     * Update an existing setting within a tenant.
     */
    suspend fun updateSetting(tenantId: String, settingId: String, request: SettingUpdateRequest): SettingResponse {
        return httpClient.put("tenants/$tenantId/settings/$settingId") {
            setBody(request)
        }.body()
    }

    /**
     * Delete a setting from a tenant.
     */
    suspend fun deleteSetting(tenantId: String, settingId: String) {
        httpClient.delete("tenants/$tenantId/settings/$settingId")
    }

    // ==================== FILTER ENDPOINTS ====================

    /**
     * Get settings for a specific greenhouse.
     */
    suspend fun getSettingsByGreenhouse(tenantId: String, greenhouseId: String): List<SettingResponse> {
        return httpClient.get("tenants/$tenantId/settings/greenhouse/$greenhouseId").body()
    }

    /**
     * Get active settings for a specific greenhouse.
     */
    suspend fun getActiveSettingsByGreenhouse(tenantId: String, greenhouseId: String): List<SettingResponse> {
        return httpClient.get("tenants/$tenantId/settings/greenhouse/$greenhouseId/active").body()
    }

    /**
     * Get settings for a specific greenhouse and parameter.
     */
    suspend fun getSettingsByGreenhouseAndParameter(
        tenantId: String,
        greenhouseId: String,
        parameterId: Short
    ): List<SettingResponse> {
        return httpClient.get("tenants/$tenantId/settings/greenhouse/$greenhouseId/parameter/$parameterId").body()
    }

    /**
     * Get settings for a specific greenhouse and period.
     */
    suspend fun getSettingsByGreenhouseAndPeriod(
        tenantId: String,
        greenhouseId: String,
        periodId: Short
    ): List<SettingResponse> {
        return httpClient.get("tenants/$tenantId/settings/greenhouse/$greenhouseId/period/$periodId").body()
    }

    /**
     * Get a specific setting by greenhouse, parameter, and period combination.
     */
    suspend fun getSettingByGreenhouseParameterPeriod(
        tenantId: String,
        greenhouseId: String,
        parameterId: Short,
        periodId: Short
    ): SettingResponse {
        return httpClient.get(
            "tenants/$tenantId/settings/greenhouse/$greenhouseId/parameter/$parameterId/period/$periodId"
        ).body()
    }
}
