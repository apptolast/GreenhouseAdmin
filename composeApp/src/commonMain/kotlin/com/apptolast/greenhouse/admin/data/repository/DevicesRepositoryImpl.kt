package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.DeviceCreateRequest
import com.apptolast.greenhouse.admin.data.model.DeviceUpdateRequest
import com.apptolast.greenhouse.admin.data.model.toDevice
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
        categoryId: Short?,
        typeId: Short?,
        unitId: Short?,
        isActive: Boolean
    ): Result<Device> = runCatching {
        val request = DeviceCreateRequest(
            greenhouseId = greenhouseId,
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
        categoryId: Short?,
        typeId: Short?,
        unitId: Short?,
        isActive: Boolean?
    ): Result<Device> = runCatching {
        val request = DeviceUpdateRequest(
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
}
