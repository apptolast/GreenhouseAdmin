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

    override suspend fun getSectorsByTenantId(tenantId: Long): Result<List<Sector>> = runCatching {
        sectorsApi.getSectorsByTenantId(tenantId).map { it.toSector() }
    }

    override suspend fun createSector(
        tenantId: Long,
        greenhouseId: Long,
        name: String?
    ): Result<Sector> = runCatching {
        val request = SectorCreateRequest(
            greenhouseId = greenhouseId,
            name = name
        )
        sectorsApi.createSector(tenantId, request).toSector()
    }

    override suspend fun updateSector(
        tenantId: Long,
        sectorId: Long,
        greenhouseId: Long?,
        name: String?
    ): Result<Sector> = runCatching {
        val request = SectorUpdateRequest(
            greenhouseId = greenhouseId,
            name = name
        )
        sectorsApi.updateSector(tenantId, sectorId, request).toSector()
    }

    override suspend fun deleteSector(tenantId: Long, sectorId: Long): Result<Unit> = runCatching {
        sectorsApi.deleteSector(tenantId, sectorId)
    }
}
