package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.domain.repository.ClientsRepository
import kotlinx.coroutines.delay
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Mock implementation of ClientsRepository.
 * Provides hardcoded data for development. Replace with real API calls later.
 */
class ClientsRepositoryImpl : ClientsRepository {

    private val currentTime = 1735689600000L // 2025-01-01 00:00:00 UTC

    private var mockClients = listOf(
        Client(
            id = "1",
            name = "Elena Rodriguez",
            email = "elena@freshveg.com",
            phone = "+34 612 345 678",
            province = "Almeria",
            country = "Spain",
            location = "Calle Mayor 123",
            createdAt = currentTime - 86400000L * 30,
            updatedAt = currentTime,
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "2",
            name = "Jean Pierre",
            email = "j.pierre@organicfarms.fr",
            phone = "+33 6 12 34 56 78",
            province = "Loire-Atlantique",
            country = "France",
            location = "15 Rue des Vignes",
            createdAt = currentTime - 86400000L * 60,
            updatedAt = currentTime - 86400000L * 5,
            status = ClientStatus.PENDING
        ),
        Client(
            id = "3",
            name = "Thomas Klein",
            email = "tklein@greenhouse.de",
            phone = "+49 170 1234567",
            province = "Bavaria",
            country = "Germany",
            location = "Hauptstraße 45",
            createdAt = currentTime - 86400000L * 90,
            updatedAt = currentTime - 86400000L * 15,
            status = ClientStatus.INACTIVE
        ),
        Client(
            id = "4",
            name = "Maria Muller",
            email = "maria@eco-grow.at",
            phone = "+43 660 1234567",
            province = "Vienna",
            country = "Austria",
            location = "Mariahilfer Str. 88",
            createdAt = currentTime - 86400000L * 45,
            updatedAt = currentTime - 86400000L * 2,
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "5",
            name = "Ahmed Salah",
            email = "ahmed@nileagri.eg",
            phone = "+20 100 123 4567",
            province = "Cairo Governorate",
            country = "Egypt",
            location = "Tahrir Square 10",
            createdAt = currentTime - 86400000L * 120,
            updatedAt = currentTime - 86400000L * 10,
            status = ClientStatus.PENDING
        ),
        Client(
            id = "6",
            name = "Sofia Bianchi",
            email = "sofia@italgreen.it",
            phone = "+39 333 1234567",
            province = "Lombardy",
            country = "Italy",
            location = "Via Roma 55",
            createdAt = currentTime - 86400000L * 75,
            updatedAt = currentTime - 86400000L * 3,
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "7",
            name = "Carlos Mendez",
            email = "carlos@agritech.mx",
            phone = "+52 33 1234 5678",
            province = "Jalisco",
            country = "Mexico",
            location = "Av. Vallarta 2020",
            createdAt = currentTime - 86400000L * 200,
            updatedAt = currentTime - 86400000L * 7,
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "8",
            name = "Anna Kowalski",
            email = "anna@greenpl.pl",
            phone = "+48 600 123 456",
            province = "Masovian",
            country = "Poland",
            location = "ul. Marszałkowska 100",
            createdAt = currentTime - 86400000L * 150,
            updatedAt = currentTime - 86400000L * 20,
            status = ClientStatus.PENDING
        ),
        Client(
            id = "9",
            name = "David Smith",
            email = "david@ukfarms.co.uk",
            phone = "+44 7700 900123",
            province = "Greater London",
            country = "United Kingdom",
            location = "10 Downing Gardens",
            createdAt = currentTime - 86400000L * 365,
            updatedAt = currentTime - 86400000L * 1,
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "10",
            name = "Yuki Tanaka",
            email = "yuki@japangrow.jp",
            phone = "+81 90 1234 5678",
            province = "Tokyo",
            country = "Japan",
            location = "1-1-1 Shibuya",
            createdAt = currentTime - 86400000L * 180,
            updatedAt = currentTime,
            status = ClientStatus.ACTIVE
        )
    )

    override suspend fun getClients(): Result<List<Client>> = runCatching {
        delay(600)
        mockClients
    }

    override suspend fun getClientById(id: String): Result<Client> = runCatching {
        delay(300)
        mockClients.find { it.id == id }
            ?: throw NoSuchElementException("Client with id $id not found")
    }

    override suspend fun getProvinces(): Result<List<String>> = runCatching {
        delay(200)
        mockClients.map { it.province }.distinct().sorted()
    }

    override suspend fun getCountries(): Result<List<String>> = runCatching {
        delay(200)
        mockClients.map { it.country }.distinct().sorted()
    }

    override suspend fun deleteClient(id: String): Result<Unit> = runCatching {
        delay(400)
        mockClients = mockClients.filter { it.id != id }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createClient(client: Client): Result<Client> = runCatching {
        delay(800)
        val newClient = client.copy(
            id = Uuid.random().toString(),
            createdAt = currentTime,
            updatedAt = currentTime
        )
        mockClients = mockClients + newClient
        newClient
    }

    override suspend fun updateClient(client: Client): Result<Client> = runCatching {
        delay(500)
        val index = mockClients.indexOfFirst { it.id == client.id }
        if (index == -1) {
            throw NoSuchElementException("Client with id ${client.id} not found")
        }
        val updatedClient = client.copy(updatedAt = currentTime)
        mockClients = mockClients.toMutableList().apply {
            set(index, updatedClient)
        }
        updatedClient
    }
}
