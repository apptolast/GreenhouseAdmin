package com.apptolast.greenhouse.admin.data.remote.api

import com.apptolast.greenhouse.admin.data.model.AlertCreateRequest
import com.apptolast.greenhouse.admin.data.model.AlertResolveRequest
import com.apptolast.greenhouse.admin.data.model.AlertResponse
import com.apptolast.greenhouse.admin.data.model.AlertSeverityResponse
import com.apptolast.greenhouse.admin.data.model.AlertTypeResponse
import com.apptolast.greenhouse.admin.data.model.AlertUpdateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

/**
 * API service for alert operations.
 * Communicates with the /tenants/{tenantId}/alerts endpoints and /catalog endpoints of InvernaderosAPI.
 * Note: Paths are relative (no leading slash) to append to base URL.
 */
class AlertsApiService(private val httpClient: HttpClient) {

    // ==================== CATALOG ENDPOINTS (no tenantId) ====================

    /**
     * Get all alert types from the catalog.
     */
    suspend fun getAlertTypes(): List<AlertTypeResponse> {
        return httpClient.get("catalog/alert-types").body()
    }

    /**
     * Get all alert severities from the catalog.
     */
    suspend fun getAlertSeverities(): List<AlertSeverityResponse> {
        return httpClient.get("catalog/alert-severities").body()
    }

    // ==================== CRUD ENDPOINTS (with tenantId) ====================

    /**
     * Get all alerts for a specific tenant.
     */
    suspend fun getAlerts(tenantId: String): List<AlertResponse> {
        return httpClient.get("tenants/$tenantId/alerts").body()
    }

    /**
     * Get a specific alert by ID within a tenant.
     */
    suspend fun getAlert(tenantId: String, alertId: String): AlertResponse {
        return httpClient.get("tenants/$tenantId/alerts/$alertId").body()
    }

    /**
     * Create a new alert for a tenant.
     */
    suspend fun createAlert(tenantId: String, request: AlertCreateRequest): AlertResponse {
        return httpClient.post("tenants/$tenantId/alerts") {
            setBody(request)
        }.body()
    }

    /**
     * Update an existing alert within a tenant.
     */
    suspend fun updateAlert(tenantId: String, alertId: String, request: AlertUpdateRequest): AlertResponse {
        return httpClient.put("tenants/$tenantId/alerts/$alertId") {
            setBody(request)
        }.body()
    }

    /**
     * Delete an alert from a tenant.
     */
    suspend fun deleteAlert(tenantId: String, alertId: String) {
        httpClient.delete("tenants/$tenantId/alerts/$alertId")
    }

    // ==================== ACTION ENDPOINTS ====================

    /**
     * Resolve an alert.
     */
    suspend fun resolveAlert(tenantId: String, alertId: String, request: AlertResolveRequest): AlertResponse {
        return httpClient.post("tenants/$tenantId/alerts/$alertId/resolve") {
            setBody(request)
        }.body()
    }

    /**
     * Reopen a resolved alert.
     */
    suspend fun reopenAlert(tenantId: String, alertId: String): AlertResponse {
        return httpClient.post("tenants/$tenantId/alerts/$alertId/reopen").body()
    }
}
