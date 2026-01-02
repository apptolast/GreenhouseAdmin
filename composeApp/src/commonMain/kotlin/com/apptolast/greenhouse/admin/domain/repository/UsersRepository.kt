package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.User

/**
 * Repository interface for user data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface UsersRepository {
    /**
     * Fetches all users for a specific client.
     * @param clientId The client ID to filter users by
     * @return Result containing list of User or error
     */
    suspend fun getUsersByClientId(clientId: String): Result<List<User>>

    /**
     * Creates a new user for a client.
     * @param user The user data to create
     * @return Result containing the created User or error
     */
    suspend fun createUser(user: User): Result<User>

    /**
     * Updates an existing user.
     * @param user The user data to update (must include valid id)
     * @return Result containing the updated User or error
     */
    suspend fun updateUser(user: User): Result<User>

    /**
     * Deletes a user by ID.
     * @param id The user ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteUser(id: String): Result<Unit>
}
