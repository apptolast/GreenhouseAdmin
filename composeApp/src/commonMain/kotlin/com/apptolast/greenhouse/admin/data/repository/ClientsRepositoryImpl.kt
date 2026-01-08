package com.apptolast.greenhouse.admin.data.repository

import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.toClient
import com.apptolast.greenhouse.admin.data.model.toCreateRequest
import com.apptolast.greenhouse.admin.data.model.toUpdateRequest
import com.apptolast.greenhouse.admin.data.remote.api.TenantsApiService
import com.apptolast.greenhouse.admin.domain.repository.ClientsRepository

/**
 * Implementation of ClientsRepository using the Tenants API.
 * Communicates with InvernaderosAPI to manage tenant/client data.
 */
class ClientsRepositoryImpl(
    private val tenantsApi: TenantsApiService
) : ClientsRepository {

    override suspend fun getClients(): Result<List<Client>> = runCatching {
        tenantsApi.getAllTenants().map { it.toClient() }
    }

    override suspend fun getClientById(id: Long): Result<Client> = runCatching {
        tenantsApi.getTenantById(id).toClient()
    }

    override suspend fun createClient(client: Client): Result<Client> = runCatching {
        val request = client.toCreateRequest()
        tenantsApi.createTenant(request).toClient()
    }

    override suspend fun updateClient(client: Client): Result<Client> = runCatching {
        val request = client.toUpdateRequest()
        tenantsApi.updateTenant(client.id, request).toClient()
    }

    override suspend fun deleteClient(id: Long): Result<Unit> = runCatching {
        tenantsApi.deleteTenant(id)
    }
}
