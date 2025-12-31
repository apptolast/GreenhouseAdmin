package com.apptolast.greenhouse.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.greenhouse.admin.data.model.Client
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
            is ClientsEvent.OnLocationFilterChanged -> updateLocationFilter(event.location)
            is ClientsEvent.OnClientClicked -> handleClientClick(event.client)
            is ClientsEvent.OnEditClientClicked -> handleEditClient(event.client)
            is ClientsEvent.OnDeleteClientClicked -> showDeleteConfirmation(event.client)
            is ClientsEvent.OnConfirmDelete -> confirmDelete()
            is ClientsEvent.OnCancelDelete -> cancelDelete()
            is ClientsEvent.OnNewClientClicked -> handleNewClient()
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
            val locationsResult = repository.getLocations()

            _uiState.update { currentState ->
                val clients = clientsResult.getOrDefault(emptyList())
                currentState.copy(
                    isLoading = false,
                    clients = clients,
                    locations = locationsResult.getOrDefault(emptyList()),
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

    private fun updateLocationFilter(location: String?) {
        _uiState.update {
            it.copy(
                locationFilter = location,
                pagination = it.pagination.copy(currentPage = 0)
            )
        }
    }

    private fun handleClientClick(client: Client) {
        // Navigate to client detail - handled by UI layer
    }

    private fun handleEditClient(client: Client) {
        // Navigate to edit client - handled by UI layer
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
            _uiState.update { it.copy(showDeleteConfirmation = false) }

            repository.deleteClient(clientToDelete.id)
                .onSuccess {
                    // Remove from local list (in real app, would refetch)
                    _uiState.update { state ->
                        state.copy(
                            clients = state.clients.filter { it.id != clientToDelete.id },
                            clientToDelete = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            error = error.message,
                            clientToDelete = null
                        )
                    }
                }
        }
    }

    private fun cancelDelete() {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = false,
                clientToDelete = null
            )
        }
    }

    private fun handleNewClient() {
        // Navigate to create client - handled by UI layer
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
