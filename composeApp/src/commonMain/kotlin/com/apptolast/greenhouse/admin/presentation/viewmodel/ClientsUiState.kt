package com.apptolast.greenhouse.admin.presentation.viewmodel

import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatusFilter
import com.apptolast.greenhouse.admin.data.model.MenuItem
import com.apptolast.greenhouse.admin.data.model.PaginationInfo

/**
 * Represents the complete UI state for the Clients screen.
 * Follows MVI pattern with immutable state.
 */
data class ClientsUiState(
    // Loading states
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,

    // Error state
    val error: String? = null,

    // Navigation/Layout states
    val menuItems: List<MenuItem> = emptyList(),
    val selectedMenuId: String = "clients",
    val alertCount: Int = 0,
    val topBarSearchQuery: String = "",

    // Data states
    val clients: List<Client> = emptyList(),
    val locations: List<String> = emptyList(),

    // Filter states
    val searchQuery: String = "",
    val statusFilter: ClientStatusFilter = ClientStatusFilter.ALL,
    val locationFilter: String? = null,

    // Pagination
    val pagination: PaginationInfo = PaginationInfo(),

    // Selection state (for bulk actions)
    val selectedClientIds: Set<String> = emptySet(),

    // Dialog states
    val showDeleteConfirmation: Boolean = false,
    val clientToDelete: Client? = null
) {
    /**
     * Returns filtered clients based on current filters.
     */
    val filteredClients: List<Client>
        get() = clients.filter { client ->
            val matchesSearch = searchQuery.isEmpty() ||
                    client.fullName.contains(searchQuery, ignoreCase = true) ||
                    client.company.contains(searchQuery, ignoreCase = true) ||
                    client.email.contains(searchQuery, ignoreCase = true)

            val matchesStatus = statusFilter.matches(client.status)

            val matchesLocation = locationFilter == null ||
                    client.country == locationFilter

            matchesSearch && matchesStatus && matchesLocation
        }

    /**
     * Returns paginated clients for display.
     */
    val paginatedClients: List<Client>
        get() {
            val filtered = filteredClients
            val start = pagination.startIndex.coerceAtMost(filtered.size)
            val end = pagination.endIndex.coerceAtMost(filtered.size)
            return if (start < end) filtered.subList(start, end) else emptyList()
        }

    /**
     * Updated pagination info with filtered total.
     */
    val currentPagination: PaginationInfo
        get() = pagination.copy(totalItems = filteredClients.size)

    /**
     * Returns true if any content is available to display.
     */
    val hasContent: Boolean
        get() = clients.isNotEmpty()

    /**
     * Returns true if in error state with no content.
     */
    val isError: Boolean
        get() = error != null && !hasContent

    /**
     * Returns true if all visible clients are selected.
     */
    val allSelected: Boolean
        get() = paginatedClients.isNotEmpty() &&
                paginatedClients.all { it.id in selectedClientIds }
}
