package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.AlertSeverityCatalog
import com.apptolast.greenhouse.admin.data.model.AlertSeverityCreateRequest
import com.apptolast.greenhouse.admin.data.model.AlertSeverityUpdateRequest
import com.apptolast.greenhouse.admin.data.model.AlertType
import com.apptolast.greenhouse.admin.data.model.AlertTypeCreateRequest
import com.apptolast.greenhouse.admin.data.model.AlertTypeUpdateRequest
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogCategory
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogType
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogUnit
import com.apptolast.greenhouse.admin.data.model.DeviceCategoryCreateRequest
import com.apptolast.greenhouse.admin.data.model.DeviceCategoryUpdateRequest
import com.apptolast.greenhouse.admin.data.model.DeviceTypeCreateRequest
import com.apptolast.greenhouse.admin.data.model.DeviceTypeUpdateRequest
import com.apptolast.greenhouse.admin.data.model.Period
import com.apptolast.greenhouse.admin.data.model.PeriodCreateRequest
import com.apptolast.greenhouse.admin.data.model.PeriodUpdateRequest
import com.apptolast.greenhouse.admin.data.model.toDomain
import com.apptolast.greenhouse.admin.data.remote.api.CatalogApiService
import com.apptolast.greenhouse.admin.domain.repository.CatalogRepository

/**
 * Implementation of CatalogRepository that communicates with the backend API.
 * Handles all catalog CRUD operations for global configuration data.
 */
class CatalogRepositoryImpl(
    private val catalogApi: CatalogApiService
) : CatalogRepository {

    // ==================== DEVICE CATEGORIES ====================

    override suspend fun getDeviceCategories(): Result<List<DeviceCatalogCategory>> = runCatching {
        catalogApi.getDeviceCategories().map { it.toDomain() }
    }

    override suspend fun createDeviceCategory(id: Short, name: String): Result<DeviceCatalogCategory> = runCatching {
        val request = DeviceCategoryCreateRequest(id = id, name = name)
        catalogApi.createDeviceCategory(request).toDomain()
    }

    override suspend fun updateDeviceCategory(id: Short, name: String): Result<DeviceCatalogCategory> = runCatching {
        val request = DeviceCategoryUpdateRequest(name = name)
        catalogApi.updateDeviceCategory(id, request).toDomain()
    }

    override suspend fun deleteDeviceCategory(id: Short): Result<Unit> = runCatching {
        catalogApi.deleteDeviceCategory(id)
    }

    // ==================== DEVICE TYPES ====================

    override suspend fun getDeviceTypes(): Result<List<DeviceCatalogType>> = runCatching {
        catalogApi.getDeviceTypes().map { it.toDomain() }
    }

    override suspend fun createDeviceType(
        name: String,
        description: String?,
        categoryId: Short,
        defaultUnitId: Short?,
        dataType: String?,
        minExpectedValue: Double?,
        maxExpectedValue: Double?,
        controlType: String?,
        isActive: Boolean
    ): Result<DeviceCatalogType> = runCatching {
        val request = DeviceTypeCreateRequest(
            name = name,
            description = description,
            categoryId = categoryId,
            defaultUnitId = defaultUnitId,
            dataType = dataType,
            minExpectedValue = minExpectedValue,
            maxExpectedValue = maxExpectedValue,
            controlType = controlType,
            isActive = isActive
        )
        catalogApi.createDeviceType(request).toDomain()
    }

    override suspend fun updateDeviceType(
        id: Short,
        name: String?,
        description: String?,
        categoryId: Short?,
        defaultUnitId: Short?,
        dataType: String?,
        minExpectedValue: Double?,
        maxExpectedValue: Double?,
        controlType: String?,
        isActive: Boolean?
    ): Result<DeviceCatalogType> = runCatching {
        val request = DeviceTypeUpdateRequest(
            name = name,
            description = description,
            categoryId = categoryId,
            defaultUnitId = defaultUnitId,
            dataType = dataType,
            minExpectedValue = minExpectedValue,
            maxExpectedValue = maxExpectedValue,
            controlType = controlType,
            isActive = isActive
        )
        catalogApi.updateDeviceType(id, request).toDomain()
    }

    override suspend fun deleteDeviceType(id: Short): Result<Unit> = runCatching {
        catalogApi.deleteDeviceType(id)
    }

    override suspend fun activateDeviceType(id: Short): Result<DeviceCatalogType> = runCatching {
        catalogApi.activateDeviceType(id).toDomain()
    }

    override suspend fun deactivateDeviceType(id: Short): Result<DeviceCatalogType> = runCatching {
        catalogApi.deactivateDeviceType(id).toDomain()
    }

    // ==================== DEVICE UNITS (READ ONLY) ====================

    override suspend fun getDeviceUnits(): Result<List<DeviceCatalogUnit>> = runCatching {
        catalogApi.getDeviceUnits().map { it.toDomain() }
    }

    // ==================== ALERT TYPES ====================

    override suspend fun getAlertTypes(): Result<List<AlertType>> = runCatching {
        catalogApi.getAlertTypes().map { it.toDomain() }
    }

    override suspend fun createAlertType(
        id: Short,
        name: String,
        description: String?
    ): Result<AlertType> = runCatching {
        val request = AlertTypeCreateRequest(id = id, name = name, description = description)
        catalogApi.createAlertType(request).toDomain()
    }

    override suspend fun updateAlertType(
        id: Short,
        name: String?,
        description: String?
    ): Result<AlertType> = runCatching {
        val request = AlertTypeUpdateRequest(name = name, description = description)
        catalogApi.updateAlertType(id, request).toDomain()
    }

    override suspend fun deleteAlertType(id: Short): Result<Unit> = runCatching {
        catalogApi.deleteAlertType(id)
    }

    // ==================== ALERT SEVERITIES ====================

    override suspend fun getAlertSeverities(): Result<List<AlertSeverityCatalog>> = runCatching {
        catalogApi.getAlertSeverities().map { it.toDomain() }
    }

    override suspend fun createAlertSeverity(
        id: Short,
        name: String,
        level: Short,
        description: String?,
        color: String?,
        requiresAction: Boolean,
        notificationDelayMinutes: Int
    ): Result<AlertSeverityCatalog> = runCatching {
        val request = AlertSeverityCreateRequest(
            id = id,
            name = name,
            level = level,
            description = description,
            color = color,
            requiresAction = requiresAction,
            notificationDelayMinutes = notificationDelayMinutes
        )
        catalogApi.createAlertSeverity(request).toDomain()
    }

    override suspend fun updateAlertSeverity(
        id: Short,
        name: String?,
        level: Short?,
        description: String?,
        color: String?,
        requiresAction: Boolean?,
        notificationDelayMinutes: Int?
    ): Result<AlertSeverityCatalog> = runCatching {
        val request = AlertSeverityUpdateRequest(
            name = name,
            level = level,
            description = description,
            color = color,
            requiresAction = requiresAction,
            notificationDelayMinutes = notificationDelayMinutes
        )
        catalogApi.updateAlertSeverity(id, request).toDomain()
    }

    override suspend fun deleteAlertSeverity(id: Short): Result<Unit> = runCatching {
        catalogApi.deleteAlertSeverity(id)
    }

    // ==================== PERIODS ====================

    override suspend fun getPeriods(): Result<List<Period>> = runCatching {
        catalogApi.getPeriods().map { it.toDomain() }
    }

    override suspend fun createPeriod(id: Short, name: String): Result<Period> = runCatching {
        val request = PeriodCreateRequest(id = id, name = name)
        catalogApi.createPeriod(request).toDomain()
    }

    override suspend fun updatePeriod(id: Short, name: String): Result<Period> = runCatching {
        val request = PeriodUpdateRequest(name = name)
        catalogApi.updatePeriod(id, request).toDomain()
    }

    override suspend fun deletePeriod(id: Short): Result<Unit> = runCatching {
        catalogApi.deletePeriod(id)
    }
}
