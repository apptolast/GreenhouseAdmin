package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.Sector

/**
 * Repository interface for sector data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface SectorsRepository {
    /**
     * Fetches all sectors for a specific client.
     * @param clientId The client ID to filter sectors by
     * @return Result containing list of Sector or error
     */
    suspend fun getSectorsByClientId(clientId: String): Result<List<Sector>>

    /**
     * Creates a new sector for a client.
     * @param sector The sector data to create
     * @return Result containing the created Sector or error
     */
    suspend fun createSector(sector: Sector): Result<Sector>

    /**
     * Updates an existing sector.
     * @param sector The sector data to update (must include valid id)
     * @return Result containing the updated Sector or error
     */
    suspend fun updateSector(sector: Sector): Result<Sector>

    /**
     * Deletes a sector by ID.
     * @param id The sector ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteSector(id: String): Result<Unit>
}
