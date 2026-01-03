package com.apptolast.greenhouse.admin.presentation.viewmodel

import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.AlertSeverity
import com.apptolast.greenhouse.admin.data.model.AlertStatus
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.DeviceStatus
import com.apptolast.greenhouse.admin.data.model.DeviceType
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.GreenhouseStatus
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.data.model.User

/**
 * Sealed interface representing all possible user intents/events on the Client Detail screen.
 * Following MVI pattern for unidirectional data flow.
 */
sealed interface ClientDetailEvent {
    /**
     * Load client data by ID.
     */
    data object LoadClient : ClientDetailEvent

    /**
     * User selected a different tab.
     */
    data class OnTabSelected(val tab: ClientDetailTab) : ClientDetailEvent

    /**
     * User clicked back button.
     */
    data object OnBackClicked : ClientDetailEvent

    /**
     * User clicked edit button.
     */
    data object OnEditClicked : ClientDetailEvent

    /**
     * User clicked delete button.
     */
    data object OnDeleteClicked : ClientDetailEvent

    /**
     * User confirmed delete action.
     */
    data object OnConfirmDelete : ClientDetailEvent

    /**
     * User cancelled delete action.
     */
    data object OnCancelDelete : ClientDetailEvent

    /**
     * User dismissed the edit dialog.
     */
    data object OnDismissEditDialog : ClientDetailEvent

    /**
     * User submitted the edit form.
     */
    data class OnSubmitEdit(
        val name: String,
        val email: String,
        val phone: String,
        val province: String,
        val country: String,
        val address: String,
        val status: ClientStatus
    ) : ClientDetailEvent

    /**
     * User selected a menu item in the sidebar.
     */
    data class OnMenuItemSelected(val itemId: String) : ClientDetailEvent

    /**
     * User typed in the topbar search field.
     */
    data class OnTopBarSearchQueryChanged(val query: String) : ClientDetailEvent

    /**
     * User clicked on alert icon.
     */
    data object OnAlertIconClicked : ClientDetailEvent

    /**
     * Navigation completed, reset navigation flag.
     */
    data object OnNavigationHandled : ClientDetailEvent

    // === Users Tab Events ===

    /**
     * Load users for the current client.
     */
    data object LoadUsers : ClientDetailEvent

    /**
     * User clicked "Add User" button.
     */
    data object OnAddUserClicked : ClientDetailEvent

    /**
     * User clicked edit on a specific user.
     */
    data class OnEditUserClicked(val user: User) : ClientDetailEvent

    /**
     * User clicked delete on a specific user.
     */
    data class OnDeleteUserClicked(val user: User) : ClientDetailEvent

    /**
     * User confirmed delete action for a user.
     */
    data object OnConfirmDeleteUser : ClientDetailEvent

    /**
     * User cancelled delete action for a user.
     */
    data object OnCancelDeleteUser : ClientDetailEvent

    /**
     * User dismissed the user form dialog.
     */
    data object OnDismissUserFormDialog : ClientDetailEvent

    /**
     * User submitted the user form (create or edit).
     */
    data class OnSubmitUserForm(
        val name: String,
        val email: String,
        val phone: String
    ) : ClientDetailEvent

    // === Greenhouses Tab Events ===

    /**
     * Load greenhouses for the current client.
     */
    data object LoadGreenhouses : ClientDetailEvent

    /**
     * User clicked "Add Greenhouse" button.
     */
    data object OnAddGreenhouseClicked : ClientDetailEvent

    /**
     * User clicked edit on a specific greenhouse.
     */
    data class OnEditGreenhouseClicked(val greenhouse: Greenhouse) : ClientDetailEvent

    /**
     * User clicked delete on a specific greenhouse.
     */
    data class OnDeleteGreenhouseClicked(val greenhouse: Greenhouse) : ClientDetailEvent

    /**
     * User confirmed delete action for a greenhouse.
     */
    data object OnConfirmDeleteGreenhouse : ClientDetailEvent

    /**
     * User cancelled delete action for a greenhouse.
     */
    data object OnCancelDeleteGreenhouse : ClientDetailEvent

    /**
     * User dismissed the greenhouse form dialog.
     */
    data object OnDismissGreenhouseFormDialog : ClientDetailEvent

    /**
     * User submitted the greenhouse form (create or edit).
     */
    data class OnSubmitGreenhouseForm(
        val name: String,
        val description: String,
        val status: GreenhouseStatus
    ) : ClientDetailEvent

    // === Sectors Tab Events ===

    /**
     * Load sectors for the current client.
     */
    data object LoadSectors : ClientDetailEvent

    /**
     * User clicked "Add Sector" button.
     */
    data object OnAddSectorClicked : ClientDetailEvent

    /**
     * User clicked edit on a specific sector.
     */
    data class OnEditSectorClicked(val sector: Sector) : ClientDetailEvent

    /**
     * User clicked delete on a specific sector.
     */
    data class OnDeleteSectorClicked(val sector: Sector) : ClientDetailEvent

    /**
     * User confirmed delete action for a sector.
     */
    data object OnConfirmDeleteSector : ClientDetailEvent

    /**
     * User cancelled delete action for a sector.
     */
    data object OnCancelDeleteSector : ClientDetailEvent

    /**
     * User dismissed the sector form dialog.
     */
    data object OnDismissSectorFormDialog : ClientDetailEvent

    /**
     * User submitted the sector form (create or edit).
     */
    data class OnSubmitSectorForm(
        val name: String,
        val greenhouseId: String,
        val greenhouseName: String,
        val area: Double
    ) : ClientDetailEvent

    // === Devices Tab Events ===

    /**
     * Load devices for the current client.
     */
    data object LoadDevices : ClientDetailEvent

    /**
     * User clicked "Add Device" button.
     */
    data object OnAddDeviceClicked : ClientDetailEvent

    /**
     * User clicked edit on a specific device.
     */
    data class OnEditDeviceClicked(val device: Device) : ClientDetailEvent

    /**
     * User clicked delete on a specific device.
     */
    data class OnDeleteDeviceClicked(val device: Device) : ClientDetailEvent

    /**
     * User confirmed delete action for a device.
     */
    data object OnConfirmDeleteDevice : ClientDetailEvent

    /**
     * User cancelled delete action for a device.
     */
    data object OnCancelDeleteDevice : ClientDetailEvent

    /**
     * User dismissed the device form dialog.
     */
    data object OnDismissDeviceFormDialog : ClientDetailEvent

    /**
     * User submitted the device form (create or edit).
     */
    data class OnSubmitDeviceForm(
        val name: String,
        val type: DeviceType,
        val status: DeviceStatus
    ) : ClientDetailEvent

    // === Alerts Tab Events ===

    /**
     * Load alerts for the current client.
     */
    data object LoadAlerts : ClientDetailEvent

    /**
     * User clicked "Add Alert" button.
     */
    data object OnAddAlertClicked : ClientDetailEvent

    /**
     * User clicked edit on a specific alert.
     */
    data class OnEditAlertClicked(val alert: Alert) : ClientDetailEvent

    /**
     * User clicked delete on a specific alert.
     */
    data class OnDeleteAlertClicked(val alert: Alert) : ClientDetailEvent

    /**
     * User confirmed delete action for an alert.
     */
    data object OnConfirmDeleteAlert : ClientDetailEvent

    /**
     * User cancelled delete action for an alert.
     */
    data object OnCancelDeleteAlert : ClientDetailEvent

    /**
     * User dismissed the alert form dialog.
     */
    data object OnDismissAlertFormDialog : ClientDetailEvent

    /**
     * User submitted the alert form (create or edit).
     */
    data class OnSubmitAlertForm(
        val title: String,
        val severity: AlertSeverity,
        val status: AlertStatus
    ) : ClientDetailEvent

    // === Settings Tab Events ===

    /**
     * Load settings for the current client.
     */
    data object LoadSettings : ClientDetailEvent

    /**
     * User clicked "Add Setting" button.
     */
    data object OnAddSettingClicked : ClientDetailEvent

    /**
     * User clicked edit on a specific setting.
     */
    data class OnEditSettingClicked(val setting: Setting) : ClientDetailEvent

    /**
     * User clicked delete on a specific setting.
     */
    data class OnDeleteSettingClicked(val setting: Setting) : ClientDetailEvent

    /**
     * User confirmed delete action for a setting.
     */
    data object OnConfirmDeleteSetting : ClientDetailEvent

    /**
     * User cancelled delete action for a setting.
     */
    data object OnCancelDeleteSetting : ClientDetailEvent

    /**
     * User dismissed the setting form dialog.
     */
    data object OnDismissSettingFormDialog : ClientDetailEvent

    /**
     * User submitted the setting form (create or edit).
     */
    data class OnSubmitSettingForm(
        val key: String,
        val value: String,
        val description: String
    ) : ClientDetailEvent
}
