package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.Device

/**
 * Repository interface for device data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface DevicesRepository {
    /**
     * Fetches all devices for a specific tenant.
     * @param tenantId The tenant ID to filter devices by
     * @return Result containing list of Device or error
     */
    suspend fun getDevicesByTenantId(tenantId: String): Result<List<Device>>

    /**
     * Creates a new device for a tenant.
     * @param tenantId The tenant ID the device belongs to
     * @param greenhouseId The greenhouse ID where the device is installed
     * @param categoryId Device category (1=SENSOR, 2=ACTUATOR)
     * @param typeId Device type ID
     * @param unitId Unit of measure ID
     * @param isActive Whether the device is active
     * @return Result containing the created Device or error
     */
    suspend fun createDevice(
        tenantId: String,
        greenhouseId: String,
        categoryId: Short?,
        typeId: Short?,
        unitId: Short?,
        isActive: Boolean
    ): Result<Device>

    /**
     * Updates an existing device.
     * @param tenantId The tenant ID the device belongs to
     * @param deviceId The device ID to update
     * @param categoryId New category ID (optional)
     * @param typeId New type ID (optional)
     * @param unitId New unit ID (optional)
     * @param isActive New active status (optional)
     * @return Result containing the updated Device or error
     */
    suspend fun updateDevice(
        tenantId: String,
        deviceId: String,
        categoryId: Short?,
        typeId: Short?,
        unitId: Short?,
        isActive: Boolean?
    ): Result<Device>

    /**
     * Deletes a device by ID.
     * @param tenantId The tenant ID the device belongs to
     * @param deviceId The device ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteDevice(tenantId: String, deviceId: String): Result<Unit>
}
