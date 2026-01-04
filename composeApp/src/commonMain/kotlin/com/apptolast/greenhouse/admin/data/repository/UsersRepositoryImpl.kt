package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.User
import com.apptolast.greenhouse.admin.data.model.UserCreateRequest
import com.apptolast.greenhouse.admin.data.model.UserRole
import com.apptolast.greenhouse.admin.data.model.UserUpdateRequest
import com.apptolast.greenhouse.admin.data.model.toUser
import com.apptolast.greenhouse.admin.data.remote.api.UsersApiService
import com.apptolast.greenhouse.admin.domain.repository.UsersRepository

/**
 * Implementation of UsersRepository that communicates with the backend API.
 */
class UsersRepositoryImpl(
    private val usersApi: UsersApiService
) : UsersRepository {

    override suspend fun getUsersByTenantId(tenantId: String): Result<List<User>> = runCatching {
        usersApi.getUsersByTenantId(tenantId).map { it.toUser() }
    }

    override suspend fun createUser(
        tenantId: String,
        username: String,
        email: String,
        password: String,
        role: UserRole,
        isActive: Boolean
    ): Result<User> = runCatching {
        val request = UserCreateRequest(
            username = username,
            email = email,
            passwordRaw = password,
            role = role.name,
            isActive = isActive
        )
        usersApi.createUser(tenantId, request).toUser()
    }

    override suspend fun updateUser(
        tenantId: String,
        userId: String,
        username: String?,
        email: String?,
        password: String?,
        role: UserRole?,
        isActive: Boolean?
    ): Result<User> = runCatching {
        val request = UserUpdateRequest(
            username = username,
            email = email,
            passwordRaw = password,
            role = role?.name,
            isActive = isActive
        )
        usersApi.updateUser(tenantId, userId, request).toUser()
    }

    override suspend fun deleteUser(tenantId: String, userId: String): Result<Unit> = runCatching {
        usersApi.deleteUser(tenantId, userId)
    }
}
