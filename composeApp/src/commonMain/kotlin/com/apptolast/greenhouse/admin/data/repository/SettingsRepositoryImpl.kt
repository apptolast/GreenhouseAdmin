package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.domain.repository.SettingsRepository
import kotlinx.coroutines.delay
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Mock implementation of SettingsRepository.
 * Provides hardcoded data for development. Replace with real API calls later.
 */
class SettingsRepositoryImpl : SettingsRepository {

    private val mockSettings = mutableListOf(
        // Settings for client "1" (Elena Rodriguez)
        Setting(
            id = "s1",
            key = "notification_email",
            value = "elena@greenhouse.com",
            description = "Email address for receiving notifications",
            clientId = "1"
        ),
        Setting(
            id = "s2",
            key = "temperature_unit",
            value = "celsius",
            description = "Temperature display unit (celsius/fahrenheit)",
            clientId = "1"
        ),
        Setting(
            id = "s3",
            key = "alert_threshold_temp",
            value = "35",
            description = "Temperature threshold for high alerts (degrees)",
            clientId = "1"
        ),
        Setting(
            id = "s4",
            key = "language",
            value = "es",
            description = "Preferred language for communications",
            clientId = "1"
        ),
        // Settings for client "2" (Jean Pierre)
        Setting(
            id = "s5",
            key = "language",
            value = "fr",
            description = "Langue preferee pour les communications",
            clientId = "2"
        ),
        // Settings for client "4" (Maria Muller)
        Setting(
            id = "s6",
            key = "temperature_unit",
            value = "celsius",
            description = "Temperatureinheit (celsius/fahrenheit)",
            clientId = "4"
        )
    )

    override suspend fun getSettingsByClientId(clientId: String): Result<List<Setting>> = runCatching {
        delay(400)
        mockSettings.filter { it.clientId == clientId }.sortedBy { it.key }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createSetting(setting: Setting): Result<Setting> = runCatching {
        delay(600)
        val newSetting = setting.copy(id = Uuid.random().toString())
        mockSettings.add(newSetting)
        newSetting
    }

    override suspend fun updateSetting(setting: Setting): Result<Setting> = runCatching {
        delay(500)
        val index = mockSettings.indexOfFirst { it.id == setting.id }
        if (index == -1) {
            throw NoSuchElementException("Setting with id ${setting.id} not found")
        }
        mockSettings[index] = setting
        setting
    }

    override suspend fun deleteSetting(id: String): Result<Unit> = runCatching {
        delay(400)
        val index = mockSettings.indexOfFirst { it.id == id }
        if (index == -1) {
            throw NoSuchElementException("Setting with id $id not found")
        }
        mockSettings.removeAt(index)
    }
}
