package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.Sector

/**
 * Repository interface for sector data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface SectorsRepository {
    /**
     * Fetches all sectors for a specific tenant.
     * @param tenantId The tenant ID to filter sectors by
     * @return Result containing list of Sector or error
     */
    suspend fun getSectorsByTenantId(tenantId: Long): Result<List<Sector>>

    /**
     * Creates a new sector for a tenant.
     * @param tenantId The tenant ID the sector belongs to
     * @param greenhouseId The greenhouse ID the sector belongs to
     * @param variety The variety/name of the sector
     * @return Result containing the created Sector or error
     */
    suspend fun createSector(
        tenantId: Long,
        greenhouseId: Long,
        variety: String?
    ): Result<Sector>

    /**
     * Updates an existing sector.
     * @param tenantId The tenant ID the sector belongs to
     * @param sectorId The sector ID to update
     * @param variety New variety/name (optional)
     * @return Result containing the updated Sector or error
     */
    suspend fun updateSector(
        tenantId: Long,
        sectorId: Long,
        variety: String?
    ): Result<Sector>

    /**
     * Deletes a sector by ID.
     * @param tenantId The tenant ID the sector belongs to
     * @param sectorId The sector ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteSector(tenantId: Long, sectorId: Long): Result<Unit>
}
