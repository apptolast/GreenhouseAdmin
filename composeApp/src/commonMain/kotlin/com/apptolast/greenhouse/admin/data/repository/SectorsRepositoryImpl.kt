package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.data.model.SectorCreateRequest
import com.apptolast.greenhouse.admin.data.model.SectorUpdateRequest
import com.apptolast.greenhouse.admin.data.model.toSector
import com.apptolast.greenhouse.admin.data.remote.api.SectorsApiService
import com.apptolast.greenhouse.admin.domain.repository.SectorsRepository

/**
 * Implementation of SectorsRepository that communicates with the backend API.
 */
class SectorsRepositoryImpl(
    private val sectorsApi: SectorsApiService
) : SectorsRepository {

    override suspend fun getSectorsByTenantId(tenantId: String): Result<List<Sector>> = runCatching {
        sectorsApi.getSectorsByTenantId(tenantId).map { it.toSector() }
    }

    override suspend fun createSector(
        tenantId: String,
        greenhouseId: String,
        variety: String?
    ): Result<Sector> = runCatching {
        val request = SectorCreateRequest(
            greenhouseId = greenhouseId,
            variety = variety
        )
        sectorsApi.createSector(tenantId, request).toSector()
    }

    override suspend fun updateSector(
        tenantId: String,
        sectorId: String,
        variety: String?
    ): Result<Sector> = runCatching {
        val request = SectorUpdateRequest(
            variety = variety
        )
        sectorsApi.updateSector(tenantId, sectorId, request).toSector()
    }

    override suspend fun deleteSector(tenantId: String, sectorId: String): Result<Unit> = runCatching {
        sectorsApi.deleteSector(tenantId, sectorId)
    }
}
