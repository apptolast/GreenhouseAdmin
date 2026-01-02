package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.domain.repository.SectorsRepository
import kotlinx.coroutines.delay
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Mock implementation of SectorsRepository.
 * Provides hardcoded data for development. Replace with real API calls later.
 */
class SectorsRepositoryImpl : SectorsRepository {

    private val mockSectors = mutableListOf(
        // Sectors for client "1" (Elena Rodriguez)
        Sector(
            id = "s1",
            name = "Sector Norte A",
            greenhouseId = "gh1",
            greenhouseName = "Invernadero Principal",
            area = 150.0,
            clientId = "1"
        ),
        Sector(
            id = "s2",
            name = "Sector Norte B",
            greenhouseId = "gh1",
            greenhouseName = "Invernadero Principal",
            area = 120.5,
            clientId = "1"
        ),
        Sector(
            id = "s3",
            name = "Sector Sur",
            greenhouseId = "gh1",
            greenhouseName = "Invernadero Principal",
            area = 200.0,
            clientId = "1"
        ),
        Sector(
            id = "s4",
            name = "Hidroponia A",
            greenhouseId = "gh2",
            greenhouseName = "Invernadero Norte",
            area = 80.0,
            clientId = "1"
        ),
        // Sectors for client "2" (Jean Pierre)
        Sector(
            id = "s5",
            name = "Section Principale",
            greenhouseId = "gh4",
            greenhouseName = "Serre Principale",
            area = 250.0,
            clientId = "2"
        ),
        // Sectors for client "4" (Maria Muller)
        Sector(
            id = "s6",
            name = "Bereich 1",
            greenhouseId = "gh5",
            greenhouseName = "Gewächshaus A",
            area = 175.5,
            clientId = "4"
        ),
        Sector(
            id = "s7",
            name = "Bereich 2",
            greenhouseId = "gh6",
            greenhouseName = "Gewächshaus B",
            area = 90.0,
            clientId = "4"
        )
    )

    override suspend fun getSectorsByClientId(clientId: String): Result<List<Sector>> = runCatching {
        delay(400)
        mockSectors.filter { it.clientId == clientId }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createSector(sector: Sector): Result<Sector> = runCatching {
        delay(600)
        val newSector = sector.copy(id = Uuid.random().toString())
        mockSectors.add(newSector)
        newSector
    }

    override suspend fun updateSector(sector: Sector): Result<Sector> = runCatching {
        delay(500)
        val index = mockSectors.indexOfFirst { it.id == sector.id }
        if (index == -1) {
            throw NoSuchElementException("Sector with id ${sector.id} not found")
        }
        mockSectors[index] = sector
        sector
    }

    override suspend fun deleteSector(id: String): Result<Unit> = runCatching {
        delay(400)
        val index = mockSectors.indexOfFirst { it.id == id }
        if (index == -1) {
            throw NoSuchElementException("Sector with id $id not found")
        }
        mockSectors.removeAt(index)
    }
}
