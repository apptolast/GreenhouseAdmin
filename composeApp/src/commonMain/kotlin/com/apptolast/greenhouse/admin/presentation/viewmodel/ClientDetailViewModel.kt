package com.apptolast.greenhouse.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.data.model.User
import com.apptolast.greenhouse.admin.domain.repository.ClientsRepository
import com.apptolast.greenhouse.admin.domain.repository.DashboardRepository
import com.apptolast.greenhouse.admin.domain.repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Client Detail screen following MVVM+MVI pattern.
 */
class ClientDetailViewModel(
    private val clientId: String,
    private val clientsRepository: ClientsRepository,
    private val dashboardRepository: DashboardRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientDetailUiState())

    /**
     * Exposes immutable StateFlow for UI consumption.
     */
    val uiState: StateFlow<ClientDetailUiState> = _uiState.asStateFlow()

    init {
        loadLayoutData()
        loadClient()
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
    fun onEvent(event: ClientDetailEvent) {
        when (event) {
            is ClientDetailEvent.LoadClient -> loadClient()
            is ClientDetailEvent.OnTabSelected -> selectTab(event.tab)
            is ClientDetailEvent.OnBackClicked -> { /* Handled by UI layer */
            }

            is ClientDetailEvent.OnEditClicked -> showEditDialog()
            is ClientDetailEvent.OnDeleteClicked -> showDeleteConfirmation()
            is ClientDetailEvent.OnConfirmDelete -> confirmDelete()
            is ClientDetailEvent.OnCancelDelete -> cancelDelete()
            is ClientDetailEvent.OnDismissEditDialog -> dismissEditDialog()
            is ClientDetailEvent.OnSubmitEdit -> submitEdit(
                name = event.name,
                email = event.email,
                phone = event.phone,
                province = event.province,
                country = event.country,
                location = event.location,
                status = event.status
            )

            is ClientDetailEvent.OnMenuItemSelected -> updateSelectedMenu(event.itemId)
            is ClientDetailEvent.OnTopBarSearchQueryChanged -> updateTopBarSearchQuery(event.query)
            is ClientDetailEvent.OnAlertIconClicked -> handleAlertClick()
            is ClientDetailEvent.OnNavigationHandled -> resetNavigationFlag()

            // Users events
            is ClientDetailEvent.LoadUsers -> loadUsers()
            is ClientDetailEvent.OnAddUserClicked -> showUserFormDialog(UserFormMode.Create)
            is ClientDetailEvent.OnEditUserClicked -> showUserFormDialog(UserFormMode.Edit(event.user))
            is ClientDetailEvent.OnDeleteUserClicked -> showDeleteUserConfirmation(event.user)
            is ClientDetailEvent.OnConfirmDeleteUser -> confirmDeleteUser()
            is ClientDetailEvent.OnCancelDeleteUser -> cancelDeleteUser()
            is ClientDetailEvent.OnDismissUserFormDialog -> dismissUserFormDialog()
            is ClientDetailEvent.OnSubmitUserForm -> submitUserForm(event.name, event.email, event.phone)
        }
    }

    private fun loadClient() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            clientsRepository.getClientById(clientId)
                .onSuccess { client ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            client = client,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    private fun selectTab(tab: ClientDetailTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        // Load users when USERS tab is selected and not already loaded
        if (tab == ClientDetailTab.USERS && _uiState.value.users.isEmpty() && !_uiState.value.isLoadingUsers) {
            loadUsers()
        }
    }

    private fun showEditDialog() {
        _uiState.update {
            it.copy(
                showEditClientDialog = true,
                updateClientError = null
            )
        }
    }

    private fun dismissEditDialog() {
        _uiState.update {
            it.copy(
                showEditClientDialog = false,
                isUpdatingClient = false,
                updateClientError = null
            )
        }
    }

    private fun submitEdit(
        name: String,
        email: String,
        phone: String,
        province: String,
        country: String,
        location: String,
        status: ClientStatus
    ) {
        val existingClient = _uiState.value.client ?: return

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

            clientsRepository.updateClient(updatedClient)
                .onSuccess { resultClient ->
                    _uiState.update {
                        it.copy(
                            client = resultClient,
                            showEditClientDialog = false,
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

    private fun showDeleteConfirmation() {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = true,
                deleteClientError = null
            )
        }
    }

    private fun cancelDelete() {
        _uiState.update {
            it.copy(
                showDeleteConfirmation = false,
                isDeletingClient = false,
                deleteClientError = null
            )
        }
    }

    private fun confirmDelete() {
        val client = _uiState.value.client ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDeletingClient = true,
                    deleteClientError = null
                )
            }

            clientsRepository.deleteClient(client.id)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            showDeleteConfirmation = false,
                            isDeletingClient = false,
                            shouldNavigateBack = true
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

    private fun resetNavigationFlag() {
        _uiState.update { it.copy(shouldNavigateBack = false) }
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

    // === Users Tab Methods ===

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUsers = true, usersError = null) }

            usersRepository.getUsersByClientId(clientId)
                .onSuccess { users ->
                    _uiState.update {
                        it.copy(
                            isLoadingUsers = false,
                            users = users
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingUsers = false,
                            usersError = error.message
                        )
                    }
                }
        }
    }

    private fun showUserFormDialog(mode: UserFormMode) {
        _uiState.update {
            it.copy(
                showUserFormDialog = true,
                userFormMode = mode,
                submitUserError = null
            )
        }
    }

    private fun dismissUserFormDialog() {
        _uiState.update {
            it.copy(
                showUserFormDialog = false,
                isSubmittingUser = false,
                submitUserError = null
            )
        }
    }

    private fun submitUserForm(name: String, email: String, phone: String) {
        val mode = _uiState.value.userFormMode

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingUser = true, submitUserError = null) }

            val result = when (mode) {
                is UserFormMode.Create -> {
                    val newUser = User(
                        id = "",
                        name = name.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        clientId = clientId
                    )
                    usersRepository.createUser(newUser)
                }

                is UserFormMode.Edit -> {
                    val updatedUser = mode.user.copy(
                        name = name.trim(),
                        email = email.trim(),
                        phone = phone.trim()
                    )
                    usersRepository.updateUser(updatedUser)
                }
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            showUserFormDialog = false,
                            isSubmittingUser = false
                        )
                    }
                    loadUsers() // Reload users from repository to ensure sync
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingUser = false,
                            submitUserError = error.message
                        )
                    }
                }
        }
    }

    private fun showDeleteUserConfirmation(user: User) {
        _uiState.update {
            it.copy(
                showDeleteUserConfirmation = true,
                userToDelete = user,
                deleteUserError = null
            )
        }
    }

    private fun cancelDeleteUser() {
        _uiState.update {
            it.copy(
                showDeleteUserConfirmation = false,
                userToDelete = null,
                deleteUserError = null
            )
        }
    }

    private fun confirmDeleteUser() {
        val user = _uiState.value.userToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingUser = true, deleteUserError = null) }

            usersRepository.deleteUser(user.id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            users = state.users.filter { it.id != user.id },
                            showDeleteUserConfirmation = false,
                            userToDelete = null,
                            isDeletingUser = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeletingUser = false,
                            deleteUserError = error.message
                        )
                    }
                }
        }
    }
}
