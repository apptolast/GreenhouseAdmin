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

    override suspend fun getAlertsByTenantId(tenantId: Long): Result<List<Alert>> = runCatching {
        alertsApi.getAlerts(tenantId).map { it.toDomain() }
    }

    override suspend fun createAlert(tenantId: Long, request: AlertCreateRequest): Result<Alert> = runCatching {
        alertsApi.createAlert(tenantId, request).toDomain()
    }

    override suspend fun updateAlert(
        tenantId: Long,
        alertId: Long,
        request: AlertUpdateRequest
    ): Result<Alert> = runCatching {
        alertsApi.updateAlert(tenantId, alertId, request).toDomain()
    }

    override suspend fun deleteAlert(tenantId: Long, alertId: Long): Result<Unit> = runCatching {
        alertsApi.deleteAlert(tenantId, alertId)
    }

    // ==================== ACTION METHODS ====================

    override suspend fun resolveAlert(
        tenantId: Long,
        alertId: Long,
        resolvedByUserId: Long?
    ): Result<Alert> = runCatching {
        alertsApi.resolveAlert(tenantId, alertId, AlertResolveRequest(resolvedByUserId)).toDomain()
    }

    override suspend fun reopenAlert(tenantId: Long, alertId: Long): Result<Alert> = runCatching {
        alertsApi.reopenAlert(tenantId, alertId).toDomain()
    }
}
