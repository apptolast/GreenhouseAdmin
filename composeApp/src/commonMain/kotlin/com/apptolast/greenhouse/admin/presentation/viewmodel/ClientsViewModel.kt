package com.apptolast.greenhouse.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.data.model.ClientStatusFilter
import com.apptolast.greenhouse.admin.data.model.PaginationInfo
import com.apptolast.greenhouse.admin.domain.repository.ClientsRepository
import com.apptolast.greenhouse.admin.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Clients screen following MVVM+MVI pattern.
 */
class ClientsViewModel(
    private val repository: ClientsRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientsUiState())

    /**
     * Exposes immutable StateFlow for UI consumption.
     */
    val uiState: StateFlow<ClientsUiState> = _uiState.asStateFlow()

    init {
        loadLayoutData()
        onEvent(ClientsEvent.LoadClients)
    }

    private fun loadLayoutData() {
        viewModelScope.launch {
            val menuItemsResult = dashboardRepository.getMenuItems()
            val alertCountResult = dashboardRepository.getAlertCount()

            _uiState.update { currentState ->
                currentState.copy(
                    menuItems = menuItemsResult.getOrDefault(emptyList()),
                    alertCount = alertCountResult.getOrDefault(0)
                )
            }
        }
    }

    /**
     * Handles all UI events following MVI pattern.
     * Single entry point for all user interactions.
     */
    fun onEvent(event: ClientsEvent) {
        when (event) {
            is ClientsEvent.LoadClients -> loadClients()
            is ClientsEvent.RefreshClients -> refreshClients()
            is ClientsEvent.OnSearchQueryChanged -> updateSearchQuery(event.query)
            is ClientsEvent.OnStatusFilterChanged -> updateStatusFilter(event.filter)
            is ClientsEvent.OnProvinceFilterChanged -> updateProvinceFilter(event.province)
            is ClientsEvent.OnClientClicked -> handleClientClick(event.client)
            is ClientsEvent.OnEditClientClicked -> handleEditClient(event.client)
            is ClientsEvent.OnDeleteClientClicked -> showDeleteConfirmation(event.client)
            is ClientsEvent.OnConfirmDelete -> confirmDelete()
            is ClientsEvent.OnCancelDelete -> cancelDelete()
            is ClientsEvent.OnNewClientClicked -> showNewClientDialog()
            is ClientsEvent.OnDismissNewClientDialog -> dismissNewClientDialog()
            is ClientsEvent.OnSubmitNewClient -> submitNewClient(
                name = event.name,
                email = event.email,
                phone = event.phone,
                province = event.province,
                country = event.country,
                location = event.location,
                status = event.status
            )

            is ClientsEvent.OnDismissEditClientDialog -> dismissEditClientDialog()
            is ClientsEvent.OnSubmitEditClient -> submitEditClient(
                id = event.id,
                name = event.name,
                email = event.email,
                phone = event.phone,
                province = event.province,
                country = event.country,
                location = event.location,
                status = event.status
            )
            is ClientsEvent.OnClientSelectionToggled -> toggleClientSelection(event.clientId)
            is ClientsEvent.OnSelectAllToggled -> toggleSelectAll()
            is ClientsEvent.OnPageChanged -> changePage(event.page)
            is ClientsEvent.OnPageSizeChanged -> changePageSize(event.size)
            is ClientsEvent.DismissError -> dismissError()
            is ClientsEvent.OnMenuItemSelected -> updateSelectedMenu(event.itemId)
            is ClientsEvent.OnTopBarSearchQueryChanged -> updateTopBarSearchQuery(event.query)
            is ClientsEvent.OnAlertIconClicked -> handleAlertClick()
        }
    }

    private fun updateSelectedMenu(itemId: String) {
        _uiState.update { it.copy(selectedMenuId = itemId) }
    }

    private fun updateTopBarSearchQuery(query: String) {
        _uiState.update { it.copy(topBarSearchQuery = query) }
    }

    private fun handleAlertClick() {
        // Handle alert click - could navigate to alerts screen
    }

    private fun loadClients() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val clientsResult = repository.getClients()
            val provincesResult = repository.getProvinces()
            val countriesResult = repository.getCountries()

            _uiState.update { currentState ->
                val clients = clientsResult.getOrDefault(emptyList())
                currentState.copy(
                    isLoading = false,
                    clients = clients,
                    provinces = provincesResult.getOrDefault(emptyList()),
                    countries = countriesResult.getOrDefault(emptyList()),
                    pagination = currentState.pagination.copy(totalItems = clients.size),
                    error = clientsResult.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun refreshClients() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            repository.getClients()
                .onSuccess { clients ->
                    _uiState.update {
                        it.copy(
                            clients = clients,
                            isRefreshing = false,
                            error = null,
                            pagination = it.pagination.copy(totalItems = clients.size)
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isRefreshing = false) }
                }
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                pagination = it.pagination.copy(currentPage = 0) // Reset to first page
            )
        }
    }

    private fun updateStatusFilter(filter: ClientStatusFilter) {
        _uiState.update {
            it.copy(
                statusFilter = filter,
                pagination = it.pagination.copy(currentPage = 0)
            )
        }
    }

    private fun updateProvinceFilter(province: String?) {
        _uiState.update {
            it.copy(
                provinceFilter = province,
                pagination = it.pagination.copy(currentPage = 0)
            )
        }
    }

    private fun handleClientClick(client: Client) {
        // Navigate to client detail - handled by UI layer
    }

    private fun handleEditClient(client: Client) {
        _uiState.update {
            it.copy(
                showEditClientDialog = true,
                clientToEdit = client,
                updateClientError = null
            )
        }
    }

    private fun showDeleteConfirmation(client: Client) {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = true,
                clientToDelete = client
            )
        }
    }

    private fun confirmDelete() {
        val clientToDelete = _uiState.value.clientToDelete ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDeletingClient = true,
                    deleteClientError = null
                )
            }

            repository.deleteClient(clientToDelete.id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            clients = state.clients.filter { it.id != clientToDelete.id },
                            showDeleteConfirmation = false,
                            clientToDelete = null,
                            isDeletingClient = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeletingClient = false,
                            deleteClientError = error.message
                        )
                    }
                }
        }
    }

    private fun cancelDelete() {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = false,
                clientToDelete = null,
                isDeletingClient = false,
                deleteClientError = null
            )
        }
    }

    private fun showNewClientDialog() {
        _uiState.update {
            it.copy(
                showNewClientDialog = true,
                createClientError = null
            )
        }
    }

    private fun dismissNewClientDialog() {
        _uiState.update {
            it.copy(
                showNewClientDialog = false,
                isCreatingClient = false,
                createClientError = null
            )
        }
    }

    private fun submitNewClient(
        name: String,
        email: String,
        phone: String,
        province: String,
        country: String,
        location: String,
        status: ClientStatus
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingClient = true, createClientError = null) }

            val newClient = Client(
                id = "", // Will be generated by repository
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                province = province,
                country = country,
                location = location.trim(),
                createdAt = 0L, // Will be set by repository
                updatedAt = 0L, // Will be set by repository
                status = status
            )

            repository.createClient(newClient)
                .onSuccess { createdClient ->
                    _uiState.update { state ->
                        val updatedClients = state.clients + createdClient
                        val updatedProvinces = if (createdClient.province !in state.provinces) {
                            (state.provinces + createdClient.province).sorted()
                        } else {
                            state.provinces
                        }
                        val updatedCountries = if (createdClient.country !in state.countries) {
                            (state.countries + createdClient.country).sorted()
                        } else {
                            state.countries
                        }
                        state.copy(
                            clients = updatedClients,
                            provinces = updatedProvinces,
                            countries = updatedCountries,
                            showNewClientDialog = false,
                            isCreatingClient = false,
                            pagination = state.pagination.copy(totalItems = updatedClients.size)
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isCreatingClient = false,
                            createClientError = error.message
                        )
                    }
                }
        }
    }

    private fun dismissEditClientDialog() {
        _uiState.update {
            it.copy(
                showEditClientDialog = false,
                clientToEdit = null,
                isUpdatingClient = false,
                updateClientError = null
            )
        }
    }

    private fun submitEditClient(
        id: String,
        name: String,
        email: String,
        phone: String,
        province: String,
        country: String,
        location: String,
        status: ClientStatus
    ) {
        val existingClient = _uiState.value.clientToEdit ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingClient = true, updateClientError = null) }

            val updatedClient = existingClient.copy(
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                province = province,
                country = country,
                location = location.trim(),
                status = status
            )

            repository.updateClient(updatedClient)
                .onSuccess { resultClient ->
                    _uiState.update { state ->
                        val updatedClients = state.clients.map {
                            if (it.id == resultClient.id) resultClient else it
                        }
                        val updatedProvinces = if (resultClient.province !in state.provinces) {
                            (state.provinces + resultClient.province).sorted()
                        } else {
                            state.provinces
                        }
                        val updatedCountries = if (resultClient.country !in state.countries) {
                            (state.countries + resultClient.country).sorted()
                        } else {
                            state.countries
                        }
                        state.copy(
                            clients = updatedClients,
                            provinces = updatedProvinces,
                            countries = updatedCountries,
                            showEditClientDialog = false,
                            clientToEdit = null,
                            isUpdatingClient = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isUpdatingClient = false,
                            updateClientError = error.message
                        )
                    }
                }
        }
    }

    private fun toggleClientSelection(clientId: String) {
        _uiState.update { state ->
            val newSelection = if (clientId in state.selectedClientIds) {
                state.selectedClientIds - clientId
            } else {
                state.selectedClientIds + clientId
            }
            state.copy(selectedClientIds = newSelection)
        }
    }

    private fun toggleSelectAll() {
        _uiState.update { state ->
            val visibleIds = state.paginatedClients.map { it.id }.toSet()
            val newSelection = if (state.allSelected) {
                state.selectedClientIds - visibleIds
            } else {
                state.selectedClientIds + visibleIds
            }
            state.copy(selectedClientIds = newSelection)
        }
    }

    private fun changePage(page: Int) {
        _uiState.update {
            it.copy(pagination = it.pagination.copy(currentPage = page))
        }
    }

    private fun changePageSize(size: Int) {
        _uiState.update {
            it.copy(
                pagination = PaginationInfo(
                    currentPage = 0,
                    pageSize = size,
                    totalItems = it.filteredClients.size
                )
            )
        }
    }

    private fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
