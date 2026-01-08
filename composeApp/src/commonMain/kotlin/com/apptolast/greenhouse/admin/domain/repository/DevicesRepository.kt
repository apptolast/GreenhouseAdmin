package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogCategory
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogType
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogUnit

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
    suspend fun getDevicesByTenantId(tenantId: Long): Result<List<Device>>

    /**
     * Creates a new device for a tenant.
     * @param tenantId The tenant ID the device belongs to
     * @param greenhouseId The greenhouse ID where the device is installed
     * @param name Optional display name for the device
     * @param categoryId Device category (1=SENSOR, 2=ACTUATOR)
     * @param typeId Device type ID
     * @param unitId Unit of measure ID
     * @param isActive Whether the device is active
     * @return Result containing the created Device or error
     */
    suspend fun createDevice(
        tenantId: Long,
        greenhouseId: Long,
        name: String?,
        categoryId: Short?,
        typeId: Short?,
        unitId: Short?,
        isActive: Boolean
    ): Result<Device>

    /**
     * Updates an existing device.
     * @param tenantId The tenant ID the device belongs to
     * @param deviceId The device ID to update
     * @param name New display name (optional)
     * @param categoryId New category ID (optional)
     * @param typeId New type ID (optional)
     * @param unitId New unit ID (optional)
     * @param isActive New active status (optional)
     * @return Result containing the updated Device or error
     */
    suspend fun updateDevice(
        tenantId: Long,
        deviceId: Long,
        name: String?,
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
    suspend fun deleteDevice(tenantId: Long, deviceId: Long): Result<Unit>

    // ==================== CATALOG METHODS ====================

    /**
     * Fetches all device categories from the catalog.
     * @return Result containing list of DeviceCatalogCategory or error
     */
    suspend fun getDeviceCategories(): Result<List<DeviceCatalogCategory>>

    /**
     * Fetches device types from the catalog, optionally filtered by category.
     * @param categoryId Optional category ID to filter types (1=SENSOR, 2=ACTUATOR)
     * @return Result containing list of DeviceCatalogType or error
     */
    suspend fun getDeviceTypes(categoryId: Short? = null): Result<List<DeviceCatalogType>>

    /**
     * Fetches all units from the catalog.
     * @return Result containing list of DeviceCatalogUnit or error
     */
    suspend fun getUnits(): Result<List<DeviceCatalogUnit>>
}
