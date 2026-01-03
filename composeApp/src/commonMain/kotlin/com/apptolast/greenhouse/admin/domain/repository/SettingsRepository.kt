package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.Setting

/**
 * Repository interface for managing settings.
 * Defines the contract for settings data operations.
 */
interface SettingsRepository {

    /**
     * Retrieves all settings for a specific client.
     * @param clientId The ID of the client
     * @return Result containing list of settings or error
     */
    suspend fun getSettingsByClientId(clientId: String): Result<List<Setting>>

    /**
     * Creates a new setting.
     * @param setting The setting to create (id will be generated)
     * @return Result containing the created setting with generated ID or error
     */
    suspend fun createSetting(setting: Setting): Result<Setting>

    /**
     * Updates an existing setting.
     * @param setting The setting with updated values
     * @return Result containing the updated setting or error
     */
    suspend fun updateSetting(setting: Setting): Result<Setting>

    /**
     * Deletes a setting by ID.
     * @param id The ID of the setting to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteSetting(id: String): Result<Unit>
}
