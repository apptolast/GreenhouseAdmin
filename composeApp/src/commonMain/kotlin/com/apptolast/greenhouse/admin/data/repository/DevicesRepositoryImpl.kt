package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.DeviceStatus
import com.apptolast.greenhouse.admin.data.model.DeviceType
import com.apptolast.greenhouse.admin.domain.repository.DevicesRepository
import kotlinx.coroutines.delay
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Mock implementation of DevicesRepository.
 * Provides hardcoded data for development. Replace with real API calls later.
 */
class DevicesRepositoryImpl : DevicesRepository {

    private val mockDevices = mutableListOf(
        // Devices for client "1" (Elena Rodriguez)
        Device(
            id = "d1",
            name = "Sensor Temperatura A1",
            type = DeviceType.SENSOR,
            status = DeviceStatus.ONLINE,
            clientId = "1"
        ),
        Device(
            id = "d2",
            name = "Sensor Humedad A1",
            type = DeviceType.SENSOR,
            status = DeviceStatus.ONLINE,
            clientId = "1"
        ),
        Device(
            id = "d3",
            name = "Valvula Riego Norte",
            type = DeviceType.ACTUATOR,
            status = DeviceStatus.ONLINE,
            clientId = "1"
        ),
        Device(
            id = "d4",
            name = "Sensor CO2 B2",
            type = DeviceType.SENSOR,
            status = DeviceStatus.OFFLINE,
            clientId = "1"
        ),
        // Devices for client "2" (Jean Pierre)
        Device(
            id = "d5",
            name = "Capteur Temperature",
            type = DeviceType.SENSOR,
            status = DeviceStatus.ONLINE,
            clientId = "2"
        ),
        Device(
            id = "d6",
            name = "Vanne Irrigation",
            type = DeviceType.ACTUATOR,
            status = DeviceStatus.ONLINE,
            clientId = "2"
        ),
        // Devices for client "4" (Maria Muller)
        Device(
            id = "d7",
            name = "Temperatursensor 1",
            type = DeviceType.SENSOR,
            status = DeviceStatus.ONLINE,
            clientId = "4"
        ),
        Device(
            id = "d8",
            name = "Bewaesserungsventil",
            type = DeviceType.ACTUATOR,
            status = DeviceStatus.OFFLINE,
            clientId = "4"
        )
    )

    override suspend fun getDevicesByClientId(clientId: String): Result<List<Device>> = runCatching {
        delay(400)
        mockDevices.filter { it.clientId == clientId }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createDevice(device: Device): Result<Device> = runCatching {
        delay(600)
        val newDevice = device.copy(id = Uuid.random().toString())
        mockDevices.add(newDevice)
        newDevice
    }

    override suspend fun updateDevice(device: Device): Result<Device> = runCatching {
        delay(500)
        val index = mockDevices.indexOfFirst { it.id == device.id }
        if (index == -1) {
            throw NoSuchElementException("Device with id ${device.id} not found")
        }
        mockDevices[index] = device
        device
    }

    override suspend fun deleteDevice(id: String): Result<Unit> = runCatching {
        delay(400)
        val index = mockDevices.indexOfFirst { it.id == id }
        if (index == -1) {
            throw NoSuchElementException("Device with id $id not found")
        }
        mockDevices.removeAt(index)
    }
}
