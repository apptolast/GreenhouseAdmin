package com.apptolast.greenhouse.admin.presentation.viewmodel

import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatusFilter

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
     * User changed location filter.
     */
    data class OnLocationFilterChanged(val location: String?) : ClientsEvent

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
     * User toggled client selection.
     */
    data class OnClientSelectionToggled(val clientId: String) : ClientsEvent

    /**
     * User toggled select all.
     */
    data object OnSelectAllToggled : ClientsEvent

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

    /**
     * User clicked on alert icon.
     */
    data object OnAlertIconClicked : ClientsEvent
}
