package com.apptolast.greenhouse.admin.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.AdaptiveDimens
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.list.ClientsCards
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.list.ClientsFilters
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.list.ClientsPagination
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.list.ClientsTable
import com.apptolast.greenhouse.admin.presentation.ui.components.common.DashboardTopBar
import com.apptolast.greenhouse.admin.presentation.ui.components.common.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.components.common.LoadingContent
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.ClientFormDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.ClientFormMode
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.DeleteConfirmationDialog
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
 * Navigation is handled by AdaptiveScaffold at the app level.
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
 */
@Composable
private fun ClientsScreenContent(
    uiState: ClientsUiState,
    onEvent: (ClientsEvent) -> Unit,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
 * Adapts layout based on window size:
 * - Compact: Cards layout with simplified pagination
 * - Expanded: Table layout with full pagination
 */
@Composable
private fun ClientsContent(
    uiState: ClientsUiState,
    onEvent: (ClientsEvent) -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current
    val contentPadding = AdaptiveDimens.contentPadding()
    val verticalSpacing = AdaptiveDimens.verticalSpacing()

    Column(
        modifier = modifier.padding(contentPadding)
    ) {
        // Header - smaller on compact
        Text(
            text = stringResource(Res.string.clients_title),
            style = if (windowInfo.isCompact) {
                MaterialTheme.typography.headlineMedium
            } else {
                MaterialTheme.typography.headlineLarge
            },
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.clients_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(verticalSpacing))

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

        Spacer(modifier = Modifier.height(verticalSpacing))

        // Content area - adaptive based on screen size
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingContent()
                }
            }

            uiState.isError -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorContent(
                        message = uiState.error ?: stringResource(Res.string.error_unknown),
                        onRetry = { onEvent(ClientsEvent.LoadClients) }
                    )
                }
            }

            else -> {
                // Adaptive list view: Cards for compact, Table for larger screens
                if (windowInfo.isCompact) {
                    ClientsCards(
                        clients = uiState.paginatedClients,
                        onClientClicked = { client ->
                            onNavigate("client_detail/${client.id}")
                        },
                        onEditClient = { onEvent(ClientsEvent.OnEditClientClicked(it)) },
                        onDeleteClient = { onEvent(ClientsEvent.OnDeleteClientClicked(it)) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    ClientsTable(
                        clients = uiState.paginatedClients,
                        onClientClicked = { client ->
                            onNavigate("client_detail/${client.id}")
                        },
                        onEditClient = { onEvent(ClientsEvent.OnEditClientClicked(it)) },
                        onDeleteClient = { onEvent(ClientsEvent.OnDeleteClientClicked(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }

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
