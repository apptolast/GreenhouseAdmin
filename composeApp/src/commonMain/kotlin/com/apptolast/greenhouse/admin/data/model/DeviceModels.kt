package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * Response DTO from the API representing a Device.
 * Matches the DeviceResponse structure from InvernaderosAPI.
 */
@Serializable
data class DeviceResponse(
    val id: String,
    val tenantId: String,
    val greenhouseId: String,
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
    val greenhouseId: String,
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
    val id: String,
    val tenantId: String,
    val greenhouseId: String,
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
     * Returns the first letter of the type name as initial for avatars.
     */
    val initial: String
        get() = typeName?.firstOrNull()?.uppercaseChar()?.toString()
            ?: categoryName?.firstOrNull()?.uppercaseChar()?.toString()
            ?: "D"

    /**
     * Returns a display name combining category and type.
     */
    val displayName: String
        get() = buildString {
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
    val name: String
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

fun DeviceUnitResponse.toDomain() = DeviceCatalogUnit(id, symbol, name)
