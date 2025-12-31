package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.Client

/**
 * Repository interface for client data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface ClientsRepository {
    /**
     * Fetches all clients.
     * @return Result containing list of Client or error
     */
    suspend fun getClients(): Result<List<Client>>

    /**
     * Fetches a specific client by ID.
     * @param id The client ID
     * @return Result containing Client or error
     */
    suspend fun getClientById(id: String): Result<Client>

    /**
     * Fetches unique locations (countries) from all clients.
     * @return Result containing list of location strings or error
     */
    suspend fun getLocations(): Result<List<String>>

    /**
     * Deletes a client by ID.
     * @param id The client ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteClient(id: String): Result<Unit>
}
