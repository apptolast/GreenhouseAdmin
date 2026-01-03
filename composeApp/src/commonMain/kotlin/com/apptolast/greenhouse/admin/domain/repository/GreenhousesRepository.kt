package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.Greenhouse

/**
 * Repository interface for greenhouse data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface GreenhousesRepository {
    /**
     * Fetches all greenhouses for a specific client.
     * @param clientId The client ID to filter greenhouses by
     * @return Result containing list of Greenhouse or error
     */
    suspend fun getGreenhousesByClientId(clientId: String): Result<List<Greenhouse>>

    /**
     * Creates a new greenhouse for a client.
     * @param greenhouse The greenhouse data to create
     * @return Result containing the created Greenhouse or error
     */
    suspend fun createGreenhouse(greenhouse: Greenhouse): Result<Greenhouse>

    /**
     * Updates an existing greenhouse.
     * @param greenhouse The greenhouse data to update (must include valid id)
     * @return Result containing the updated Greenhouse or error
     */
    suspend fun updateGreenhouse(greenhouse: Greenhouse): Result<Greenhouse>

    /**
     * Deletes a greenhouse by ID.
     * @param id The greenhouse ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteGreenhouse(id: String): Result<Unit>
}
