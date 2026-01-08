package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Response DTO from the API representing a Device.
 * Matches the DeviceResponse structure from InvernaderosAPI.
 */
@Serializable
data class DeviceResponse(
    val id: Long,
    val tenantId: Long,
    val greenhouseId: Long,
    val name: String? = null,
    val categoryId: Short? = null,
    val categoryName: String? = null,
    val typeId: Short? = null,
    val typeName: String? = null,
    val unitId: Short? = null,
    val unitSymbol: String? = null,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Request DTO for creating a new Device.
 */
@Serializable
data class DeviceCreateRequest(
    val greenhouseId: Long,
    val name: String? = null,
    val categoryId: Short? = null,
    val typeId: Short? = null,
    val unitId: Short? = null,
    val isActive: Boolean? = true
)

/**
 * Request DTO for updating an existing Device.
 */
@Serializable
data class DeviceUpdateRequest(
    val name: String? = null,
    val categoryId: Short? = null,
    val typeId: Short? = null,
    val unitId: Short? = null,
    val isActive: Boolean? = null
)

/**
 * Domain model representing a Device (Sensor or Actuator).
 * Used internally in the app for business logic.
 */
@Serializable
data class Device(
    val id: Long,
    val tenantId: Long,
    val greenhouseId: Long,
    val name: String? = null,
    val categoryId: Short? = null,
    val categoryName: String? = null,
    val typeId: Short? = null,
    val typeName: String? = null,
    val unitId: Short? = null,
    val unitSymbol: String? = null,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    /**
     * Device category enum for easier handling.
     */
    val category: DeviceCategory
        get() = when (categoryId) {
            CATEGORY_SENSOR -> DeviceCategory.SENSOR
            CATEGORY_ACTUATOR -> DeviceCategory.ACTUATOR
            else -> DeviceCategory.SENSOR
        }

    /**
     * Returns the first letter of the name or type name as initial for avatars.
     */
    val initial: String
        get() = name?.firstOrNull()?.uppercaseChar()?.toString()
            ?: typeName?.firstOrNull()?.uppercaseChar()?.toString()
            ?: categoryName?.firstOrNull()?.uppercaseChar()?.toString()
            ?: "D"

    /**
     * Returns a display name (name if available, otherwise category and type).
     */
    val displayName: String
        get() = name ?: buildString {
            if (typeName != null) {
                append(typeName)
            } else if (categoryName != null) {
                append(categoryName)
            } else {
                append("Device")
            }
            if (unitSymbol != null) {
                append(" ($unitSymbol)")
            }
        }

    /**
     * Returns a formatted category name for display.
     */
    val categoryDisplayName: String
        get() = categoryName ?: when (categoryId) {
            CATEGORY_SENSOR -> "Sensor"
            CATEGORY_ACTUATOR -> "Actuator"
            else -> "Unknown"
        }

    companion object {
        const val CATEGORY_SENSOR: Short = 1
        const val CATEGORY_ACTUATOR: Short = 2
    }
}

/**
 * Device category enum.
 */
enum class DeviceCategory {
    SENSOR,
    ACTUATOR
}

/**
 * Extension to convert DeviceResponse to domain model.
 */
fun DeviceResponse.toDevice() = Device(
    id = id,
    tenantId = tenantId,
    greenhouseId = greenhouseId,
    name = name,
    categoryId = categoryId,
    categoryName = categoryName,
    typeId = typeId,
    typeName = typeName,
    unitId = unitId,
    unitSymbol = unitSymbol,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ==================== CATALOG DTOs ====================

/**
 * Response DTO for Device Category from the catalog API.
 */
@Serializable
data class DeviceCategoryResponse(
    val id: Short,
    val name: String
)

/**
 * Response DTO for Device Type from the catalog API.
 */
@Serializable
data class DeviceTypeResponse(
    val id: Short,
    val name: String,
    val description: String? = null,
    val categoryId: Short,
    val categoryName: String? = null,
    val defaultUnitId: Short? = null,
    val defaultUnitSymbol: String? = null,
    val dataType: String? = null,
    val minExpectedValue: Double? = null,
    val maxExpectedValue: Double? = null,
    val controlType: String? = null,
    val isActive: Boolean = true
)

/**
 * Response DTO for Unit from the catalog API.
 */
@Serializable
data class DeviceUnitResponse(
    val id: Short,
    val symbol: String,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true
)

// ==================== CATALOG DOMAIN MODELS ====================

/**
 * Domain model for Device Category from catalog.
 */
data class DeviceCatalogCategory(
    val id: Short,
    val name: String
)

/**
 * Domain model for Device Type from catalog.
 */
data class DeviceCatalogType(
    val id: Short,
    val name: String,
    val description: String?,
    val categoryId: Short,
    val defaultUnitId: Short?,
    val defaultUnitSymbol: String?,
    val controlType: String?
)

/**
 * Domain model for Unit from catalog.
 */
data class DeviceCatalogUnit(
    val id: Short,
    val symbol: String,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true
)

// ==================== CATALOG EXTENSION FUNCTIONS ====================

fun DeviceCategoryResponse.toDomain() = DeviceCatalogCategory(id, name)

fun DeviceTypeResponse.toDomain() = DeviceCatalogType(
    id = id,
    name = name,
    description = description,
    categoryId = categoryId,
    defaultUnitId = defaultUnitId,
    defaultUnitSymbol = defaultUnitSymbol,
    controlType = controlType
)

fun DeviceUnitResponse.toDomain() = DeviceCatalogUnit(
    id = id,
    symbol = symbol,
    name = name,
    description = description,
    isActive = isActive
)

// ==================== CATALOG REQUEST DTOs ====================

/**
 * Request DTO for creating a Device Category.
 * Note: ID is auto-generated by the backend.
 */
@Serializable
data class DeviceCategoryCreateRequest(
    val name: String
)

/**
 * Request DTO for updating a Device Category.
 */
@Serializable
data class DeviceCategoryUpdateRequest(
    val name: String? = null
)

/**
 * Request DTO for creating a Device Type.
 * Note: ID is auto-generated by the backend.
 */
@Serializable
data class DeviceTypeCreateRequest(
    val name: String,
    val description: String? = null,
    val categoryId: Short,
    val defaultUnitId: Short? = null,
    val dataType: String? = null,
    val minExpectedValue: Double? = null,
    val maxExpectedValue: Double? = null,
    val controlType: String? = null,
    val isActive: Boolean = true
)

/**
 * Request DTO for updating a Device Type.
 */
@Serializable
data class DeviceTypeUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val categoryId: Short? = null,
    val defaultUnitId: Short? = null,
    val dataType: String? = null,
    val minExpectedValue: Double? = null,
    val maxExpectedValue: Double? = null,
    val controlType: String? = null,
    val isActive: Boolean? = null
)

// ==================== DEVICE UNIT REQUEST DTOs ====================

/**
 * Request DTO for creating a Device Unit.
 * Note: ID is auto-generated by the backend.
 */
@Serializable
data class DeviceUnitCreateRequest(
    val symbol: String,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true
)

/**
 * Request DTO for updating a Device Unit.
 */
@Serializable
data class DeviceUnitUpdateRequest(
    val symbol: String? = null,
    val name: String? = null,
    val description: String? = null,
    val isActive: Boolean? = null
)

// ==================== ACTUATOR STATE DTOs ====================

/**
 * Response DTO for Actuator State from the catalog API.
 * Represents possible states for actuator devices (ON, OFF, STANDBY, etc.).
 */
@Serializable
data class ActuatorStateResponse(
    val id: Short,
    val name: String,
    val description: String? = null,
    val isOperational: Boolean = false,
    val displayOrder: Short = 0,
    val color: String? = null
)

/**
 * Request DTO for creating an Actuator State.
 * Note: ID is auto-generated by the backend.
 */
@Serializable
data class ActuatorStateCreateRequest(
    val name: String,
    val description: String? = null,
    val isOperational: Boolean = false,
    val displayOrder: Short = 0,
    val color: String? = null
)

/**
 * Request DTO for updating an Actuator State.
 */
@Serializable
data class ActuatorStateUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val isOperational: Boolean? = null,
    val displayOrder: Short? = null,
    val color: String? = null
)

// ==================== ACTUATOR STATE DOMAIN MODEL ====================

/**
 * Domain model for Actuator State from catalog.
 */
data class ActuatorState(
    val id: Short,
    val name: String,
    val description: String?,
    val isOperational: Boolean,
    val displayOrder: Short,
    val color: String?
)

/**
 * Converts ActuatorStateResponse DTO to domain model.
 */
fun ActuatorStateResponse.toDomain() = ActuatorState(
    id = id,
    name = name,
    description = description,
    isOperational = isOperational,
    displayOrder = displayOrder,
    color = color
)
