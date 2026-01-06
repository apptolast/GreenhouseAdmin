package com.apptolast.greenhouse.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.greenhouse.admin.data.model.AlertSeverityCatalog
import com.apptolast.greenhouse.admin.data.model.AlertType
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogCategory
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogType
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogUnit
import com.apptolast.greenhouse.admin.data.model.Period
import com.apptolast.greenhouse.admin.domain.repository.AuthRepository
import com.apptolast.greenhouse.admin.domain.repository.CatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings screen following MVVM+MVI pattern.
 * Manages account settings and catalog CRUD operations.
 */
class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())

    /**
     * Exposes immutable StateFlow for UI consumption.
     */
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
        loadAllCatalogs()
    }

    /**
     * Handles all UI events following MVI pattern.
     */
    fun onEvent(event: SettingsEvent) {
        when (event) {
            // Account events
            is SettingsEvent.OnLogoutClicked -> showLogoutConfirmation()
            is SettingsEvent.OnConfirmLogout -> performLogout()
            is SettingsEvent.OnCancelLogout -> cancelLogout()
            is SettingsEvent.OnLogoutComplete -> handleLogoutComplete()

            // Tab events
            is SettingsEvent.OnTabSelected -> selectTab(event.tab)

            // Device Categories events
            is SettingsEvent.OnAddDeviceCategoryClicked -> showDeviceCategoryDialog(DeviceCategoryFormMode.Create)
            is SettingsEvent.OnEditDeviceCategoryClicked -> showDeviceCategoryDialog(DeviceCategoryFormMode.Edit(event.category))
            is SettingsEvent.OnDeleteDeviceCategoryClicked -> showDeleteDeviceCategoryConfirmation(event.category)
            is SettingsEvent.OnSubmitDeviceCategory -> submitDeviceCategory(event.name)
            is SettingsEvent.OnDismissDeviceCategoryDialog -> dismissDeviceCategoryDialog()
            is SettingsEvent.OnConfirmDeleteDeviceCategory -> confirmDeleteDeviceCategory()
            is SettingsEvent.OnCancelDeleteDeviceCategory -> cancelDeleteDeviceCategory()

            // Device Types events
            is SettingsEvent.OnAddDeviceTypeClicked -> showDeviceTypeDialog(DeviceTypeFormMode.Create)
            is SettingsEvent.OnEditDeviceTypeClicked -> showDeviceTypeDialog(DeviceTypeFormMode.Edit(event.deviceType))
            is SettingsEvent.OnDeleteDeviceTypeClicked -> showDeleteDeviceTypeConfirmation(event.deviceType)
            is SettingsEvent.OnSubmitDeviceType -> submitDeviceType(event)
            is SettingsEvent.OnDismissDeviceTypeDialog -> dismissDeviceTypeDialog()
            is SettingsEvent.OnConfirmDeleteDeviceType -> confirmDeleteDeviceType()
            is SettingsEvent.OnCancelDeleteDeviceType -> cancelDeleteDeviceType()
            is SettingsEvent.OnActivateDeviceType -> activateDeviceType(event.deviceType)
            is SettingsEvent.OnDeactivateDeviceType -> deactivateDeviceType(event.deviceType)

            // Alert Types events
            is SettingsEvent.OnAddAlertTypeClicked -> showAlertTypeDialog(AlertTypeFormMode.Create)
            is SettingsEvent.OnEditAlertTypeClicked -> showAlertTypeDialog(AlertTypeFormMode.Edit(event.alertType))
            is SettingsEvent.OnDeleteAlertTypeClicked -> showDeleteAlertTypeConfirmation(event.alertType)
            is SettingsEvent.OnSubmitAlertType -> submitAlertType(event.name, event.description)
            is SettingsEvent.OnDismissAlertTypeDialog -> dismissAlertTypeDialog()
            is SettingsEvent.OnConfirmDeleteAlertType -> confirmDeleteAlertType()
            is SettingsEvent.OnCancelDeleteAlertType -> cancelDeleteAlertType()

            // Alert Severities events
            is SettingsEvent.OnAddAlertSeverityClicked -> showAlertSeverityDialog(AlertSeverityFormMode.Create)
            is SettingsEvent.OnEditAlertSeverityClicked -> showAlertSeverityDialog(AlertSeverityFormMode.Edit(event.severity))
            is SettingsEvent.OnDeleteAlertSeverityClicked -> showDeleteAlertSeverityConfirmation(event.severity)
            is SettingsEvent.OnSubmitAlertSeverity -> submitAlertSeverity(event)
            is SettingsEvent.OnDismissAlertSeverityDialog -> dismissAlertSeverityDialog()
            is SettingsEvent.OnConfirmDeleteAlertSeverity -> confirmDeleteAlertSeverity()
            is SettingsEvent.OnCancelDeleteAlertSeverity -> cancelDeleteAlertSeverity()

            // Periods events
            is SettingsEvent.OnAddPeriodClicked -> showPeriodDialog(PeriodFormMode.Create)
            is SettingsEvent.OnEditPeriodClicked -> showPeriodDialog(PeriodFormMode.Edit(event.period))
            is SettingsEvent.OnDeletePeriodClicked -> showDeletePeriodConfirmation(event.period)
            is SettingsEvent.OnSubmitPeriod -> submitPeriod(event.name)
            is SettingsEvent.OnDismissPeriodDialog -> dismissPeriodDialog()
            is SettingsEvent.OnConfirmDeletePeriod -> confirmDeletePeriod()
            is SettingsEvent.OnCancelDeletePeriod -> cancelDeletePeriod()

            // Refresh events
            is SettingsEvent.OnRefreshCatalogs -> loadAllCatalogs()
        }
    }

    // ==================== ACCOUNT METHODS ====================

    private fun loadUserInfo() {
        val session = authRepository.getCurrentSession()
        _uiState.update {
            it.copy(
                username = session?.username ?: "",
                roles = session?.roles ?: emptyList()
            )
        }
    }

    private fun showLogoutConfirmation() {
        _uiState.update { it.copy(showLogoutConfirmation = true) }
    }

    private fun cancelLogout() {
        _uiState.update { it.copy(showLogoutConfirmation = false) }
    }

    private fun performLogout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true, showLogoutConfirmation = false) }

            authRepository.logout()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            isLogoutSuccessful = true
                        )
                    }
                }
                .onFailure {
                    authRepository.clearLocalSession()
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            isLogoutSuccessful = true
                        )
                    }
                }
        }
    }

    private fun handleLogoutComplete() {
        _uiState.update { it.copy(isLogoutSuccessful = false) }
    }

    // ==================== TAB METHODS ====================

    private fun selectTab(tab: SettingsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    // ==================== CATALOG LOADING ====================

    private fun loadAllCatalogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCatalogsLoading = true, catalogsError = null) }

            // Load all catalogs in parallel
            val categoriesResult = catalogRepository.getDeviceCategories()
            val typesResult = catalogRepository.getDeviceTypes()
            val unitsResult = catalogRepository.getDeviceUnits()
            val alertTypesResult = catalogRepository.getAlertTypes()
            val severitiesResult = catalogRepository.getAlertSeverities()
            val periodsResult = catalogRepository.getPeriods()

            _uiState.update { state ->
                state.copy(
                    isCatalogsLoading = false,
                    deviceCategories = categoriesResult.getOrDefault(emptyList()).sortedBy { it.id },
                    deviceTypes = typesResult.getOrDefault(emptyList()).sortedBy { it.id },
                    deviceUnits = unitsResult.getOrDefault(emptyList()).sortedBy { it.id },
                    alertTypes = alertTypesResult.getOrDefault(emptyList()).sortedBy { it.id },
                    alertSeverities = severitiesResult.getOrDefault(emptyList()).sortedBy { it.id },
                    periods = periodsResult.getOrDefault(emptyList()).sortedBy { it.id },
                    catalogsError = if (categoriesResult.isFailure || typesResult.isFailure ||
                        unitsResult.isFailure || alertTypesResult.isFailure ||
                        severitiesResult.isFailure || periodsResult.isFailure
                    ) "Failed to load some catalogs" else null
                )
            }
        }
    }

    // ==================== DEVICE CATEGORIES CRUD ====================

    private fun showDeviceCategoryDialog(mode: DeviceCategoryFormMode) {
        _uiState.update {
            it.copy(
                showDeviceCategoryDialog = true,
                deviceCategoryFormMode = mode,
                submitDeviceCategoryError = null
            )
        }
    }

    private fun dismissDeviceCategoryDialog() {
        _uiState.update {
            it.copy(
                showDeviceCategoryDialog = false,
                submitDeviceCategoryError = null
            )
        }
    }

    private fun showDeleteDeviceCategoryConfirmation(category: DeviceCatalogCategory) {
        _uiState.update {
            it.copy(
                showDeleteDeviceCategoryConfirmation = true,
                deviceCategoryToDelete = category
            )
        }
    }

    private fun cancelDeleteDeviceCategory() {
        _uiState.update {
            it.copy(
                showDeleteDeviceCategoryConfirmation = false,
                deviceCategoryToDelete = null
            )
        }
    }

    private fun submitDeviceCategory(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingDeviceCategory = true, submitDeviceCategoryError = null) }

            val result = when (val mode = _uiState.value.deviceCategoryFormMode) {
                is DeviceCategoryFormMode.Create -> catalogRepository.createDeviceCategory(name)
                is DeviceCategoryFormMode.Edit -> catalogRepository.updateDeviceCategory(mode.category.id, name)
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSubmittingDeviceCategory = false,
                            showDeviceCategoryDialog = false
                        )
                    }
                    loadDeviceCategories()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingDeviceCategory = false,
                            submitDeviceCategoryError = error.message ?: "Failed to save category"
                        )
                    }
                }
        }
    }

    private fun confirmDeleteDeviceCategory() {
        val categoryToDelete = _uiState.value.deviceCategoryToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingDeviceCategory = true) }

            catalogRepository.deleteDeviceCategory(categoryToDelete.id)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeletingDeviceCategory = false,
                            showDeleteDeviceCategoryConfirmation = false,
                            deviceCategoryToDelete = null
                        )
                    }
                    loadDeviceCategories()
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isDeletingDeviceCategory = false,
                            showDeleteDeviceCategoryConfirmation = false,
                            deviceCategoryToDelete = null
                        )
                    }
                }
        }
    }

    private fun loadDeviceCategories() {
        viewModelScope.launch {
            catalogRepository.getDeviceCategories()
                .onSuccess { categories ->
                    _uiState.update { it.copy(deviceCategories = categories.sortedBy { c -> c.id }) }
                }
        }
    }

    // ==================== DEVICE TYPES CRUD ====================

    private fun showDeviceTypeDialog(mode: DeviceTypeFormMode) {
        _uiState.update {
            it.copy(
                showDeviceTypeDialog = true,
                deviceTypeFormMode = mode,
                submitDeviceTypeError = null
            )
        }
    }

    private fun dismissDeviceTypeDialog() {
        _uiState.update {
            it.copy(
                showDeviceTypeDialog = false,
                submitDeviceTypeError = null
            )
        }
    }

    private fun showDeleteDeviceTypeConfirmation(deviceType: DeviceCatalogType) {
        _uiState.update {
            it.copy(
                showDeleteDeviceTypeConfirmation = true,
                deviceTypeToDelete = deviceType
            )
        }
    }

    private fun cancelDeleteDeviceType() {
        _uiState.update {
            it.copy(
                showDeleteDeviceTypeConfirmation = false,
                deviceTypeToDelete = null
            )
        }
    }

    private fun submitDeviceType(event: SettingsEvent.OnSubmitDeviceType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingDeviceType = true, submitDeviceTypeError = null) }

            val result = when (val mode = _uiState.value.deviceTypeFormMode) {
                is DeviceTypeFormMode.Create -> catalogRepository.createDeviceType(
                    name = event.name,
                    description = event.description,
                    categoryId = event.categoryId,
                    defaultUnitId = event.defaultUnitId,
                    dataType = event.dataType,
                    minExpectedValue = event.minExpectedValue,
                    maxExpectedValue = event.maxExpectedValue,
                    controlType = event.controlType,
                    isActive = event.isActive
                )

                is DeviceTypeFormMode.Edit -> catalogRepository.updateDeviceType(
                    id = mode.deviceType.id,
                    name = event.name,
                    description = event.description,
                    categoryId = event.categoryId,
                    defaultUnitId = event.defaultUnitId,
                    dataType = event.dataType,
                    minExpectedValue = event.minExpectedValue,
                    maxExpectedValue = event.maxExpectedValue,
                    controlType = event.controlType,
                    isActive = event.isActive
                )
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSubmittingDeviceType = false,
                            showDeviceTypeDialog = false
                        )
                    }
                    loadDeviceTypes()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingDeviceType = false,
                            submitDeviceTypeError = error.message ?: "Failed to save device type"
                        )
                    }
                }
        }
    }

    private fun confirmDeleteDeviceType() {
        val typeToDelete = _uiState.value.deviceTypeToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingDeviceType = true) }

            catalogRepository.deleteDeviceType(typeToDelete.id)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeletingDeviceType = false,
                            showDeleteDeviceTypeConfirmation = false,
                            deviceTypeToDelete = null
                        )
                    }
                    loadDeviceTypes()
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isDeletingDeviceType = false,
                            showDeleteDeviceTypeConfirmation = false,
                            deviceTypeToDelete = null
                        )
                    }
                }
        }
    }

    private fun activateDeviceType(deviceType: DeviceCatalogType) {
        viewModelScope.launch {
            catalogRepository.activateDeviceType(deviceType.id)
                .onSuccess { loadDeviceTypes() }
        }
    }

    private fun deactivateDeviceType(deviceType: DeviceCatalogType) {
        viewModelScope.launch {
            catalogRepository.deactivateDeviceType(deviceType.id)
                .onSuccess { loadDeviceTypes() }
        }
    }

    private fun loadDeviceTypes() {
        viewModelScope.launch {
            catalogRepository.getDeviceTypes()
                .onSuccess { types ->
                    _uiState.update { it.copy(deviceTypes = types.sortedBy { t -> t.id }) }
                }
        }
    }

    // ==================== ALERT TYPES CRUD ====================

    private fun showAlertTypeDialog(mode: AlertTypeFormMode) {
        _uiState.update {
            it.copy(
                showAlertTypeDialog = true,
                alertTypeFormMode = mode,
                submitAlertTypeError = null
            )
        }
    }

    private fun dismissAlertTypeDialog() {
        _uiState.update {
            it.copy(
                showAlertTypeDialog = false,
                submitAlertTypeError = null
            )
        }
    }

    private fun showDeleteAlertTypeConfirmation(alertType: AlertType) {
        _uiState.update {
            it.copy(
                showDeleteAlertTypeConfirmation = true,
                alertTypeToDelete = alertType
            )
        }
    }

    private fun cancelDeleteAlertType() {
        _uiState.update {
            it.copy(
                showDeleteAlertTypeConfirmation = false,
                alertTypeToDelete = null
            )
        }
    }

    private fun submitAlertType(name: String, description: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingAlertType = true, submitAlertTypeError = null) }

            val result = when (val mode = _uiState.value.alertTypeFormMode) {
                is AlertTypeFormMode.Create -> catalogRepository.createAlertType(name, description)
                is AlertTypeFormMode.Edit -> catalogRepository.updateAlertType(mode.alertType.id, name, description)
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSubmittingAlertType = false,
                            showAlertTypeDialog = false
                        )
                    }
                    loadAlertTypes()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingAlertType = false,
                            submitAlertTypeError = error.message ?: "Failed to save alert type"
                        )
                    }
                }
        }
    }

    private fun confirmDeleteAlertType() {
        val typeToDelete = _uiState.value.alertTypeToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAlertType = true) }

            catalogRepository.deleteAlertType(typeToDelete.id)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeletingAlertType = false,
                            showDeleteAlertTypeConfirmation = false,
                            alertTypeToDelete = null
                        )
                    }
                    loadAlertTypes()
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isDeletingAlertType = false,
                            showDeleteAlertTypeConfirmation = false,
                            alertTypeToDelete = null
                        )
                    }
                }
        }
    }

    private fun loadAlertTypes() {
        viewModelScope.launch {
            catalogRepository.getAlertTypes()
                .onSuccess { types ->
                    _uiState.update { it.copy(alertTypes = types.sortedBy { t -> t.id }) }
                }
        }
    }

    // ==================== ALERT SEVERITIES CRUD ====================

    private fun showAlertSeverityDialog(mode: AlertSeverityFormMode) {
        _uiState.update {
            it.copy(
                showAlertSeverityDialog = true,
                alertSeverityFormMode = mode,
                submitAlertSeverityError = null
            )
        }
    }

    private fun dismissAlertSeverityDialog() {
        _uiState.update {
            it.copy(
                showAlertSeverityDialog = false,
                submitAlertSeverityError = null
            )
        }
    }

    private fun showDeleteAlertSeverityConfirmation(severity: AlertSeverityCatalog) {
        _uiState.update {
            it.copy(
                showDeleteAlertSeverityConfirmation = true,
                alertSeverityToDelete = severity
            )
        }
    }

    private fun cancelDeleteAlertSeverity() {
        _uiState.update {
            it.copy(
                showDeleteAlertSeverityConfirmation = false,
                alertSeverityToDelete = null
            )
        }
    }

    private fun submitAlertSeverity(event: SettingsEvent.OnSubmitAlertSeverity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingAlertSeverity = true, submitAlertSeverityError = null) }

            val result = when (val mode = _uiState.value.alertSeverityFormMode) {
                is AlertSeverityFormMode.Create -> catalogRepository.createAlertSeverity(
                    name = event.name,
                    level = event.level,
                    description = event.description,
                    color = event.color,
                    requiresAction = event.requiresAction,
                    notificationDelayMinutes = event.notificationDelayMinutes
                )

                is AlertSeverityFormMode.Edit -> catalogRepository.updateAlertSeverity(
                    id = mode.severity.id,
                    name = event.name,
                    level = event.level,
                    description = event.description,
                    color = event.color,
                    requiresAction = event.requiresAction,
                    notificationDelayMinutes = event.notificationDelayMinutes
                )
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSubmittingAlertSeverity = false,
                            showAlertSeverityDialog = false
                        )
                    }
                    loadAlertSeverities()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingAlertSeverity = false,
                            submitAlertSeverityError = error.message ?: "Failed to save alert severity"
                        )
                    }
                }
        }
    }

    private fun confirmDeleteAlertSeverity() {
        val severityToDelete = _uiState.value.alertSeverityToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAlertSeverity = true) }

            catalogRepository.deleteAlertSeverity(severityToDelete.id)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeletingAlertSeverity = false,
                            showDeleteAlertSeverityConfirmation = false,
                            alertSeverityToDelete = null
                        )
                    }
                    loadAlertSeverities()
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isDeletingAlertSeverity = false,
                            showDeleteAlertSeverityConfirmation = false,
                            alertSeverityToDelete = null
                        )
                    }
                }
        }
    }

    private fun loadAlertSeverities() {
        viewModelScope.launch {
            catalogRepository.getAlertSeverities()
                .onSuccess { severities ->
                    _uiState.update { it.copy(alertSeverities = severities.sortedBy { s -> s.id }) }
                }
        }
    }

    // ==================== PERIODS CRUD ====================

    private fun showPeriodDialog(mode: PeriodFormMode) {
        _uiState.update {
            it.copy(
                showPeriodDialog = true,
                periodFormMode = mode,
                submitPeriodError = null
            )
        }
    }

    private fun dismissPeriodDialog() {
        _uiState.update {
            it.copy(
                showPeriodDialog = false,
                submitPeriodError = null
            )
        }
    }

    private fun showDeletePeriodConfirmation(period: Period) {
        _uiState.update {
            it.copy(
                showDeletePeriodConfirmation = true,
                periodToDelete = period
            )
        }
    }

    private fun cancelDeletePeriod() {
        _uiState.update {
            it.copy(
                showDeletePeriodConfirmation = false,
                periodToDelete = null
            )
        }
    }

    private fun submitPeriod(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingPeriod = true, submitPeriodError = null) }

            val result = when (val mode = _uiState.value.periodFormMode) {
                is PeriodFormMode.Create -> catalogRepository.createPeriod(name)
                is PeriodFormMode.Edit -> catalogRepository.updatePeriod(mode.period.id, name)
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSubmittingPeriod = false,
                            showPeriodDialog = false
                        )
                    }
                    loadPeriods()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingPeriod = false,
                            submitPeriodError = error.message ?: "Failed to save period"
                        )
                    }
                }
        }
    }

    private fun confirmDeletePeriod() {
        val periodToDelete = _uiState.value.periodToDelete ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingPeriod = true) }

            catalogRepository.deletePeriod(periodToDelete.id)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeletingPeriod = false,
                            showDeletePeriodConfirmation = false,
                            periodToDelete = null
                        )
                    }
                    loadPeriods()
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isDeletingPeriod = false,
                            showDeletePeriodConfirmation = false,
                            periodToDelete = null
                        )
                    }
                }
        }
    }

    private fun loadPeriods() {
        viewModelScope.launch {
            catalogRepository.getPeriods()
                .onSuccess { periods ->
                    _uiState.update { it.copy(periods = periods.sortedBy { p -> p.id }) }
                }
        }
    }
}

// ==================== UI STATE ====================

/**
 * UI state for the Settings screen.
 */
data class SettingsUiState(
    // Account tab state
    val username: String = "",
    val roles: List<String> = emptyList(),
    val showLogoutConfirmation: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isLogoutSuccessful: Boolean = false,

    // Tab selection
    val selectedTab: SettingsTab = SettingsTab.ACCOUNT,

    // Global catalog loading state
    val isCatalogsLoading: Boolean = true,
    val catalogsError: String? = null,

    // Device Categories state
    val deviceCategories: List<DeviceCatalogCategory> = emptyList(),
    val showDeviceCategoryDialog: Boolean = false,
    val deviceCategoryFormMode: DeviceCategoryFormMode = DeviceCategoryFormMode.Create,
    val isSubmittingDeviceCategory: Boolean = false,
    val submitDeviceCategoryError: String? = null,
    val showDeleteDeviceCategoryConfirmation: Boolean = false,
    val deviceCategoryToDelete: DeviceCatalogCategory? = null,
    val isDeletingDeviceCategory: Boolean = false,

    // Device Types state
    val deviceTypes: List<DeviceCatalogType> = emptyList(),
    val showDeviceTypeDialog: Boolean = false,
    val deviceTypeFormMode: DeviceTypeFormMode = DeviceTypeFormMode.Create,
    val isSubmittingDeviceType: Boolean = false,
    val submitDeviceTypeError: String? = null,
    val showDeleteDeviceTypeConfirmation: Boolean = false,
    val deviceTypeToDelete: DeviceCatalogType? = null,
    val isDeletingDeviceType: Boolean = false,

    // Device Units state (read-only)
    val deviceUnits: List<DeviceCatalogUnit> = emptyList(),

    // Alert Types state
    val alertTypes: List<AlertType> = emptyList(),
    val showAlertTypeDialog: Boolean = false,
    val alertTypeFormMode: AlertTypeFormMode = AlertTypeFormMode.Create,
    val isSubmittingAlertType: Boolean = false,
    val submitAlertTypeError: String? = null,
    val showDeleteAlertTypeConfirmation: Boolean = false,
    val alertTypeToDelete: AlertType? = null,
    val isDeletingAlertType: Boolean = false,

    // Alert Severities state
    val alertSeverities: List<AlertSeverityCatalog> = emptyList(),
    val showAlertSeverityDialog: Boolean = false,
    val alertSeverityFormMode: AlertSeverityFormMode = AlertSeverityFormMode.Create,
    val isSubmittingAlertSeverity: Boolean = false,
    val submitAlertSeverityError: String? = null,
    val showDeleteAlertSeverityConfirmation: Boolean = false,
    val alertSeverityToDelete: AlertSeverityCatalog? = null,
    val isDeletingAlertSeverity: Boolean = false,

    // Periods state
    val periods: List<Period> = emptyList(),
    val showPeriodDialog: Boolean = false,
    val periodFormMode: PeriodFormMode = PeriodFormMode.Create,
    val isSubmittingPeriod: Boolean = false,
    val submitPeriodError: String? = null,
    val showDeletePeriodConfirmation: Boolean = false,
    val periodToDelete: Period? = null,
    val isDeletingPeriod: Boolean = false
)

// ==================== ENUMS AND SEALED INTERFACES ====================

/**
 * Enum representing the available tabs in the Settings screen.
 */
enum class SettingsTab {
    ACCOUNT,
    DEVICE_CATEGORIES,
    DEVICE_TYPES,
    DEVICE_UNITS,
    ALERT_TYPES,
    ALERT_SEVERITIES,
    PERIODS
}

/**
 * Mode for the device category form dialog.
 */
sealed interface DeviceCategoryFormMode {
    data object Create : DeviceCategoryFormMode
    data class Edit(val category: DeviceCatalogCategory) : DeviceCategoryFormMode
}

/**
 * Mode for the device type form dialog.
 */
sealed interface DeviceTypeFormMode {
    data object Create : DeviceTypeFormMode
    data class Edit(val deviceType: DeviceCatalogType) : DeviceTypeFormMode
}

/**
 * Mode for the alert type form dialog.
 */
sealed interface AlertTypeFormMode {
    data object Create : AlertTypeFormMode
    data class Edit(val alertType: AlertType) : AlertTypeFormMode
}

/**
 * Mode for the alert severity form dialog.
 */
sealed interface AlertSeverityFormMode {
    data object Create : AlertSeverityFormMode
    data class Edit(val severity: AlertSeverityCatalog) : AlertSeverityFormMode
}

/**
 * Mode for the period form dialog.
 */
sealed interface PeriodFormMode {
    data object Create : PeriodFormMode
    data class Edit(val period: Period) : PeriodFormMode
}

// ==================== EVENTS ====================

/**
 * Events for the Settings screen.
 */
sealed interface SettingsEvent {
    // Account events
    data object OnLogoutClicked : SettingsEvent
    data object OnConfirmLogout : SettingsEvent
    data object OnCancelLogout : SettingsEvent
    data object OnLogoutComplete : SettingsEvent

    // Tab events
    data class OnTabSelected(val tab: SettingsTab) : SettingsEvent

    // Device Categories events
    data object OnAddDeviceCategoryClicked : SettingsEvent
    data class OnEditDeviceCategoryClicked(val category: DeviceCatalogCategory) : SettingsEvent
    data class OnDeleteDeviceCategoryClicked(val category: DeviceCatalogCategory) : SettingsEvent
    data class OnSubmitDeviceCategory(val name: String) : SettingsEvent
    data object OnDismissDeviceCategoryDialog : SettingsEvent
    data object OnConfirmDeleteDeviceCategory : SettingsEvent
    data object OnCancelDeleteDeviceCategory : SettingsEvent

    // Device Types events
    data object OnAddDeviceTypeClicked : SettingsEvent
    data class OnEditDeviceTypeClicked(val deviceType: DeviceCatalogType) : SettingsEvent
    data class OnDeleteDeviceTypeClicked(val deviceType: DeviceCatalogType) : SettingsEvent
    data class OnSubmitDeviceType(
        val name: String,
        val description: String?,
        val categoryId: Short,
        val defaultUnitId: Short?,
        val dataType: String?,
        val minExpectedValue: Double?,
        val maxExpectedValue: Double?,
        val controlType: String?,
        val isActive: Boolean
    ) : SettingsEvent

    data object OnDismissDeviceTypeDialog : SettingsEvent
    data object OnConfirmDeleteDeviceType : SettingsEvent
    data object OnCancelDeleteDeviceType : SettingsEvent
    data class OnActivateDeviceType(val deviceType: DeviceCatalogType) : SettingsEvent
    data class OnDeactivateDeviceType(val deviceType: DeviceCatalogType) : SettingsEvent

    // Alert Types events
    data object OnAddAlertTypeClicked : SettingsEvent
    data class OnEditAlertTypeClicked(val alertType: AlertType) : SettingsEvent
    data class OnDeleteAlertTypeClicked(val alertType: AlertType) : SettingsEvent
    data class OnSubmitAlertType(val name: String, val description: String?) : SettingsEvent
    data object OnDismissAlertTypeDialog : SettingsEvent
    data object OnConfirmDeleteAlertType : SettingsEvent
    data object OnCancelDeleteAlertType : SettingsEvent

    // Alert Severities events
    data object OnAddAlertSeverityClicked : SettingsEvent
    data class OnEditAlertSeverityClicked(val severity: AlertSeverityCatalog) : SettingsEvent
    data class OnDeleteAlertSeverityClicked(val severity: AlertSeverityCatalog) : SettingsEvent
    data class OnSubmitAlertSeverity(
        val name: String,
        val level: Short,
        val description: String?,
        val color: String?,
        val requiresAction: Boolean,
        val notificationDelayMinutes: Int
    ) : SettingsEvent

    data object OnDismissAlertSeverityDialog : SettingsEvent
    data object OnConfirmDeleteAlertSeverity : SettingsEvent
    data object OnCancelDeleteAlertSeverity : SettingsEvent

    // Periods events
    data object OnAddPeriodClicked : SettingsEvent
    data class OnEditPeriodClicked(val period: Period) : SettingsEvent
    data class OnDeletePeriodClicked(val period: Period) : SettingsEvent
    data class OnSubmitPeriod(val name: String) : SettingsEvent
    data object OnDismissPeriodDialog : SettingsEvent
    data object OnConfirmDeletePeriod : SettingsEvent
    data object OnCancelDeletePeriod : SettingsEvent

    // Refresh events
    data object OnRefreshCatalogs : SettingsEvent
}
