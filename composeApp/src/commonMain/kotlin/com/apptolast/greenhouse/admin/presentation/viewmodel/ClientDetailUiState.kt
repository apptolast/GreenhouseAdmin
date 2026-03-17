package com.apptolast.greenhouse.admin.presentation.viewmodel

import com.apptolast.greenhouse.admin.data.model.ActuatorState
import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.AlertSeverityCatalog
import com.apptolast.greenhouse.admin.data.model.AlertType
import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.DataType
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogCategory
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogType
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogUnit
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.data.model.User

/**
 * Represents the complete UI state for the Client Detail screen.
 * Follows MVI pattern with immutable state.
 */
data class ClientDetailUiState(
    // Loading states
    val isLoading: Boolean = true,
    val error: String? = null,

    // Navigation/Layout states
    val selectedMenuId: String = "clients",
    val topBarSearchQuery: String = "",

    // Catalogs state (preloaded on init for all form dropdowns)
    val isCatalogsLoading: Boolean = true,
    val catalogsError: String? = null,

    // Client data
    val client: Client? = null,

    // Tab state
    val selectedTab: ClientDetailTab = ClientDetailTab.GENERAL,

    // Edit dialog states
    val showEditClientDialog: Boolean = false,
    val isUpdatingClient: Boolean = false,
    val updateClientError: String? = null,

    // Delete dialog states
    val showDeleteConfirmation: Boolean = false,
    val isDeletingClient: Boolean = false,
    val deleteClientError: String? = null,

    // Navigation signal for after successful delete
    val shouldNavigateBack: Boolean = false,

    // Users tab state
    val users: List<User> = emptyList(),
    val isLoadingUsers: Boolean = false,
    val usersError: String? = null,

    // User form dialog states
    val showUserFormDialog: Boolean = false,
    val userFormMode: FormMode<User> = FormMode.Create,
    val isSubmittingUser: Boolean = false,
    val submitUserError: String? = null,

    // User delete dialog states
    val showDeleteUserConfirmation: Boolean = false,
    val userToDelete: User? = null,
    val isDeletingUser: Boolean = false,
    val deleteUserError: String? = null,

    // Greenhouses tab state
    val greenhouses: List<Greenhouse> = emptyList(),
    val isLoadingGreenhouses: Boolean = false,
    val greenhousesError: String? = null,

    // Greenhouse form dialog states
    val showGreenhouseFormDialog: Boolean = false,
    val greenhouseFormMode: FormMode<Greenhouse> = FormMode.Create,
    val isSubmittingGreenhouse: Boolean = false,
    val submitGreenhouseError: String? = null,

    // Greenhouse delete dialog states
    val showDeleteGreenhouseConfirmation: Boolean = false,
    val greenhouseToDelete: Greenhouse? = null,
    val isDeletingGreenhouse: Boolean = false,
    val deleteGreenhouseError: String? = null,

    // Sectors tab state
    val sectors: List<Sector> = emptyList(),
    val isLoadingSectors: Boolean = false,
    val sectorsError: String? = null,

    // Sector form dialog states
    val showSectorFormDialog: Boolean = false,
    val sectorFormMode: FormMode<Sector> = FormMode.Create,
    val isSubmittingSector: Boolean = false,
    val submitSectorError: String? = null,

    // Sector delete dialog states
    val showDeleteSectorConfirmation: Boolean = false,
    val sectorToDelete: Sector? = null,
    val isDeletingSector: Boolean = false,
    val deleteSectorError: String? = null,

    // Devices tab state
    val devices: List<Device> = emptyList(),
    val isLoadingDevices: Boolean = false,
    val devicesError: String? = null,

    // Device form dialog states
    val showDeviceFormDialog: Boolean = false,
    val deviceFormMode: FormMode<Device> = FormMode.Create,
    val isSubmittingDevice: Boolean = false,
    val submitDeviceError: String? = null,

    // Device delete dialog states
    val showDeleteDeviceConfirmation: Boolean = false,
    val deviceToDelete: Device? = null,
    val isDeletingDevice: Boolean = false,
    val deleteDeviceError: String? = null,

    // Device catalog state (preloaded at init)
    val deviceCategories: List<DeviceCatalogCategory> = emptyList(),
    val deviceTypes: List<DeviceCatalogType> = emptyList(),
    val deviceUnits: List<DeviceCatalogUnit> = emptyList(),

    // Alerts tab state
    val alerts: List<Alert> = emptyList(),
    val isLoadingAlerts: Boolean = false,
    val alertsError: String? = null,

    // Alert form dialog states
    val showAlertFormDialog: Boolean = false,
    val alertFormMode: FormMode<Alert> = FormMode.Create,
    val isSubmittingAlert: Boolean = false,
    val submitAlertError: String? = null,

    // Alert delete dialog states
    val showDeleteAlertConfirmation: Boolean = false,
    val alertToDelete: Alert? = null,
    val isDeletingAlert: Boolean = false,
    val deleteAlertError: String? = null,

    // Alert catalog state (preloaded at init)
    val alertTypes: List<AlertType> = emptyList(),
    val alertSeverities: List<AlertSeverityCatalog> = emptyList(),

    // Settings tab state
    val settings: List<Setting> = emptyList(),
    val isLoadingSettings: Boolean = false,
    val settingsError: String? = null,

    // Setting form dialog states
    val showSettingFormDialog: Boolean = false,
    val settingFormMode: FormMode<Setting> = FormMode.Create,
    val isSubmittingSetting: Boolean = false,
    val submitSettingError: String? = null,

    // Setting delete dialog states
    val showDeleteSettingConfirmation: Boolean = false,
    val settingToDelete: Setting? = null,
    val isDeletingSetting: Boolean = false,
    val deleteSettingError: String? = null,

    // Setting catalog state (preloaded at init)
    val actuatorStates: List<ActuatorState> = emptyList(),
    val dataTypes: List<DataType> = emptyList(),

    // === Greenhouse hierarchical view state ===

    // Currently expanded greenhouses in the tree panel
    val expandedGreenhouseIds: Set<Long> = emptySet(),

    // Selected greenhouse in the tree panel (null = none)
    val selectedGreenhouseId: Long? = null,

    // Selected sector within a greenhouse (null = none)
    val selectedSectorId: Long? = null,

    // Active sub-tab when viewing a sector's detail
    val sectorSubTab: SectorSubTab = SectorSubTab.DEVICES,

    // Alerts tab filters
    val alertsFilterGreenhouseId: Long? = null,
    val alertsFilterSectorId: Long? = null
) {
    /**
     * Returns true if in error state with no content.
     */
    val isError: Boolean
        get() = error != null && client == null

    /**
     * Returns true if client data is loaded.
     */
    val hasContent: Boolean
        get() = client != null

    /**
     * Sectors belonging to the selected greenhouse.
     */
    fun sectorsForGreenhouse(greenhouseId: Long): List<Sector> =
        sectors.filter { it.greenhouseId == greenhouseId }

    /**
     * Devices belonging to a specific sector.
     */
    fun devicesForSector(sectorId: Long): List<Device> =
        devices.filter { it.sectorId == sectorId }

    /**
     * Alerts belonging to a specific sector.
     */
    fun alertsForSector(sectorId: Long): List<Alert> =
        alerts.filter { it.sectorId == sectorId }

    /**
     * Settings belonging to a specific sector.
     */
    fun settingsForSector(sectorId: Long): List<Setting> =
        settings.filter { it.sectorId == sectorId }

    /**
     * Filtered alerts for the global Alerts tab.
     * Filters by greenhouse and/or sector if filter values are set.
     */
    val filteredAlerts: List<Alert>
        get() {
            var result = alerts
            alertsFilterSectorId?.let { sId ->
                result = result.filter { it.sectorId == sId }
            } ?: alertsFilterGreenhouseId?.let { ghId ->
                val sectorIds = sectors.filter { it.greenhouseId == ghId }.map { it.id }.toSet()
                result = result.filter { it.sectorId in sectorIds }
            }
            return result
        }

    /**
     * The currently selected greenhouse object, if any.
     */
    val selectedGreenhouse: Greenhouse?
        get() = selectedGreenhouseId?.let { id -> greenhouses.find { it.id == id } }

    /**
     * The currently selected sector object, if any.
     */
    val selectedSector: Sector?
        get() = selectedSectorId?.let { id -> sectors.find { it.id == id } }
}

/**
 * Enum representing the available tabs in the client detail screen.
 */
enum class ClientDetailTab {
    GENERAL,
    USERS,
    GREENHOUSES,
    ALERTS
}

/**
 * Sub-tabs within the sector detail view inside the Greenhouses hierarchy.
 */
enum class SectorSubTab {
    DEVICES, ALERTS, SETTINGS
}

