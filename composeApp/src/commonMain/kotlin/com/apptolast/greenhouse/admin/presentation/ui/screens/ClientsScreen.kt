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
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientFormDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientFormMode
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientsFilters
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientsPagination
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientsTable
import com.apptolast.greenhouse.admin.presentation.ui.components.DashboardTopBar
import com.apptolast.greenhouse.admin.presentation.ui.components.DeleteConfirmationDialog
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
                onNavigate = onNavigate,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
        }
    }

    // Create Client Dialog
    if (uiState.showNewClientDialog) {
        ClientFormDialog(
            mode = ClientFormMode.Create,
            provinces = uiState.provinces,
            countries = uiState.countries,
            isSubmitting = uiState.isCreatingClient,
            error = uiState.createClientError,
            onSubmit = { _, name, email, phone, province, country, location, status ->
                onEvent(
                    ClientsEvent.OnSubmitNewClient(
                        name = name,
                        email = email,
                        phone = phone,
                        province = province,
                        country = country,
                        location = location,
                        status = status
                    )
                )
            },
            onDismiss = { onEvent(ClientsEvent.OnDismissNewClientDialog) }
        )
    }

    // Edit Client Dialog
    if (uiState.showEditClientDialog && uiState.clientToEdit != null) {
        ClientFormDialog(
            mode = ClientFormMode.Edit(uiState.clientToEdit),
            provinces = uiState.provinces,
            countries = uiState.countries,
            isSubmitting = uiState.isUpdatingClient,
            error = uiState.updateClientError,
            onSubmit = { id, name, email, phone, province, country, location, status ->
                onEvent(
                    ClientsEvent.OnSubmitEditClient(
                        id = id ?: return@ClientFormDialog,
                        name = name,
                        email = email,
                        phone = phone,
                        province = province,
                        country = country,
                        location = location,
                        status = status
                    )
                )
            },
            onDismiss = { onEvent(ClientsEvent.OnDismissEditClientDialog) }
        )
    }

    // Delete Confirmation Dialog
    if (uiState.showDeleteConfirmation && uiState.clientToDelete != null) {
        DeleteConfirmationDialog(
            clientName = uiState.clientToDelete.name,
            isDeleting = uiState.isDeletingClient,
            error = uiState.deleteClientError,
            onConfirm = { onEvent(ClientsEvent.OnConfirmDelete) },
            onDismiss = { onEvent(ClientsEvent.OnCancelDelete) }
        )
    }
}

/**
 * Central content area for clients list.
 */
@Composable
private fun ClientsContent(
    uiState: ClientsUiState,
    onEvent: (ClientsEvent) -> Unit,
    onNavigate: (String) -> Unit,
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
            provinceFilter = uiState.provinceFilter,
            provinces = uiState.provinces,
            onSearchQueryChanged = { onEvent(ClientsEvent.OnSearchQueryChanged(it)) },
            onStatusFilterChanged = { onEvent(ClientsEvent.OnStatusFilterChanged(it)) },
            onProvinceFilterChanged = { onEvent(ClientsEvent.OnProvinceFilterChanged(it)) },
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
                            onClientClicked = { client ->
                                onNavigate("client_detail/${client.id}")
                            },
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
