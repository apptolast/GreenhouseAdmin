package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.AlertCreateRequest
import com.apptolast.greenhouse.admin.data.model.AlertResolveRequest
import com.apptolast.greenhouse.admin.data.model.AlertSeverityCatalog
import com.apptolast.greenhouse.admin.data.model.AlertType
import com.apptolast.greenhouse.admin.data.model.AlertUpdateRequest
import com.apptolast.greenhouse.admin.data.model.toDomain
import com.apptolast.greenhouse.admin.data.remote.api.AlertsApiService
import com.apptolast.greenhouse.admin.domain.repository.AlertsRepository

/**
 * Implementation of AlertsRepository using real API calls.
 */
class AlertsRepositoryImpl(
    private val alertsApi: AlertsApiService
) : AlertsRepository {

    // ==================== CATALOG METHODS ====================

    override suspend fun getAlertTypes(): Result<List<AlertType>> = runCatching {
        alertsApi.getAlertTypes().map { it.toDomain() }
    }

    override suspend fun getAlertSeverities(): Result<List<AlertSeverityCatalog>> = runCatching {
        alertsApi.getAlertSeverities().map { it.toDomain() }
    }

    // ==================== CRUD METHODS ====================

    override suspend fun getAlertsByTenantId(tenantId: String): Result<List<Alert>> = runCatching {
        alertsApi.getAlerts(tenantId).map { it.toDomain() }
    }

    override suspend fun createAlert(tenantId: String, request: AlertCreateRequest): Result<Alert> = runCatching {
        alertsApi.createAlert(tenantId, request).toDomain()
    }

    override suspend fun updateAlert(
        tenantId: String,
        alertId: String,
        request: AlertUpdateRequest
    ): Result<Alert> = runCatching {
        alertsApi.updateAlert(tenantId, alertId, request).toDomain()
    }

    override suspend fun deleteAlert(tenantId: String, alertId: String): Result<Unit> = runCatching {
        alertsApi.deleteAlert(tenantId, alertId)
    }

    // ==================== ACTION METHODS ====================

    override suspend fun resolveAlert(
        tenantId: String,
        alertId: String,
        resolvedByUserId: String?
    ): Result<Alert> = runCatching {
        alertsApi.resolveAlert(tenantId, alertId, AlertResolveRequest(resolvedByUserId)).toDomain()
    }

    override suspend fun reopenAlert(tenantId: String, alertId: String): Result<Alert> = runCatching {
        alertsApi.reopenAlert(tenantId, alertId).toDomain()
    }
}
