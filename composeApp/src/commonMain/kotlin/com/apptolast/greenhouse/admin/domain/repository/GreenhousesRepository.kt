package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Location

/**
 * Repository interface for greenhouse data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface GreenhousesRepository {
    /**
     * Fetches all greenhouses for a specific tenant.
     * @param tenantId The tenant ID to filter greenhouses by
     * @return Result containing list of Greenhouse or error
     */
    suspend fun getGreenhousesByTenantId(tenantId: Long): Result<List<Greenhouse>>

    /**
     * Creates a new greenhouse for a tenant.
     * @param tenantId The tenant ID the greenhouse belongs to
     * @param name The greenhouse name
     * @param location Optional geographic location
     * @param areaM2 Optional area in square meters
     * @param timezone Optional timezone (defaults to Europe/Madrid)
     * @param isActive Whether the greenhouse is active
     * @return Result containing the created Greenhouse or error
     */
    suspend fun createGreenhouse(
        tenantId: Long,
        name: String,
        location: Location? = null,
        areaM2: Double? = null,
        timezone: String? = "Europe/Madrid",
        isActive: Boolean = true
    ): Result<Greenhouse>

    /**
     * Updates an existing greenhouse.
     * All update fields are optional for partial updates.
     * @param tenantId The tenant ID the greenhouse belongs to
     * @param greenhouseId The greenhouse ID to update
     * @param name New name (optional)
     * @param location New location (optional)
     * @param areaM2 New area (optional)
     * @param timezone New timezone (optional)
     * @param isActive New active status (optional)
     * @return Result containing the updated Greenhouse or error
     */
    suspend fun updateGreenhouse(
        tenantId: Long,
        greenhouseId: Long,
        name: String? = null,
        location: Location? = null,
        areaM2: Double? = null,
        timezone: String? = null,
        isActive: Boolean? = null
    ): Result<Greenhouse>

    /**
     * Deletes a greenhouse by ID.
     * @param tenantId The tenant ID the greenhouse belongs to
     * @param greenhouseId The greenhouse ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteGreenhouse(tenantId: Long, greenhouseId: Long): Result<Unit>
}
