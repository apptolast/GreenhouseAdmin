package com.apptolast.greenhouse.admin.presentation.viewmodel

import com.apptolast.greenhouse.admin.data.model.ClientStatus
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
        val location: String,
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
}
