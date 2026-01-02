package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.Alert

/**
 * Repository interface for alert data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface AlertsRepository {
    /**
     * Fetches all alerts for a specific client.
     * @param clientId The client ID to filter alerts by
     * @return Result containing list of Alert or error
     */
    suspend fun getAlertsByClientId(clientId: String): Result<List<Alert>>

    /**
     * Creates a new alert for a client.
     * @param alert The alert data to create
     * @return Result containing the created Alert or error
     */
    suspend fun createAlert(alert: Alert): Result<Alert>

    /**
     * Updates an existing alert.
     * @param alert The alert data to update (must include valid id)
     * @return Result containing the updated Alert or error
     */
    suspend fun updateAlert(alert: Alert): Result<Alert>

    /**
     * Deletes an alert by ID.
     * @param id The alert ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteAlert(id: String): Result<Unit>
}
