package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.AlertSeverity
import com.apptolast.greenhouse.admin.data.model.AlertStatus
import com.apptolast.greenhouse.admin.domain.repository.AlertsRepository
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Mock implementation of AlertsRepository.
 * Provides hardcoded data for development. Replace with real API calls later.
 */
class AlertsRepositoryImpl : AlertsRepository {

    private val mockAlerts = mutableListOf(
        // Alerts for client "1" (Elena Rodriguez)
        Alert(
            id = "a1",
            title = "Temperatura alta en Sector A",
            severity = AlertSeverity.HIGH,
            status = AlertStatus.UNREAD,
            createdAt = Clock.System.now().toEpochMilliseconds() - 3600000, // 1 hour ago
            clientId = "1"
        ),
        Alert(
            id = "a2",
            title = "Humedad baja detectada",
            severity = AlertSeverity.MEDIUM,
            status = AlertStatus.READ,
            createdAt = Clock.System.now().toEpochMilliseconds() - 86400000, // 1 day ago
            clientId = "1"
        ),
        Alert(
            id = "a3",
            title = "Mantenimiento programado",
            severity = AlertSeverity.LOW,
            status = AlertStatus.DISMISSED,
            createdAt = Clock.System.now().toEpochMilliseconds() - 172800000, // 2 days ago
            clientId = "1"
        ),
        Alert(
            id = "a4",
            title = "Sensor CO2 desconectado",
            severity = AlertSeverity.CRITICAL,
            status = AlertStatus.UNREAD,
            createdAt = Clock.System.now().toEpochMilliseconds() - 1800000, // 30 minutes ago
            clientId = "1"
        ),
        // Alerts for client "2" (Jean Pierre)
        Alert(
            id = "a5",
            title = "Alerte temperature basse",
            severity = AlertSeverity.MEDIUM,
            status = AlertStatus.UNREAD,
            createdAt = Clock.System.now().toEpochMilliseconds() - 7200000, // 2 hours ago
            clientId = "2"
        ),
        // Alerts for client "4" (Maria Muller)
        Alert(
            id = "a6",
            title = "Bewaesserungssystem Fehler",
            severity = AlertSeverity.HIGH,
            status = AlertStatus.UNREAD,
            createdAt = Clock.System.now().toEpochMilliseconds() - 14400000, // 4 hours ago
            clientId = "4"
        )
    )

    override suspend fun getAlertsByClientId(clientId: String): Result<List<Alert>> = runCatching {
        delay(400)
        mockAlerts.filter { it.clientId == clientId }.sortedByDescending { it.createdAt }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createAlert(alert: Alert): Result<Alert> = runCatching {
        delay(600)
        val newAlert = alert.copy(
            id = Uuid.random().toString(),
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        mockAlerts.add(newAlert)
        newAlert
    }

    override suspend fun updateAlert(alert: Alert): Result<Alert> = runCatching {
        delay(500)
        val index = mockAlerts.indexOfFirst { it.id == alert.id }
        if (index == -1) {
            throw NoSuchElementException("Alert with id ${alert.id} not found")
        }
        mockAlerts[index] = alert
        alert
    }

    override suspend fun deleteAlert(id: String): Result<Unit> = runCatching {
        delay(400)
        val index = mockAlerts.indexOfFirst { it.id == id }
        if (index == -1) {
            throw NoSuchElementException("Alert with id $id not found")
        }
        mockAlerts.removeAt(index)
    }
}
