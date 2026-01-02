package com.apptolast.greenhouse.admin.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.AdaptiveDimens
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientDetailGeneralTab
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientDetailHeader
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientDetailTabBar
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientDetailUsersTab
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientFormDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.ClientFormMode
import com.apptolast.greenhouse.admin.presentation.ui.components.ComingSoonContent
import com.apptolast.greenhouse.admin.presentation.ui.components.DashboardTopBar
import com.apptolast.greenhouse.admin.presentation.ui.components.DeleteConfirmationDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.components.LoadingContent
import com.apptolast.greenhouse.admin.presentation.ui.components.UserFormDialog
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailEvent
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailTab
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailUiState
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailViewModel
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.app_name
import greenhouseadmin.composeapp.generated.resources.breadcrumb_clients
import greenhouseadmin.composeapp.generated.resources.error_unknown
import greenhouseadmin.composeapp.generated.resources.new_user
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
 * Navigation is handled by AdaptiveScaffold at the app level.
 */
@Composable
fun ClientDetailScreen(
    clientId: String,
    onNavigateBack: () -> Unit,
    viewModel: ClientDetailViewModel = koinViewModel { parametersOf(clientId) }
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation after successful delete
    LaunchedEffect(uiState.shouldNavigateBack) {
        if (uiState.shouldNavigateBack) {
            viewModel.onEvent(ClientDetailEvent.OnNavigationHandled)
            onNavigateBack()
        }
    }

    ClientDetailScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

/**
 * Stateless client detail screen content composable.
 */
@Composable
private fun ClientDetailScreenContent(
    uiState: ClientDetailUiState,
    onEvent: (ClientDetailEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val client = uiState.client

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                        onNavigateBack = onNavigateBack
                    )
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

    // User Form Dialog
    if (uiState.showUserFormDialog) {
        UserFormDialog(
            mode = uiState.userFormMode,
            isSubmitting = uiState.isSubmittingUser,
            error = uiState.submitUserError,
            onSubmit = { name, email, phone ->
                onEvent(ClientDetailEvent.OnSubmitUserForm(name, email, phone))
            },
            onDismiss = { onEvent(ClientDetailEvent.OnDismissUserFormDialog) }
        )
    }

    // User Delete Confirmation Dialog
    if (uiState.showDeleteUserConfirmation && uiState.userToDelete != null) {
        DeleteConfirmationDialog(
            clientName = uiState.userToDelete.name,
            isDeleting = uiState.isDeletingUser,
            error = uiState.deleteUserError,
            onConfirm = { onEvent(ClientDetailEvent.OnConfirmDeleteUser) },
            onDismiss = { onEvent(ClientDetailEvent.OnCancelDeleteUser) }
        )
    }
}

@Composable
private fun ClientDetailContent(
    uiState: ClientDetailUiState,
    client: com.apptolast.greenhouse.admin.data.model.Client,
    onEvent: (ClientDetailEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val contentPadding = AdaptiveDimens.contentPadding()
    val windowInfo = LocalAppWindowInfo.current

    // Show FAB on compact screens when on Users tab
    val showFab = windowInfo.isCompact && uiState.selectedTab == ClientDetailTab.USERS

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with client info and actions
            ClientDetailHeader(
                client = client,
                onBackClick = onNavigateBack,
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

                ClientDetailTab.USERS -> {
                    ClientDetailUsersTab(
                        users = uiState.users,
                        isLoading = uiState.isLoadingUsers,
                        error = uiState.usersError,
                        onAddUser = { onEvent(ClientDetailEvent.OnAddUserClicked) },
                        onEditUser = { user -> onEvent(ClientDetailEvent.OnEditUserClicked(user)) },
                        onDeleteUser = { user -> onEvent(ClientDetailEvent.OnDeleteUserClicked(user)) },
                        onRetry = { onEvent(ClientDetailEvent.LoadUsers) }
                    )
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

        // FAB for adding users on compact screens
        if (showFab) {
            FloatingActionButton(
                onClick = { onEvent(ClientDetailEvent.OnAddUserClicked) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.new_user)
                )
            }
        }
    }
}
