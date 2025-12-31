package com.apptolast.greenhouse.admin.presentation.viewmodel

/**
 * Sealed interface representing all possible user intents/events on the Dashboard.
 * Following MVI pattern for unidirectional data flow.
 */
sealed interface DashboardEvent {
    /**
     * Initial load of dashboard data.
     */
    data object LoadDashboard : DashboardEvent

    /**
     * Pull-to-refresh or manual refresh.
     */
    data object RefreshDashboard : DashboardEvent

    /**
     * User typed in the search field.
     */
    data class OnSearchQueryChanged(val query: String) : DashboardEvent

    /**
     * User selected a menu item from sidebar.
     */
    data class OnMenuItemSelected(val itemId: String) : DashboardEvent

    /**
     * User clicked the alert notification icon.
     */
    data object OnAlertIconClicked : DashboardEvent

    /**
     * Dismiss current error message.
     */
    data object DismissError : DashboardEvent
}
