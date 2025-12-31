package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.domain.repository.ClientsRepository
import kotlinx.coroutines.delay

/**
 * Mock implementation of ClientsRepository.
 * Provides hardcoded data for development. Replace with real API calls later.
 */
class ClientsRepositoryImpl : ClientsRepository {

    private val mockClients = listOf(
        Client(
            id = "1",
            firstName = "Elena",
            lastName = "Rodriguez",
            email = "elena@freshveg.com",
            company = "FreshVeg Distributors",
            city = "Almeria",
            country = "Spain",
            greenhouseCount = 12,
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "2",
            firstName = "Jean",
            lastName = "Pierre",
            email = "j.pierre@organicfarms.fr",
            company = "Organic Farms Ltd.",
            city = "Nantes",
            country = "France",
            greenhouseCount = 4,
            status = ClientStatus.PENDING
        ),
        Client(
            id = "3",
            firstName = "Thomas",
            lastName = "Klein",
            email = "tklein@greenhouse.de",
            company = "Bavaria Grow",
            city = "Munich",
            country = "Germany",
            greenhouseCount = 28,
            status = ClientStatus.INACTIVE
        ),
        Client(
            id = "4",
            firstName = "Maria",
            lastName = "Muller",
            email = "maria@eco-grow.at",
            company = "EcoGrow Austria",
            city = "Vienna",
            country = "Austria",
            greenhouseCount = 8,
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "5",
            firstName = "Ahmed",
            lastName = "Salah",
            email = "ahmed@nileagri.eg",
            company = "Nile AgriCorp",
            city = "Cairo",
            country = "Egypt",
            greenhouseCount = 54,
            status = ClientStatus.PENDING
        ),
        Client(
            id = "6",
            firstName = "Sofia",
            lastName = "Bianchi",
            email = "sofia@italgreen.it",
            company = "ItalGreen S.r.l.",
            city = "Milan",
            country = "Italy",
            greenhouseCount = 15,
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "7",
            firstName = "Carlos",
            lastName = "Mendez",
            email = "carlos@agritech.mx",
            company = "AgriTech Mexico",
            city = "Guadalajara",
            country = "Mexico",
            greenhouseCount = 32,
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "8",
            firstName = "Anna",
            lastName = "Kowalski",
            email = "anna@greenpl.pl",
            company = "GreenPL",
            city = "Warsaw",
            country = "Poland",
            greenhouseCount = 7,
            status = ClientStatus.PENDING
        ),
        Client(
            id = "9",
            firstName = "David",
            lastName = "Smith",
            email = "david@ukfarms.co.uk",
            company = "UK Farms Ltd",
            city = "London",
            country = "United Kingdom",
            greenhouseCount = 19,
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "10",
            firstName = "Yuki",
            lastName = "Tanaka",
            email = "yuki@japangrow.jp",
            company = "Japan Grow Inc.",
            city = "Tokyo",
            country = "Japan",
            greenhouseCount = 45,
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

    override suspend fun getLocations(): Result<List<String>> = runCatching {
        delay(200)
        mockClients.map { it.country }.distinct().sorted()
    }

    override suspend fun deleteClient(id: String): Result<Unit> = runCatching {
        delay(400)
        // In mock, we just simulate success
        // In real implementation, this would call the API
    }
}
