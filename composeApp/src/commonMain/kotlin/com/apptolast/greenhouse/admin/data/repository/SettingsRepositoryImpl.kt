package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.ActuatorState
import com.apptolast.greenhouse.admin.data.model.DataType
import com.apptolast.greenhouse.admin.data.model.Period
import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.data.model.SettingCreateRequest
import com.apptolast.greenhouse.admin.data.model.SettingUpdateRequest
import com.apptolast.greenhouse.admin.data.model.toDomain
import com.apptolast.greenhouse.admin.data.remote.api.SettingsApiService
import com.apptolast.greenhouse.admin.domain.repository.SettingsRepository

/**
 * Implementation of SettingsRepository that communicates with the backend API.
 */
class SettingsRepositoryImpl(
    private val settingsApi: SettingsApiService
) : SettingsRepository {

    // ==================== CATALOG ====================

    override suspend fun getPeriods(): Result<List<Period>> = runCatching {
        settingsApi.getPeriods().map { it.toDomain() }
    }

    override suspend fun getActuatorStates(): Result<List<ActuatorState>> = runCatching {
        settingsApi.getActuatorStates().map { it.toDomain() }
    }

    override suspend fun getDataTypes(): Result<List<DataType>> = runCatching {
        settingsApi.getDataTypes().map { it.toDomain() }
    }

    // ==================== CRUD ====================

    override suspend fun getSettingsByTenantId(tenantId: Long): Result<List<Setting>> = runCatching {
        settingsApi.getSettings(tenantId).map { it.toDomain() }
    }

    override suspend fun createSetting(tenantId: Long, request: SettingCreateRequest): Result<Setting> = runCatching {
        settingsApi.createSetting(tenantId, request).toDomain()
    }

    override suspend fun updateSetting(
        tenantId: Long,
        settingId: Long,
        request: SettingUpdateRequest
    ): Result<Setting> = runCatching {
        settingsApi.updateSetting(tenantId, settingId, request).toDomain()
    }

    override suspend fun deleteSetting(tenantId: Long, settingId: Long): Result<Unit> = runCatching {
        settingsApi.deleteSetting(tenantId, settingId)
    }
}
