package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.Device

/**
 * Repository interface for device data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface DevicesRepository {
    /**
     * Fetches all devices for a specific client.
     * @param clientId The client ID to filter devices by
     * @return Result containing list of Device or error
     */
    suspend fun getDevicesByClientId(clientId: String): Result<List<Device>>

    /**
     * Creates a new device for a client.
     * @param device The device data to create
     * @return Result containing the created Device or error
     */
    suspend fun createDevice(device: Device): Result<Device>

    /**
     * Updates an existing device.
     * @param device The device data to update (must include valid id)
     * @return Result containing the updated Device or error
     */
    suspend fun updateDevice(device: Device): Result<Device>

    /**
     * Deletes a device by ID.
     * @param id The device ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteDevice(id: String): Result<Unit>
}
