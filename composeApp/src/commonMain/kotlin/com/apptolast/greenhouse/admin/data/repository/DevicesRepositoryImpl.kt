package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogCategory
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogType
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogUnit
import com.apptolast.greenhouse.admin.data.model.DeviceCreateRequest
import com.apptolast.greenhouse.admin.data.model.DeviceUpdateRequest
import com.apptolast.greenhouse.admin.data.model.toDevice
import com.apptolast.greenhouse.admin.data.model.toDomain
import com.apptolast.greenhouse.admin.data.remote.api.DevicesApiService
import com.apptolast.greenhouse.admin.domain.repository.DevicesRepository

/**
 * Implementation of DevicesRepository that communicates with the backend API.
 */
class DevicesRepositoryImpl(
    private val devicesApi: DevicesApiService
) : DevicesRepository {

    override suspend fun getDevicesByTenantId(tenantId: String): Result<List<Device>> = runCatching {
        devicesApi.getDevicesByTenantId(tenantId).map { it.toDevice() }
    }

    override suspend fun createDevice(
        tenantId: String,
        greenhouseId: String,
        name: String?,
        categoryId: Short?,
        typeId: Short?,
        unitId: Short?,
        isActive: Boolean
    ): Result<Device> = runCatching {
        val request = DeviceCreateRequest(
            greenhouseId = greenhouseId,
            name = name,
            categoryId = categoryId,
            typeId = typeId,
            unitId = unitId,
            isActive = isActive
        )
        devicesApi.createDevice(tenantId, request).toDevice()
    }

    override suspend fun updateDevice(
        tenantId: String,
        deviceId: String,
        name: String?,
        categoryId: Short?,
        typeId: Short?,
        unitId: Short?,
        isActive: Boolean?
    ): Result<Device> = runCatching {
        val request = DeviceUpdateRequest(
            name = name,
            categoryId = categoryId,
            typeId = typeId,
            unitId = unitId,
            isActive = isActive
        )
        devicesApi.updateDevice(tenantId, deviceId, request).toDevice()
    }

    override suspend fun deleteDevice(tenantId: String, deviceId: String): Result<Unit> = runCatching {
        devicesApi.deleteDevice(tenantId, deviceId)
    }

    // ==================== CATALOG METHODS ====================

    override suspend fun getDeviceCategories(): Result<List<DeviceCatalogCategory>> = runCatching {
        devicesApi.getDeviceCategories().map { it.toDomain() }
    }

    override suspend fun getDeviceTypes(categoryId: Short?): Result<List<DeviceCatalogType>> = runCatching {
        devicesApi.getDeviceTypes(categoryId).map { it.toDomain() }
    }

    override suspend fun getUnits(): Result<List<DeviceCatalogUnit>> = runCatching {
        devicesApi.getUnits().map { it.toDomain() }
    }
}
