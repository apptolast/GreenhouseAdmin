package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.AlertCreateRequest
import com.apptolast.greenhouse.admin.data.model.AlertSeverityCatalog
import com.apptolast.greenhouse.admin.data.model.AlertType
import com.apptolast.greenhouse.admin.data.model.AlertUpdateRequest

/**
 * Repository interface for alert data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface AlertsRepository {

    // ==================== CATALOG METHODS ====================

    /**
     * Fetches all alert types from the catalog.
     * @return Result containing list of AlertType or error
     */
    suspend fun getAlertTypes(): Result<List<AlertType>>

    /**
     * Fetches all alert severities from the catalog.
     * @return Result containing list of AlertSeverityCatalog or error
     */
    suspend fun getAlertSeverities(): Result<List<AlertSeverityCatalog>>

    // ==================== CRUD METHODS ====================

    /**
     * Fetches all alerts for a specific tenant.
     * @param tenantId The tenant ID to filter alerts by
     * @return Result containing list of Alert or error
     */
    suspend fun getAlertsByTenantId(tenantId: String): Result<List<Alert>>

    /**
     * Creates a new alert for a tenant.
     * @param tenantId The tenant ID
     * @param request The alert data to create
     * @return Result containing the created Alert or error
     */
    suspend fun createAlert(tenantId: String, request: AlertCreateRequest): Result<Alert>

    /**
     * Updates an existing alert.
     * @param tenantId The tenant ID
     * @param alertId The alert ID to update
     * @param request The alert data to update
     * @return Result containing the updated Alert or error
     */
    suspend fun updateAlert(tenantId: String, alertId: String, request: AlertUpdateRequest): Result<Alert>

    /**
     * Deletes an alert by ID.
     * @param tenantId The tenant ID
     * @param alertId The alert ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteAlert(tenantId: String, alertId: String): Result<Unit>

    // ==================== ACTION METHODS ====================

    /**
     * Resolves an alert.
     * @param tenantId The tenant ID
     * @param alertId The alert ID to resolve
     * @param resolvedByUserId Optional user ID who resolved the alert
     * @return Result containing the resolved Alert or error
     */
    suspend fun resolveAlert(tenantId: String, alertId: String, resolvedByUserId: String? = null): Result<Alert>

    /**
     * Reopens a resolved alert.
     * @param tenantId The tenant ID
     * @param alertId The alert ID to reopen
     * @return Result containing the reopened Alert or error
     */
    suspend fun reopenAlert(tenantId: String, alertId: String): Result<Alert>
}
