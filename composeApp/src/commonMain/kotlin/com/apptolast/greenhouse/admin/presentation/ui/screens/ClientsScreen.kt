package com.apptolast.greenhouse.admin.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientsFilters
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientsPagination
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientsTable
import com.apptolast.greenhouse.admin.presentation.ui.components.DashboardTopBar
import com.apptolast.greenhouse.admin.presentation.ui.components.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.components.LoadingContent
import com.apptolast.greenhouse.admin.presentation.ui.components.SidebarNavigation
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientsEvent
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientsUiState
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientsViewModel
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.app_name
import greenhouseadmin.composeapp.generated.resources.breadcrumb_clients
import greenhouseadmin.composeapp.generated.resources.clients_subtitle
import greenhouseadmin.composeapp.generated.resources.clients_title
import greenhouseadmin.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Clients screen composable.
 * Follows MVI pattern - receives ViewModel from Koin, no state parameters.
 */
@Composable
fun ClientsScreen(
    onNavigate: (String) -> Unit,
    viewModel: ClientsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ClientsScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigate = onNavigate
    )
}

/**
 * Stateless clients screen content composable.
 * Maintains the same layout as Dashboard with sidebar and topbar.
 */
@Composable
private fun ClientsScreenContent(
    uiState: ClientsUiState,
    onEvent: (ClientsEvent) -> Unit,
    onNavigate: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sidebar navigation
        SidebarNavigation(
            menuItems = uiState.menuItems,
            selectedItemId = uiState.selectedMenuId,
            onItemSelected = { itemId ->
                onEvent(ClientsEvent.OnMenuItemSelected(itemId))
                // Navigate to the selected route
                val route = uiState.menuItems.find { it.id == itemId }?.route
                if (route != null && route != "clients") {
                    onNavigate(route)
                }
            }
        )

        // Main content area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            // Top bar with breadcrumb
            DashboardTopBar(
                title = stringResource(Res.string.app_name),
                subtitle = stringResource(Res.string.breadcrumb_clients),
                searchQuery = uiState.topBarSearchQuery,
                alertCount = uiState.alertCount,
                onSearchQueryChange = { onEvent(ClientsEvent.OnTopBarSearchQueryChanged(it)) },
                onAlertClick = { onEvent(ClientsEvent.OnAlertIconClicked) }
            )

            // Clients content area
            ClientsContent(
                uiState = uiState,
                onEvent = onEvent,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
        }
    }
}

/**
 * Central content area for clients list.
 */
@Composable
private fun ClientsContent(
    uiState: ClientsUiState,
    onEvent: (ClientsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp)
    ) {
        // Header
        Text(
            text = stringResource(Res.string.clients_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.clients_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Filters
        ClientsFilters(
            searchQuery = uiState.searchQuery,
            statusFilter = uiState.statusFilter,
            locationFilter = uiState.locationFilter,
            locations = uiState.locations,
            onSearchQueryChanged = { onEvent(ClientsEvent.OnSearchQueryChanged(it)) },
            onStatusFilterChanged = { onEvent(ClientsEvent.OnStatusFilterChanged(it)) },
            onLocationFilterChanged = { onEvent(ClientsEvent.OnLocationFilterChanged(it)) },
            onNewClientClicked = { onEvent(ClientsEvent.OnNewClientClicked) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Content area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }

                uiState.isError -> {
                    ErrorContent(
                        message = uiState.error ?: stringResource(Res.string.error_unknown),
                        onRetry = { onEvent(ClientsEvent.LoadClients) }
                    )
                }

                else -> {
                    Column {
                        // Table
                        ClientsTable(
                            clients = uiState.paginatedClients,
                            selectedIds = uiState.selectedClientIds,
                            allSelected = uiState.allSelected,
                            onSelectAllToggled = { onEvent(ClientsEvent.OnSelectAllToggled) },
                            onClientSelectionToggled = { onEvent(ClientsEvent.OnClientSelectionToggled(it)) },
                            onEditClient = { onEvent(ClientsEvent.OnEditClientClicked(it)) },
                            onDeleteClient = { onEvent(ClientsEvent.OnDeleteClientClicked(it)) },
                            modifier = Modifier.weight(1f)
                        )

                        // Pagination
                        ClientsPagination(
                            pagination = uiState.currentPagination,
                            onPageChanged = { onEvent(ClientsEvent.OnPageChanged(it)) },
                            onPageSizeChanged = { onEvent(ClientsEvent.OnPageSizeChanged(it)) }
                        )
                    }
                }
            }
        }
    }
}
