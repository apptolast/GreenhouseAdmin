package com.apptolast.greenhouse.admin.presentation.viewmodel

import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.MenuItem
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
    val deleteUserError: String? = null
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
