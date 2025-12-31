package com.apptolast.greenhouse.admin.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientDetailGeneralTab
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientDetailHeader
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientDetailTabBar
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientFormDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientFormMode
import com.apptolast.greenhouse.admin.presentation.ui.components.ComingSoonContent
import com.apptolast.greenhouse.admin.presentation.ui.components.DashboardTopBar
import com.apptolast.greenhouse.admin.presentation.ui.components.DeleteConfirmationDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.components.LoadingContent
import com.apptolast.greenhouse.admin.presentation.ui.components.SidebarNavigation
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailEvent
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailTab
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailUiState
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailViewModel
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.app_name
import greenhouseadmin.composeapp.generated.resources.breadcrumb_clients
import greenhouseadmin.composeapp.generated.resources.error_unknown
import greenhouseadmin.composeapp.generated.resources.tab_alerts
import greenhouseadmin.composeapp.generated.resources.tab_devices
import greenhouseadmin.composeapp.generated.resources.tab_greenhouses
import greenhouseadmin.composeapp.generated.resources.tab_sectors
import greenhouseadmin.composeapp.generated.resources.tab_settings
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Client Detail screen composable.
 * Follows MVI pattern - receives ViewModel from Koin with clientId parameter.
 */
@Composable
fun ClientDetailScreen(
    clientId: String,
    onNavigate: (String) -> Unit,
    viewModel: ClientDetailViewModel = koinViewModel { parametersOf(clientId) }
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation after successful delete
    LaunchedEffect(uiState.shouldNavigateBack) {
        if (uiState.shouldNavigateBack) {
            viewModel.onEvent(ClientDetailEvent.OnNavigationHandled)
            onNavigate("clients")
        }
    }

    ClientDetailScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigate = onNavigate
    )
}

/**
 * Stateless client detail screen content composable.
 * Maintains the same layout as other screens with sidebar and topbar.
 */
@Composable
private fun ClientDetailScreenContent(
    uiState: ClientDetailUiState,
    onEvent: (ClientDetailEvent) -> Unit,
    onNavigate: (String) -> Unit
) {
    val client = uiState.client

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
                onEvent(ClientDetailEvent.OnMenuItemSelected(itemId))
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
            val breadcrumb = if (client != null) {
                "${stringResource(Res.string.breadcrumb_clients)} / ${client.name}"
            } else {
                stringResource(Res.string.breadcrumb_clients)
            }

            DashboardTopBar(
                title = stringResource(Res.string.app_name),
                subtitle = breadcrumb,
                searchQuery = uiState.topBarSearchQuery,
                alertCount = uiState.alertCount,
                onSearchQueryChange = { onEvent(ClientDetailEvent.OnTopBarSearchQueryChanged(it)) },
                onAlertClick = { onEvent(ClientDetailEvent.OnAlertIconClicked) }
            )

            // Content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                when {
                    uiState.isLoading -> {
                        LoadingContent()
                    }

                    uiState.isError -> {
                        ErrorContent(
                            message = uiState.error ?: stringResource(Res.string.error_unknown),
                            onRetry = { onEvent(ClientDetailEvent.LoadClient) }
                        )
                    }

                    client != null -> {
                        ClientDetailContent(
                            uiState = uiState,
                            client = client,
                            onEvent = onEvent,
                            onNavigate = onNavigate
                        )
                    }
                }
            }
        }
    }

    // Edit Client Dialog
    if (uiState.showEditClientDialog && client != null) {
        ClientFormDialog(
            mode = ClientFormMode.Edit(client),
            provinces = listOf(client.province), // Use current value as option
            countries = listOf(client.country), // Use current value as option
            isSubmitting = uiState.isUpdatingClient,
            error = uiState.updateClientError,
            onSubmit = { _, name, email, phone, province, country, location, status ->
                onEvent(
                    ClientDetailEvent.OnSubmitEdit(
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
            onDismiss = { onEvent(ClientDetailEvent.OnDismissEditDialog) }
        )
    }

    // Delete Confirmation Dialog
    if (uiState.showDeleteConfirmation && client != null) {
        DeleteConfirmationDialog(
            clientName = client.name,
            isDeleting = uiState.isDeletingClient,
            error = uiState.deleteClientError,
            onConfirm = { onEvent(ClientDetailEvent.OnConfirmDelete) },
            onDismiss = { onEvent(ClientDetailEvent.OnCancelDelete) }
        )
    }
}

@Composable
private fun ClientDetailContent(
    uiState: ClientDetailUiState,
    client: com.apptolast.greenhouse.admin.data.model.Client,
    onEvent: (ClientDetailEvent) -> Unit,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with client info and actions
        ClientDetailHeader(
            client = client,
            onBackClick = { onNavigate("back") },
            onEditClick = { onEvent(ClientDetailEvent.OnEditClicked) },
            onDeleteClick = { onEvent(ClientDetailEvent.OnDeleteClicked) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tab bar
        ClientDetailTabBar(
            selectedTab = uiState.selectedTab,
            onTabSelected = { onEvent(ClientDetailEvent.OnTabSelected(it)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tab content
        when (uiState.selectedTab) {
            ClientDetailTab.GENERAL -> {
                ClientDetailGeneralTab(client = client)
            }

            ClientDetailTab.GREENHOUSES -> {
                ComingSoonContent(tabName = stringResource(Res.string.tab_greenhouses))
            }

            ClientDetailTab.SECTORS -> {
                ComingSoonContent(tabName = stringResource(Res.string.tab_sectors))
            }

            ClientDetailTab.DEVICES -> {
                ComingSoonContent(tabName = stringResource(Res.string.tab_devices))
            }

            ClientDetailTab.ALERTS -> {
                ComingSoonContent(tabName = stringResource(Res.string.tab_alerts))
            }

            ClientDetailTab.SETTINGS -> {
                ComingSoonContent(tabName = stringResource(Res.string.tab_settings))
            }
        }
    }
}
