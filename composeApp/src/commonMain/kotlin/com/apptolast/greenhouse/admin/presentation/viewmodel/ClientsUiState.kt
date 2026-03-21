package com.apptolast.greenhouse.admin.presentation.viewmodel

import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatusFilter
import com.apptolast.greenhouse.admin.data.model.PaginationInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.search.SearchNavigationTarget
import com.apptolast.greenhouse.admin.presentation.ui.components.common.search.SearchResult
import com.apptolast.greenhouse.admin.presentation.ui.components.common.search.SearchResultCategory

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
    val selectedMenuId: String = "clients",
    val topBarSearchQuery: String = "",

    // Data states
    val clients: List<Client> = emptyList(),
    val provinces: List<String> = emptyList(),
    val countries: List<String> = emptyList(),

    // Filter states
    val searchQuery: String = "",
    val statusFilter: ClientStatusFilter = ClientStatusFilter.ALL,
    val provinceFilter: String? = null,

    // Pagination
    val pagination: PaginationInfo = PaginationInfo(),

    // Create dialog states
    val showNewClientDialog: Boolean = false,
    val isCreatingClient: Boolean = false,
    val createClientError: String? = null,

    // Edit dialog states
    val showEditClientDialog: Boolean = false,
    val clientToEdit: Client? = null,
    val isUpdatingClient: Boolean = false,
    val updateClientError: String? = null,

    // Delete dialog states
    val showDeleteConfirmation: Boolean = false,
    val clientToDelete: Client? = null,
    val isDeletingClient: Boolean = false,
    val deleteClientError: String? = null
) {
    /**
     * Returns filtered clients based on current filters.
     */
    val filteredClients: List<Client>
        get() = clients.filter { client ->
            val matchesSearch = searchQuery.isEmpty() ||
                    client.name.contains(searchQuery, ignoreCase = true) ||
                    client.email.contains(searchQuery, ignoreCase = true)

            val matchesStatus = statusFilter.matches(client.status)

            val matchesProvince = provinceFilter == null ||
                    client.province == provinceFilter

            matchesSearch && matchesStatus && matchesProvince
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
     * Search results for the top bar autocomplete.
     * Searches clients by name, email, or code.
     */
    val topBarSearchResults: List<SearchResult>
        get() {
            if (topBarSearchQuery.length < 2) return emptyList()
            val query = topBarSearchQuery
            return clients.filter { client ->
                client.name.contains(query, ignoreCase = true) ||
                        client.email.contains(query, ignoreCase = true) ||
                        client.code.contains(query, ignoreCase = true)
            }.take(10).map { client ->
                SearchResult(
                    id = "client-${client.id}",
                    category = SearchResultCategory.CLIENT,
                    title = client.name,
                    subtitle = client.email,
                    code = client.code,
                    navigationTarget = SearchNavigationTarget.ToClient(client.id)
                )
            }
        }
}
