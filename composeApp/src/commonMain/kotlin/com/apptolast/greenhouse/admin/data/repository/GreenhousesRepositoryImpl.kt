package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.GreenhouseStatus
import com.apptolast.greenhouse.admin.domain.repository.GreenhousesRepository
import kotlinx.coroutines.delay
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Mock implementation of GreenhousesRepository.
 * Provides hardcoded data for development. Replace with real API calls later.
 */
class GreenhousesRepositoryImpl : GreenhousesRepository {

    private val mockGreenhouses = mutableListOf(
        // Greenhouses for client "1" (Elena Rodriguez)
        Greenhouse(
            id = "gh1",
            name = "Invernadero Principal",
            description = "Producción de tomates y pimientos",
            status = GreenhouseStatus.ACTIVE,
            clientId = "1"
        ),
        Greenhouse(
            id = "gh2",
            name = "Invernadero Norte",
            description = "Cultivo de lechugas hidropónicas",
            status = GreenhouseStatus.ACTIVE,
            clientId = "1"
        ),
        Greenhouse(
            id = "gh3",
            name = "Invernadero Experimental",
            description = "Pruebas de nuevas variedades",
            status = GreenhouseStatus.INACTIVE,
            clientId = "1"
        ),
        // Greenhouses for client "2" (Jean Pierre)
        Greenhouse(
            id = "gh4",
            name = "Serre Principale",
            description = "Production biologique",
            status = GreenhouseStatus.ACTIVE,
            clientId = "2"
        ),
        // Greenhouses for client "4" (Maria Muller)
        Greenhouse(
            id = "gh5",
            name = "Gewächshaus A",
            description = "Gemüseanbau",
            status = GreenhouseStatus.ACTIVE,
            clientId = "4"
        ),
        Greenhouse(
            id = "gh6",
            name = "Gewächshaus B",
            description = "Kräuterproduktion",
            status = GreenhouseStatus.ACTIVE,
            clientId = "4"
        )
    )

    override suspend fun getGreenhousesByClientId(clientId: String): Result<List<Greenhouse>> = runCatching {
        delay(400)
        mockGreenhouses.filter { it.clientId == clientId }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createGreenhouse(greenhouse: Greenhouse): Result<Greenhouse> = runCatching {
        delay(600)
        val newGreenhouse = greenhouse.copy(id = Uuid.random().toString())
        mockGreenhouses.add(newGreenhouse)
        newGreenhouse
    }

    override suspend fun updateGreenhouse(greenhouse: Greenhouse): Result<Greenhouse> = runCatching {
        delay(500)
        val index = mockGreenhouses.indexOfFirst { it.id == greenhouse.id }
        if (index == -1) {
            throw NoSuchElementException("Greenhouse with id ${greenhouse.id} not found")
        }
        mockGreenhouses[index] = greenhouse
        greenhouse
    }

    override suspend fun deleteGreenhouse(id: String): Result<Unit> = runCatching {
        delay(400)
        val index = mockGreenhouses.indexOfFirst { it.id == id }
        if (index == -1) {
            throw NoSuchElementException("Greenhouse with id $id not found")
        }
        mockGreenhouses.removeAt(index)
    }
}
