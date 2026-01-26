package com.apptolast.greenhouse.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.AlertCreateRequest
import com.apptolast.greenhouse.admin.data.model.AlertUpdateRequest
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Location
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.data.model.SettingCreateRequest
import com.apptolast.greenhouse.admin.data.model.SettingUpdateRequest
import com.apptolast.greenhouse.admin.data.model.User
import com.apptolast.greenhouse.admin.data.model.UserRole
import com.apptolast.greenhouse.admin.data.model.toIsActive
import com.apptolast.greenhouse.admin.domain.repository.AlertsRepository
import com.apptolast.greenhouse.admin.domain.repository.ClientsRepository
import com.apptolast.greenhouse.admin.domain.repository.DevicesRepository
import com.apptolast.greenhouse.admin.domain.repository.GreenhousesRepository
import com.apptolast.greenhouse.admin.domain.repository.SectorsRepository
import com.apptolast.greenhouse.admin.domain.repository.SettingsRepository
import com.apptolast.greenhouse.admin.domain.repository.UsersRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Client Detail screen following MVVM+MVI pattern.
 */
class ClientDetailViewModel(
    private val clientId: Long,
    private val clientsRepository: ClientsRepository,
    private val usersRepository: UsersRepository,
    private val greenhousesRepository: GreenhousesRepository,
    private val sectorsRepository: SectorsRepository,
    private val devicesRepository: DevicesRepository,
    private val alertsRepository: AlertsRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientDetailUiState())

    /**
     * Exposes immutable StateFlow for UI consumption.
     */
    val uiState: StateFlow<ClientDetailUiState> = _uiState.asStateFlow()

    init {
        loadClient()
        // Preload all catalogs for form dropdowns
        loadAllCatalogs()
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
            is ClientDetailEvent.OnNavigationHandled -> resetNavigationFlag()

            // Users events
            is ClientDetailEvent.LoadUsers -> loadUsers()
            is ClientDetailEvent.OnAddUserClicked -> showUserFormDialog(UserFormMode.Create)
            is ClientDetailEvent.OnEditUserClicked -> showUserFormDialog(UserFormMode.Edit(event.user))
            is ClientDetailEvent.OnDeleteUserClicked -> showDeleteUserConfirmation(event.user)
            is ClientDetailEvent.OnConfirmDeleteUser -> confirmDeleteUser()
            is ClientDetailEvent.OnCancelDeleteUser -> cancelDeleteUser()
            is ClientDetailEvent.OnDismissUserFormDialog -> dismissUserFormDialog()
            is ClientDetailEvent.OnSubmitUserForm -> submitUserForm(
                event.username,
                event.email,
                event.password,
                event.role,
                event.isActive
            )

            // Greenhouses events
            is ClientDetailEvent.LoadGreenhouses -> loadGreenhouses()
            is ClientDetailEvent.OnAddGreenhouseClicked -> showGreenhouseFormDialog(GreenhouseFormMode.Create)
            is ClientDetailEvent.OnEditGreenhouseClicked -> showGreenhouseFormDialog(GreenhouseFormMode.Edit(event.greenhouse))
            is ClientDetailEvent.OnDeleteGreenhouseClicked -> showDeleteGreenhouseConfirmation(event.greenhouse)
            is ClientDetailEvent.OnConfirmDeleteGreenhouse -> confirmDeleteGreenhouse()
            is ClientDetailEvent.OnCancelDeleteGreenhouse -> cancelDeleteGreenhouse()
            is ClientDetailEvent.OnDismissGreenhouseFormDialog -> dismissGreenhouseFormDialog()
            is ClientDetailEvent.OnSubmitGreenhouseForm -> submitGreenhouseForm(
                event.name,
                event.location,
                event.areaM2,
                event.timezone,
                event.isActive
            )

            // Sectors events
            is ClientDetailEvent.LoadSectors -> loadSectors()
            is ClientDetailEvent.OnAddSectorClicked -> showSectorFormDialog(SectorFormMode.Create)
            is ClientDetailEvent.OnEditSectorClicked -> showSectorFormDialog(SectorFormMode.Edit(event.sector))
            is ClientDetailEvent.OnDeleteSectorClicked -> showDeleteSectorConfirmation(event.sector)
            is ClientDetailEvent.OnConfirmDeleteSector -> confirmDeleteSector()
            is ClientDetailEvent.OnCancelDeleteSector -> cancelDeleteSector()
            is ClientDetailEvent.OnDismissSectorFormDialog -> dismissSectorFormDialog()
            is ClientDetailEvent.OnSubmitSectorForm -> submitSectorForm(
                event.greenhouseId,
                event.variety
            )

            // Devices events
            is ClientDetailEvent.LoadDevices -> loadDevices()
            is ClientDetailEvent.OnAddDeviceClicked -> showDeviceFormDialog(DeviceFormMode.Create)
            is ClientDetailEvent.OnEditDeviceClicked -> showDeviceFormDialog(DeviceFormMode.Edit(event.device))
            is ClientDetailEvent.OnDeleteDeviceClicked -> showDeleteDeviceConfirmation(event.device)
            is ClientDetailEvent.OnConfirmDeleteDevice -> confirmDeleteDevice()
            is ClientDetailEvent.OnCancelDeleteDevice -> cancelDeleteDevice()
            is ClientDetailEvent.OnDismissDeviceFormDialog -> dismissDeviceFormDialog()
            is ClientDetailEvent.OnSubmitDeviceForm -> submitDeviceForm(
                event.greenhouseId,
                event.name,
                event.categoryId,
                event.typeId,
                event.unitId,
                event.isActive
            )
            // OnDeviceCategoryChanged is no longer needed - types are filtered locally in the dialog
            is ClientDetailEvent.OnDeviceCategoryChanged -> { /* No-op: types filtered locally */
            }

            // Alerts events
            is ClientDetailEvent.LoadAlerts -> loadAlerts()
            is ClientDetailEvent.OnAddAlertClicked -> showAlertFormDialog(AlertFormMode.Create)
            is ClientDetailEvent.OnEditAlertClicked -> showAlertFormDialog(AlertFormMode.Edit(event.alert))
            is ClientDetailEvent.OnDeleteAlertClicked -> showDeleteAlertConfirmation(event.alert)
            is ClientDetailEvent.OnConfirmDeleteAlert -> confirmDeleteAlert()
            is ClientDetailEvent.OnCancelDeleteAlert -> cancelDeleteAlert()
            is ClientDetailEvent.OnDismissAlertFormDialog -> dismissAlertFormDialog()
            is ClientDetailEvent.OnSubmitAlertForm -> submitAlertForm(
                event.greenhouseId,
                event.alertTypeId,
                event.severityId,
                event.message
            )

            is ClientDetailEvent.OnResolveAlertClicked -> resolveAlert(event.alert)
            is ClientDetailEvent.OnReopenAlertClicked -> reopenAlert(event.alert)

            // Settings events
            is ClientDetailEvent.LoadSettings -> loadSettings()
            is ClientDetailEvent.OnAddSettingClicked -> showSettingFormDialog(SettingFormMode.Create)
            is ClientDetailEvent.OnEditSettingClicked -> showSettingFormDialog(SettingFormMode.Edit(event.setting))
            is ClientDetailEvent.OnDeleteSettingClicked -> showDeleteSettingConfirmation(event.setting)
            is ClientDetailEvent.OnConfirmDeleteSetting -> confirmDeleteSetting()
            is ClientDetailEvent.OnCancelDeleteSetting -> cancelDeleteSetting()
            is ClientDetailEvent.OnDismissSettingFormDialog -> dismissSettingFormDialog()
            is ClientDetailEvent.OnSubmitSettingForm -> submitSettingForm(
                event.greenhouseId,
                event.parameterId,
                event.actuatorStateId,
                event.value,
                event.isActive
            )
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

    /**
     * Preloads all catalog data needed for dropdown menus across all tabs.
     * Called once during ViewModel initialization.
     * All catalog endpoints are loaded in parallel for better performance.
     */
    private fun loadAllCatalogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCatalogsLoading = true, catalogsError = null) }

            try {
                coroutineScope {
                    // Device catalogs
                    val categoriesDeferred = async { devicesRepository.getDeviceCategories() }
                    val typesDeferred = async { devicesRepository.getDeviceTypes(null) } // All types
                    val unitsDeferred = async { devicesRepository.getUnits() }

                    // Alert catalogs
                    val alertTypesDeferred = async { alertsRepository.getAlertTypes() }
                    val severitiesDeferred = async { alertsRepository.getAlertSeverities() }

                    // Settings catalogs
                    val actuatorStatesDeferred = async { settingsRepository.getActuatorStates() }

                    // Greenhouses (tenant-specific but used across all tabs)
                    val greenhousesDeferred = async { greenhousesRepository.getGreenhousesByTenantId(clientId) }

                    // Await all results
                    val categories = categoriesDeferred.await()
                    val types = typesDeferred.await()
                    val units = unitsDeferred.await()
                    val alertTypes = alertTypesDeferred.await()
                    val severities = severitiesDeferred.await()
                    val actuatorStates = actuatorStatesDeferred.await()
                    val greenhouses = greenhousesDeferred.await()

                    // Update state with all catalog data
                    _uiState.update { state ->
                        state.copy(
                            // Device catalogs
                            deviceCategories = categories.getOrDefault(emptyList()),
                            deviceTypes = types.getOrDefault(emptyList()),
                            deviceUnits = units.getOrDefault(emptyList()),
                            // Alert catalogs
                            alertTypes = alertTypes.getOrDefault(emptyList()),
                            alertSeverities = severities.getOrDefault(emptyList()),
                            // Settings catalogs
                            actuatorStates = actuatorStates.getOrDefault(emptyList()).sortedBy { it.displayOrder },
                            // Greenhouses
                            greenhouses = greenhouses.getOrDefault(emptyList()),
                            // Loading state
                            isCatalogsLoading = false,
                            catalogsError = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCatalogsLoading = false,
                        catalogsError = e.message ?: "Error loading catalogs"
                    )
                }
            }
        }
    }

    private fun selectTab(tab: ClientDetailTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        // Load tab-specific data when tab is selected and not already loaded
        // Note: Greenhouses and catalogs are preloaded at ViewModel init
        when (tab) {
            ClientDetailTab.USERS -> {
                if (_uiState.value.users.isEmpty() && !_uiState.value.isLoadingUsers) {
                    loadUsers()
                }
            }

            ClientDetailTab.GREENHOUSES -> {
                // Greenhouses already loaded at init, but allow manual refresh
                if (_uiState.value.greenhouses.isEmpty() && !_uiState.value.isLoadingGreenhouses) {
                    loadGreenhouses()
                }
            }

            ClientDetailTab.SECTORS -> {
                if (_uiState.value.sectors.isEmpty() && !_uiState.value.isLoadingSectors) {
                    loadSectors()
                }
            }

            ClientDetailTab.DEVICES -> {
                if (_uiState.value.devices.isEmpty() && !_uiState.value.isLoadingDevices) {
                    loadDevices()
                }
            }

            ClientDetailTab.ALERTS -> {
                if (_uiState.value.alerts.isEmpty() && !_uiState.value.isLoadingAlerts) {
                    loadAlerts()
                }
            }

            ClientDetailTab.SETTINGS -> {
                if (_uiState.value.settings.isEmpty() && !_uiState.value.isLoadingSettings) {
                    loadSettings()
                }
            }

            else -> { /* No lazy loading for other tabs */
            }
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
        location: Location?,
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
                location = location,
                isActive = status.toIsActive(),
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

    // === Users Tab Methods ===

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUsers = true, usersError = null) }

            usersRepository.getUsersByTenantId(clientId)
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

    private fun submitUserForm(username: String, email: String, password: String?, role: UserRole, isActive: Boolean) {
        val mode = _uiState.value.userFormMode

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingUser = true, submitUserError = null) }

            val result = when (mode) {
                is UserFormMode.Create -> {
                    usersRepository.createUser(
                        tenantId = clientId,
                        username = username.trim(),
                        email = email.trim(),
                        password = password ?: "",
                        role = role,
                        isActive = isActive
                    )
                }

                is UserFormMode.Edit -> {
                    usersRepository.updateUser(
                        tenantId = clientId,
                        userId = mode.user.id,
                        username = username.trim(),
                        email = email.trim(),
                        password = password?.takeIf { it.isNotBlank() },
                        role = role,
                        isActive = isActive
                    )
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

            usersRepository.deleteUser(clientId, user.id)
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

    // === Greenhouses Tab Methods ===

    private fun loadGreenhouses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingGreenhouses = true, greenhousesError = null) }

            greenhousesRepository.getGreenhousesByTenantId(clientId)
                .onSuccess { greenhouses ->
                    _uiState.update {
                        it.copy(
                            isLoadingGreenhouses = false,
                            greenhouses = greenhouses
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingGreenhouses = false,
                            greenhousesError = error.message
                        )
                    }
                }
        }
    }

    private fun showGreenhouseFormDialog(mode: GreenhouseFormMode) {
        _uiState.update {
            it.copy(
                showGreenhouseFormDialog = true,
                greenhouseFormMode = mode,
                submitGreenhouseError = null
            )
        }
    }

    private fun dismissGreenhouseFormDialog() {
        _uiState.update {
            it.copy(
                showGreenhouseFormDialog = false,
                isSubmittingGreenhouse = false,
                submitGreenhouseError = null
            )
        }
    }

    private fun submitGreenhouseForm(
        name: String,
        location: Location?,
        areaM2: Double?,
        timezone: String?,
        isActive: Boolean
    ) {
        val mode = _uiState.value.greenhouseFormMode

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingGreenhouse = true, submitGreenhouseError = null) }

            val result = when (mode) {
                is GreenhouseFormMode.Create -> {
                    greenhousesRepository.createGreenhouse(
                        tenantId = clientId,
                        name = name.trim(),
                        location = location,
                        areaM2 = areaM2,
                        timezone = timezone,
                        isActive = isActive
                    )
                }

                is GreenhouseFormMode.Edit -> {
                    greenhousesRepository.updateGreenhouse(
                        tenantId = clientId,
                        greenhouseId = mode.greenhouse.id,
                        name = name.trim(),
                        location = location,
                        areaM2 = areaM2,
                        timezone = timezone,
                        isActive = isActive
                    )
                }
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            showGreenhouseFormDialog = false,
                            isSubmittingGreenhouse = false
                        )
                    }
                    loadGreenhouses() // Reload greenhouses from repository to ensure sync
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingGreenhouse = false,
                            submitGreenhouseError = error.message
                        )
                    }
                }
        }
    }

    private fun showDeleteGreenhouseConfirmation(greenhouse: Greenhouse) {
        _uiState.update {
            it.copy(
                showDeleteGreenhouseConfirmation = true,
                greenhouseToDelete = greenhouse,
                deleteGreenhouseError = null
            )
        }
    }

    private fun cancelDeleteGreenhouse() {
        _uiState.update {
            it.copy(
                showDeleteGreenhouseConfirmation = false,
                greenhouseToDelete = null,
                deleteGreenhouseError = null
            )
        }
    }

    private fun confirmDeleteGreenhouse() {
        val greenhouse = _uiState.value.greenhouseToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingGreenhouse = true, deleteGreenhouseError = null) }

            greenhousesRepository.deleteGreenhouse(clientId, greenhouse.id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            greenhouses = state.greenhouses.filter { it.id != greenhouse.id },
                            showDeleteGreenhouseConfirmation = false,
                            greenhouseToDelete = null,
                            isDeletingGreenhouse = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeletingGreenhouse = false,
                            deleteGreenhouseError = error.message
                        )
                    }
                }
        }
    }

    // === Sectors Tab Methods ===

    private fun loadSectors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSectors = true, sectorsError = null) }

            sectorsRepository.getSectorsByTenantId(clientId)
                .onSuccess { sectors ->
                    _uiState.update {
                        it.copy(
                            isLoadingSectors = false,
                            sectors = sectors
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingSectors = false,
                            sectorsError = error.message
                        )
                    }
                }
        }
    }

    private fun showSectorFormDialog(mode: SectorFormMode) {
        _uiState.update {
            it.copy(
                showSectorFormDialog = true,
                sectorFormMode = mode,
                submitSectorError = null
            )
        }
    }

    private fun dismissSectorFormDialog() {
        _uiState.update {
            it.copy(
                showSectorFormDialog = false,
                isSubmittingSector = false,
                submitSectorError = null
            )
        }
    }

    private fun submitSectorForm(greenhouseId: Long?, variety: String) {
        if (greenhouseId == null) return

        val mode = _uiState.value.sectorFormMode

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingSector = true, submitSectorError = null) }

            val result = when (mode) {
                is SectorFormMode.Create -> {
                    sectorsRepository.createSector(
                        tenantId = clientId,
                        greenhouseId = greenhouseId,
                        variety = variety.trim().takeIf { it.isNotBlank() }
                    )
                }

                is SectorFormMode.Edit -> {
                    sectorsRepository.updateSector(
                        tenantId = clientId,
                        sectorId = mode.sector.id,
                        variety = variety.trim().takeIf { it.isNotBlank() }
                    )
                }
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            showSectorFormDialog = false,
                            isSubmittingSector = false
                        )
                    }
                    loadSectors() // Reload sectors from repository to ensure sync
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingSector = false,
                            submitSectorError = error.message
                        )
                    }
                }
        }
    }

    private fun showDeleteSectorConfirmation(sector: Sector) {
        _uiState.update {
            it.copy(
                showDeleteSectorConfirmation = true,
                sectorToDelete = sector,
                deleteSectorError = null
            )
        }
    }

    private fun cancelDeleteSector() {
        _uiState.update {
            it.copy(
                showDeleteSectorConfirmation = false,
                sectorToDelete = null,
                deleteSectorError = null
            )
        }
    }

    private fun confirmDeleteSector() {
        val sector = _uiState.value.sectorToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingSector = true, deleteSectorError = null) }

            sectorsRepository.deleteSector(clientId, sector.id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            sectors = state.sectors.filter { it.id != sector.id },
                            showDeleteSectorConfirmation = false,
                            sectorToDelete = null,
                            isDeletingSector = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeletingSector = false,
                            deleteSectorError = error.message
                        )
                    }
                }
        }
    }

    // === Devices Tab Methods ===

    private fun loadDevices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDevices = true, devicesError = null) }

            devicesRepository.getDevicesByTenantId(clientId)
                .onSuccess { devices ->
                    _uiState.update {
                        it.copy(
                            isLoadingDevices = false,
                            devices = devices
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingDevices = false,
                            devicesError = error.message
                        )
                    }
                }
        }
    }

    private fun showDeviceFormDialog(mode: DeviceFormMode) {
        _uiState.update {
            it.copy(
                showDeviceFormDialog = true,
                deviceFormMode = mode,
                submitDeviceError = null
            )
        }
        // Catalogs already loaded at ViewModel init - types filtered in UI
    }

    private fun dismissDeviceFormDialog() {
        _uiState.update {
            it.copy(
                showDeviceFormDialog = false,
                isSubmittingDevice = false,
                submitDeviceError = null
            )
        }
    }

    private fun submitDeviceForm(
        greenhouseId: Long?,
        name: String,
        categoryId: Short?,
        typeId: Short?,
        unitId: Short?,
        isActive: Boolean
    ) {
        if (greenhouseId == null) return

        val mode = _uiState.value.deviceFormMode
        val deviceName = name.ifBlank { null } // Convert empty string to null

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingDevice = true, submitDeviceError = null) }

            val result = when (mode) {
                is DeviceFormMode.Create -> {
                    devicesRepository.createDevice(
                        tenantId = clientId,
                        greenhouseId = greenhouseId,
                        name = deviceName,
                        categoryId = categoryId,
                        typeId = typeId,
                        unitId = unitId,
                        isActive = isActive
                    )
                }

                is DeviceFormMode.Edit -> {
                    devicesRepository.updateDevice(
                        tenantId = clientId,
                        deviceId = mode.device.id,
                        name = deviceName,
                        categoryId = categoryId,
                        typeId = typeId,
                        unitId = unitId,
                        isActive = isActive
                    )
                }
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            showDeviceFormDialog = false,
                            isSubmittingDevice = false
                        )
                    }
                    loadDevices() // Reload devices from repository to ensure sync
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingDevice = false,
                            submitDeviceError = error.message
                        )
                    }
                }
        }
    }

    private fun showDeleteDeviceConfirmation(device: Device) {
        _uiState.update {
            it.copy(
                showDeleteDeviceConfirmation = true,
                deviceToDelete = device,
                deleteDeviceError = null
            )
        }
    }

    private fun cancelDeleteDevice() {
        _uiState.update {
            it.copy(
                showDeleteDeviceConfirmation = false,
                deviceToDelete = null,
                deleteDeviceError = null
            )
        }
    }

    private fun confirmDeleteDevice() {
        val device = _uiState.value.deviceToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingDevice = true, deleteDeviceError = null) }

            devicesRepository.deleteDevice(clientId, device.id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            devices = state.devices.filter { it.id != device.id },
                            showDeleteDeviceConfirmation = false,
                            deviceToDelete = null,
                            isDeletingDevice = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeletingDevice = false,
                            deleteDeviceError = error.message
                        )
                    }
                }
        }
    }


    // === Alerts Tab Methods ===

    private fun loadAlerts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAlerts = true, alertsError = null) }

            alertsRepository.getAlertsByTenantId(clientId)
                .onSuccess { alerts ->
                    _uiState.update {
                        it.copy(
                            isLoadingAlerts = false,
                            alerts = alerts
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingAlerts = false,
                            alertsError = error.message
                        )
                    }
                }
        }
    }

    private fun showAlertFormDialog(mode: AlertFormMode) {
        _uiState.update {
            it.copy(
                showAlertFormDialog = true,
                alertFormMode = mode,
                submitAlertError = null
            )
        }
        // Catalogs already loaded at ViewModel init
    }

    private fun dismissAlertFormDialog() {
        _uiState.update {
            it.copy(
                showAlertFormDialog = false,
                isSubmittingAlert = false,
                submitAlertError = null
            )
        }
    }

    private fun submitAlertForm(
        greenhouseId: Long?,
        alertTypeId: Short?,
        severityId: Short?,
        message: String
    ) {
        if (greenhouseId == null) return

        val mode = _uiState.value.alertFormMode

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingAlert = true, submitAlertError = null) }

            val result = when (mode) {
                is AlertFormMode.Create -> {
                    val request = AlertCreateRequest(
                        greenhouseId = greenhouseId,
                        alertTypeId = alertTypeId,
                        severityId = severityId,
                        message = message.trim()
                    )
                    alertsRepository.createAlert(clientId, request)
                }

                is AlertFormMode.Edit -> {
                    val request = AlertUpdateRequest(
                        alertTypeId = alertTypeId,
                        severityId = severityId,
                        message = message.trim()
                    )
                    alertsRepository.updateAlert(clientId, mode.alert.id, request)
                }
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            showAlertFormDialog = false,
                            isSubmittingAlert = false
                        )
                    }
                    loadAlerts() // Reload alerts from repository to ensure sync
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingAlert = false,
                            submitAlertError = error.message
                        )
                    }
                }
        }
    }

    private fun showDeleteAlertConfirmation(alert: Alert) {
        _uiState.update {
            it.copy(
                showDeleteAlertConfirmation = true,
                alertToDelete = alert,
                deleteAlertError = null
            )
        }
    }

    private fun cancelDeleteAlert() {
        _uiState.update {
            it.copy(
                showDeleteAlertConfirmation = false,
                alertToDelete = null,
                deleteAlertError = null
            )
        }
    }

    private fun confirmDeleteAlert() {
        val alert = _uiState.value.alertToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAlert = true, deleteAlertError = null) }

            alertsRepository.deleteAlert(clientId, alert.id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            alerts = state.alerts.filter { it.id != alert.id },
                            showDeleteAlertConfirmation = false,
                            alertToDelete = null,
                            isDeletingAlert = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeletingAlert = false,
                            deleteAlertError = error.message
                        )
                    }
                }
        }
    }

    /**
     * Resolves an alert.
     */
    private fun resolveAlert(alert: Alert) {
        viewModelScope.launch {
            alertsRepository.resolveAlert(clientId, alert.id)
                .onSuccess { updatedAlert ->
                    _uiState.update { state ->
                        state.copy(
                            alerts = state.alerts.map {
                                if (it.id == alert.id) updatedAlert else it
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(alertsError = error.message)
                    }
                }
        }
    }

    /**
     * Reopens a resolved alert.
     */
    private fun reopenAlert(alert: Alert) {
        viewModelScope.launch {
            alertsRepository.reopenAlert(clientId, alert.id)
                .onSuccess { updatedAlert ->
                    _uiState.update { state ->
                        state.copy(
                            alerts = state.alerts.map {
                                if (it.id == alert.id) updatedAlert else it
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(alertsError = error.message)
                    }
                }
        }
    }

    // =============================================
    // Settings Tab Handlers
    // =============================================

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSettings = true, settingsError = null) }

            settingsRepository.getSettingsByTenantId(clientId)
                .onSuccess { settings ->
                    _uiState.update {
                        it.copy(
                            settings = settings,
                            isLoadingSettings = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingSettings = false,
                            settingsError = error.message
                        )
                    }
                }
        }
    }

    private fun showSettingFormDialog(mode: SettingFormMode) {
        _uiState.update {
            it.copy(
                showSettingFormDialog = true,
                settingFormMode = mode,
                isSubmittingSetting = false,
                submitSettingError = null
            )
        }
        // Catalogs already loaded at ViewModel init
    }

    private fun dismissSettingFormDialog() {
        _uiState.update {
            it.copy(
                showSettingFormDialog = false,
                isSubmittingSetting = false,
                submitSettingError = null
            )
        }
    }

    private fun submitSettingForm(
        greenhouseId: Long?,
        parameterId: Short,
        actuatorStateId: Short,
        value: String,
        isActive: Boolean
    ) {
        if (greenhouseId == null) return

        val mode = _uiState.value.settingFormMode

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingSetting = true, submitSettingError = null) }

            val result = when (mode) {
                is SettingFormMode.Create -> {
                    val request = SettingCreateRequest(
                        greenhouseId = greenhouseId,
                        parameterId = parameterId,
                        actuatorStateId = actuatorStateId,
                        value = value.ifBlank { null },
                        isActive = isActive
                    )
                    settingsRepository.createSetting(clientId, request)
                }

                is SettingFormMode.Edit -> {
                    val request = SettingUpdateRequest(
                        parameterId = parameterId,
                        actuatorStateId = actuatorStateId,
                        value = value.ifBlank { null },
                        isActive = isActive
                    )
                    settingsRepository.updateSetting(clientId, mode.setting.id, request)
                }
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            showSettingFormDialog = false,
                            isSubmittingSetting = false
                        )
                    }
                    loadSettings() // Reload settings from repository to ensure sync
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingSetting = false,
                            submitSettingError = error.message
                        )
                    }
                }
        }
    }

    private fun showDeleteSettingConfirmation(setting: Setting) {
        _uiState.update {
            it.copy(
                showDeleteSettingConfirmation = true,
                settingToDelete = setting,
                deleteSettingError = null
            )
        }
    }

    private fun cancelDeleteSetting() {
        _uiState.update {
            it.copy(
                showDeleteSettingConfirmation = false,
                settingToDelete = null,
                deleteSettingError = null
            )
        }
    }

    private fun confirmDeleteSetting() {
        val setting = _uiState.value.settingToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingSetting = true, deleteSettingError = null) }

            settingsRepository.deleteSetting(clientId, setting.id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            settings = state.settings.filter { it.id != setting.id },
                            showDeleteSettingConfirmation = false,
                            settingToDelete = null,
                            isDeletingSetting = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeletingSetting = false,
                            deleteSettingError = error.message
                        )
                    }
                }
        }
    }
}
