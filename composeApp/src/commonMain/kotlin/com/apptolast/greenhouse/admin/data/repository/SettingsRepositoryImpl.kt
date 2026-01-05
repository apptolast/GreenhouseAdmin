package com.apptolast.greenhouse.admin.data.repository

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

    // ==================== CRUD ====================

    override suspend fun getSettingsByTenantId(tenantId: String): Result<List<Setting>> = runCatching {
        settingsApi.getSettings(tenantId).map { it.toDomain() }
    }

    override suspend fun createSetting(tenantId: String, request: SettingCreateRequest): Result<Setting> = runCatching {
        settingsApi.createSetting(tenantId, request).toDomain()
    }

    override suspend fun updateSetting(
        tenantId: String,
        settingId: String,
        request: SettingUpdateRequest
    ): Result<Setting> = runCatching {
        settingsApi.updateSetting(tenantId, settingId, request).toDomain()
    }

    override suspend fun deleteSetting(tenantId: String, settingId: String): Result<Unit> = runCatching {
        settingsApi.deleteSetting(tenantId, settingId)
    }
}
