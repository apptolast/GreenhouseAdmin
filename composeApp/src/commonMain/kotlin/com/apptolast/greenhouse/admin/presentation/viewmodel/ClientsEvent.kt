package com.apptolast.greenhouse.admin.presentation.viewmodel

import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.data.model.ClientStatusFilter
import com.apptolast.greenhouse.admin.data.model.Location

/**
 * Sealed interface representing all possible user intents/events on the Clients screen.
 * Following MVI pattern for unidirectional data flow.
 */
sealed interface ClientsEvent {
    /**
     * Initial load of clients data.
     */
    data object LoadClients : ClientsEvent

    /**
     * Pull-to-refresh or manual refresh.
     */
    data object RefreshClients : ClientsEvent

    /**
     * User typed in the search field.
     */
    data class OnSearchQueryChanged(val query: String) : ClientsEvent

    /**
     * User changed status filter.
     */
    data class OnStatusFilterChanged(val filter: ClientStatusFilter) : ClientsEvent

    /**
     * User changed province filter.
     */
    data class OnProvinceFilterChanged(val province: String?) : ClientsEvent

    /**
     * User clicked on a client row.
     */
    data class OnClientClicked(val client: Client) : ClientsEvent

    /**
     * User clicked edit action on a client.
     */
    data class OnEditClientClicked(val client: Client) : ClientsEvent

    /**
     * User clicked delete action on a client.
     */
    data class OnDeleteClientClicked(val client: Client) : ClientsEvent

    /**
     * User confirmed delete action.
     */
    data object OnConfirmDelete : ClientsEvent

    /**
     * User cancelled delete action.
     */
    data object OnCancelDelete : ClientsEvent

    /**
     * User clicked new client button.
     */
    data object OnNewClientClicked : ClientsEvent

    /**
     * User dismissed the new client dialog.
     */
    data object OnDismissNewClientDialog : ClientsEvent

    /**
     * User submitted the new client form.
     */
    data class OnSubmitNewClient(
        val name: String,
        val email: String,
        val phone: String,
        val province: String,
        val country: String,
        val location: Location?,
        val status: ClientStatus
    ) : ClientsEvent

    /**
     * User dismissed the edit client dialog.
     */
    data object OnDismissEditClientDialog : ClientsEvent

    /**
     * User submitted the edit client form.
     */
    data class OnSubmitEditClient(
        val id: Long,
        val name: String,
        val email: String,
        val phone: String,
        val province: String,
        val country: String,
        val location: Location?,
        val status: ClientStatus
    ) : ClientsEvent

    /**
     * User changed page.
     */
    data class OnPageChanged(val page: Int) : ClientsEvent

    /**
     * User changed page size.
     */
    data class OnPageSizeChanged(val size: Int) : ClientsEvent

    /**
     * Dismiss current error message.
     */
    data object DismissError : ClientsEvent

    /**
     * User selected a menu item in the sidebar.
     */
    data class OnMenuItemSelected(val itemId: String) : ClientsEvent

    /**
     * User typed in the topbar search field.
     */
    data class OnTopBarSearchQueryChanged(val query: String) : ClientsEvent
}
