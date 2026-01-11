package com.apptolast.greenhouse.admin.data.model

import kotlinx.serialization.Serializable

/**
 * DTO for tenant response from the API.
 * Maps to the TenantResponse from InvernaderosAPI.
 */
@Serializable
data class TenantResponse(
    val id: Long,
    val code: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val province: String? = null,
    val country: String? = null,
    val location: Location? = null,
    val isActive: Boolean? = true,
    val status: String = "Activo"
)

/**
 * DTO for creating a new tenant.
 * Matches TenantCreateRequest from InvernaderosAPI.
 */
@Serializable
data class CreateTenantRequest(
    val name: String,
    val email: String,
    val phone: String? = null,
    val province: String? = null,
    val country: String? = "España",
    val location: Location? = null,
    val status: String? = "Activo"
)

/**
 * DTO for updating an existing tenant.
 * Matches TenantUpdateRequest from InvernaderosAPI.
 * All fields are optional for partial updates.
 */
@Serializable
data class UpdateTenantRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val province: String? = null,
    val country: String? = null,
    val location: Location? = null,
    val status: String? = null
)

/**
 * Extension function to convert API response to domain model.
 */
fun TenantResponse.toClient(): Client = Client(
    id = id,
    code = code,
    name = name,
    email = email,
    phone = phone ?: "",
    province = province ?: "",
    country = country ?: "",
    location = location,
    isActive = isActive,
    status = when (status) {
        "Activo" -> ClientStatus.ACTIVE
        "Inactivo" -> ClientStatus.INACTIVE
        "Pendiente" -> ClientStatus.PENDING
        else -> ClientStatus.ACTIVE
    }
)

/**
 * Extension function to convert domain model to create request.
 */
fun Client.toCreateRequest(): CreateTenantRequest = CreateTenantRequest(
    name = name,
    email = email,
    phone = phone.ifBlank { null },
    province = province.ifBlank { null },
    country = country.ifBlank { "España" },
    location = location,
    status = status.toApiStatus()
)

/**
 * Extension function to convert domain model to update request.
 */
fun Client.toUpdateRequest(): UpdateTenantRequest = UpdateTenantRequest(
    name = name,
    email = email,
    phone = phone.ifBlank { null },
    province = province.ifBlank { null },
    country = country.ifBlank { null },
    location = location,
    status = status.toApiStatus()
)

/**
 * Convert ClientStatus enum to API status string.
 */
fun ClientStatus.toApiStatus(): String = when (this) {
    ClientStatus.ACTIVE -> "Activo"
    ClientStatus.INACTIVE -> "Inactivo"
    ClientStatus.PENDING -> "Pendiente"
}
