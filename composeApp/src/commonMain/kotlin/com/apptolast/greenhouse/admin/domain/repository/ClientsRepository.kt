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
    suspend fun getClientById(id: Long): Result<Client>

    /**
     * Fetches unique provinces from all clients.
     * @return Result containing list of province strings or error
     */
    suspend fun getProvinces(): Result<List<String>>

    /**
     * Fetches unique countries from all clients.
     * @return Result containing list of country strings or error
     */
    suspend fun getCountries(): Result<List<String>>

    /**
     * Creates a new client.
     * @param client The client data to create
     * @return Result containing the created Client or error
     */
    suspend fun createClient(client: Client): Result<Client>

    /**
     * Updates an existing client.
     * @param client The client data to update (must include valid id)
     * @return Result containing the updated Client or error
     */
    suspend fun updateClient(client: Client): Result<Client>

    /**
     * Deletes a client by ID.
     * @param id The client ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteClient(id: Long): Result<Unit>
}
