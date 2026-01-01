package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.User
import com.apptolast.greenhouse.admin.domain.repository.UsersRepository
import kotlinx.coroutines.delay
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Mock implementation of UsersRepository.
 * Provides hardcoded data for development. Replace with real API calls later.
 */
class UsersRepositoryImpl : UsersRepository {

    private val mockUsers = mutableListOf(
        // Users for client "1" (Elena Rodriguez)
        User(
            id = "u1",
            name = "Carlos Garcia",
            email = "carlos@freshveg.com",
            phone = "+34 612 111 111",
            clientId = "1"
        ),
        User(
            id = "u2",
            name = "Ana Martinez",
            email = "ana@freshveg.com",
            phone = "+34 612 222 222",
            clientId = "1"
        ),
        // Users for client "2" (Jean Pierre)
        User(
            id = "u3",
            name = "Marie Dupont",
            email = "marie@organicfarms.fr",
            phone = "+33 6 33 44 55 66",
            clientId = "2"
        ),
        // Users for client "4" (Maria Muller)
        User(
            id = "u4",
            name = "Hans Weber",
            email = "hans@eco-grow.at",
            phone = "+43 660 7777777",
            clientId = "4"
        ),
        User(
            id = "u5",
            name = "Lisa Schmidt",
            email = "lisa@eco-grow.at",
            phone = "+43 660 8888888",
            clientId = "4"
        ),
        User(
            id = "u6",
            name = "Peter Braun",
            email = "peter@eco-grow.at",
            phone = "+43 660 9999999",
            clientId = "4"
        )
    )

    override suspend fun getUsersByClientId(clientId: String): Result<List<User>> = runCatching {
        delay(400)
        mockUsers.filter { it.clientId == clientId }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createUser(user: User): Result<User> = runCatching {
        delay(600)
        val newUser = user.copy(id = Uuid.random().toString())
        mockUsers.add(newUser)
        newUser
    }

    override suspend fun updateUser(user: User): Result<User> = runCatching {
        delay(500)
        val index = mockUsers.indexOfFirst { it.id == user.id }
        if (index == -1) {
            throw NoSuchElementException("User with id ${user.id} not found")
        }
        mockUsers[index] = user
        user
    }

    override suspend fun deleteUser(id: String): Result<Unit> = runCatching {
        delay(400)
        val index = mockUsers.indexOfFirst { it.id == id }
        if (index == -1) {
            throw NoSuchElementException("User with id $id not found")
        }
        mockUsers.removeAt(index)
    }
}
