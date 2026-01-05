package com.apptolast.greenhouse.admin.presentation.viewmodel

import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.AlertSeverityCatalog
import com.apptolast.greenhouse.admin.data.model.AlertType
import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogCategory
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogType
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogUnit
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.MenuItem
import com.apptolast.greenhouse.admin.data.model.Period
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
    val menuItems: List<MenuItem> = emptyList(),
    val selectedMenuId: String = "clients",
    val alertCount: Int = 0,
    val topBarSearchQuery: String = "",

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
    val userFormMode: UserFormMode = UserFormMode.Create,
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
    val greenhouseFormMode: GreenhouseFormMode = GreenhouseFormMode.Create,
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
    val sectorFormMode: SectorFormMode = SectorFormMode.Create,
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
    val deviceFormMode: DeviceFormMode = DeviceFormMode.Create,
    val isSubmittingDevice: Boolean = false,
    val submitDeviceError: String? = null,

    // Device delete dialog states
    val showDeleteDeviceConfirmation: Boolean = false,
    val deviceToDelete: Device? = null,
    val isDeletingDevice: Boolean = false,
    val deleteDeviceError: String? = null,

    // Device catalog state (for form dropdowns)
    val deviceCategories: List<DeviceCatalogCategory> = emptyList(),
    val deviceTypes: List<DeviceCatalogType> = emptyList(),
    val deviceUnits: List<DeviceCatalogUnit> = emptyList(),
    val isLoadingDeviceCatalog: Boolean = false,

    // Alerts tab state
    val alerts: List<Alert> = emptyList(),
    val isLoadingAlerts: Boolean = false,
    val alertsError: String? = null,

    // Alert form dialog states
    val showAlertFormDialog: Boolean = false,
    val alertFormMode: AlertFormMode = AlertFormMode.Create,
    val isSubmittingAlert: Boolean = false,
    val submitAlertError: String? = null,

    // Alert delete dialog states
    val showDeleteAlertConfirmation: Boolean = false,
    val alertToDelete: Alert? = null,
    val isDeletingAlert: Boolean = false,
    val deleteAlertError: String? = null,

    // Alert catalog state (for form dropdowns)
    val alertTypes: List<AlertType> = emptyList(),
    val alertSeverities: List<AlertSeverityCatalog> = emptyList(),
    val isLoadingAlertCatalog: Boolean = false,

    // Settings tab state
    val settings: List<Setting> = emptyList(),
    val isLoadingSettings: Boolean = false,
    val settingsError: String? = null,

    // Setting form dialog states
    val showSettingFormDialog: Boolean = false,
    val settingFormMode: SettingFormMode = SettingFormMode.Create,
    val isSubmittingSetting: Boolean = false,
    val submitSettingError: String? = null,

    // Setting delete dialog states
    val showDeleteSettingConfirmation: Boolean = false,
    val settingToDelete: Setting? = null,
    val isDeletingSetting: Boolean = false,
    val deleteSettingError: String? = null,

    // Setting catalog state (for form dropdowns)
    val periods: List<Period> = emptyList(),
    val isLoadingSettingCatalog: Boolean = false
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
}

/**
 * Enum representing the available tabs in the client detail screen.
 */
enum class ClientDetailTab {
    GENERAL,
    USERS,
    GREENHOUSES,
    SECTORS,
    DEVICES,
    ALERTS,
    SETTINGS
}

/**
 * Mode for the user form dialog.
 */
sealed interface UserFormMode {
    data object Create : UserFormMode
    data class Edit(val user: User) : UserFormMode
}

/**
 * Mode for the greenhouse form dialog.
 */
sealed interface GreenhouseFormMode {
    data object Create : GreenhouseFormMode
    data class Edit(val greenhouse: Greenhouse) : GreenhouseFormMode
}

/**
 * Mode for the sector form dialog.
 */
sealed interface SectorFormMode {
    data object Create : SectorFormMode
    data class Edit(val sector: Sector) : SectorFormMode
}

/**
 * Mode for the device form dialog.
 */
sealed interface DeviceFormMode {
    data object Create : DeviceFormMode
    data class Edit(val device: Device) : DeviceFormMode
}

/**
 * Mode for the alert form dialog.
 */
sealed interface AlertFormMode {
    data object Create : AlertFormMode
    data class Edit(val alert: Alert) : AlertFormMode
}

/**
 * Mode for the setting form dialog.
 */
sealed interface SettingFormMode {
    data object Create : SettingFormMode
    data class Edit(val setting: Setting) : SettingFormMode
}
