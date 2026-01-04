package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.GreenhouseCreateRequest
import com.apptolast.greenhouse.admin.data.model.GreenhouseUpdateRequest
import com.apptolast.greenhouse.admin.data.model.Location
import com.apptolast.greenhouse.admin.data.model.toGreenhouse
import com.apptolast.greenhouse.admin.data.remote.api.GreenhousesApiService
import com.apptolast.greenhouse.admin.domain.repository.GreenhousesRepository

/**
 * Implementation of GreenhousesRepository that communicates with the backend API.
 */
class GreenhousesRepositoryImpl(
    private val greenhousesApi: GreenhousesApiService
) : GreenhousesRepository {

    override suspend fun getGreenhousesByTenantId(tenantId: String): Result<List<Greenhouse>> = runCatching {
        greenhousesApi.getGreenhousesByTenantId(tenantId).map { it.toGreenhouse() }
    }

    override suspend fun createGreenhouse(
        tenantId: String,
        name: String,
        location: Location?,
        areaM2: Double?,
        timezone: String?,
        isActive: Boolean
    ): Result<Greenhouse> = runCatching {
        val request = GreenhouseCreateRequest(
            name = name,
            location = location,
            areaM2 = areaM2,
            timezone = timezone,
            isActive = isActive
        )
        greenhousesApi.createGreenhouse(tenantId, request).toGreenhouse()
    }

    override suspend fun updateGreenhouse(
        tenantId: String,
        greenhouseId: String,
        name: String?,
        location: Location?,
        areaM2: Double?,
        timezone: String?,
        isActive: Boolean?
    ): Result<Greenhouse> = runCatching {
        val request = GreenhouseUpdateRequest(
            name = name,
            location = location,
            areaM2 = areaM2,
            timezone = timezone,
            isActive = isActive
        )
        greenhousesApi.updateGreenhouse(tenantId, greenhouseId, request).toGreenhouse()
    }

    override suspend fun deleteGreenhouse(tenantId: String, greenhouseId: String): Result<Unit> = runCatching {
        greenhousesApi.deleteGreenhouse(tenantId, greenhouseId)
    }
}
