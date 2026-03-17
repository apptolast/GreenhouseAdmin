package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.ActuatorState
import com.apptolast.greenhouse.admin.data.model.DataType
import com.apptolast.greenhouse.admin.data.model.Period
import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.data.model.SettingCreateRequest
import com.apptolast.greenhouse.admin.data.model.SettingUpdateRequest

/**
 * Repository interface for managing settings.
 * Defines the contract for settings data operations.
 */
interface SettingsRepository {

    // ==================== CATALOG ====================

    /**
     * Retrieves all periods from the catalog.
     * Periods define when a setting applies: DAY, NIGHT, or ALL (24h).
     * @return Result containing list of periods or error
     */
    suspend fun getPeriods(): Result<List<Period>>

    /**
     * Retrieves all actuator states from the catalog.
     * Actuator states define the state configuration (ON, OFF, AUTO, etc.).
     * @return Result containing list of actuator states or error
     */
    suspend fun getActuatorStates(): Result<List<ActuatorState>>

    /**
     * Retrieves all data types from the catalog.
     * Data types define the kind of data associated with setpoints.
     * @return Result containing list of data types or error
     */
    suspend fun getDataTypes(): Result<List<DataType>>

    // ==================== CRUD ====================

    /**
     * Retrieves all settings for a specific tenant.
     * @param tenantId The ID of the tenant
     * @return Result containing list of settings or error
     */
    suspend fun getSettingsByTenantId(tenantId: Long): Result<List<Setting>>

    /**
     * Creates a new setting for a tenant.
     * @param tenantId The ID of the tenant
     * @param request The setting creation request
     * @return Result containing the created setting or error
     */
    suspend fun createSetting(tenantId: Long, request: SettingCreateRequest): Result<Setting>

    /**
     * Updates an existing setting.
     * @param tenantId The ID of the tenant
     * @param settingId The ID of the setting to update
     * @param request The setting update request
     * @return Result containing the updated setting or error
     */
    suspend fun updateSetting(tenantId: Long, settingId: Long, request: SettingUpdateRequest): Result<Setting>

    /**
     * Deletes a setting by ID.
     * @param tenantId The ID of the tenant
     * @param settingId The ID of the setting to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteSetting(tenantId: Long, settingId: Long): Result<Unit>
}
