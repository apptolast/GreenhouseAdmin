package com.apptolast.greenhouse.admin.domain.repository

import com.apptolast.greenhouse.admin.data.model.User
import com.apptolast.greenhouse.admin.data.model.UserRole

/**
 * Repository interface for user data operations.
 * All methods return Result<T> for consistent error handling.
 */
interface UsersRepository {
    /**
     * Fetches all users for a specific tenant.
     * @param tenantId The tenant ID to filter users by
     * @return Result containing list of User or error
     */
    suspend fun getUsersByTenantId(tenantId: String): Result<List<User>>

    /**
     * Creates a new user for a tenant.
     * @param tenantId The tenant ID the user belongs to
     * @param username The username for login
     * @param email The user's email address
     * @param password The raw password (will be hashed by backend)
     * @param role The user's role
     * @param isActive Whether the user is active
     * @return Result containing the created User or error
     */
    suspend fun createUser(
        tenantId: String,
        username: String,
        email: String,
        password: String,
        role: UserRole,
        isActive: Boolean = true
    ): Result<User>

    /**
     * Updates an existing user.
     * All update fields are optional for partial updates.
     * @param tenantId The tenant ID the user belongs to
     * @param userId The user ID to update
     * @param username New username (optional)
     * @param email New email (optional)
     * @param password New password (optional)
     * @param role New role (optional)
     * @param isActive New active status (optional)
     * @return Result containing the updated User or error
     */
    suspend fun updateUser(
        tenantId: String,
        userId: String,
        username: String? = null,
        email: String? = null,
        password: String? = null,
        role: UserRole? = null,
        isActive: Boolean? = null
    ): Result<User>

    /**
     * Deletes a user by ID.
     * @param tenantId The tenant ID the user belongs to
     * @param userId The user ID to delete
     * @return Result containing success or error
     */
    suspend fun deleteUser(tenantId: String, userId: String): Result<Unit>
}
