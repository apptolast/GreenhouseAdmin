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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.local.ClipboardManager
import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.User
import com.apptolast.greenhouse.admin.data.model.UserRole
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.AdaptiveDimens
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailAlertsTab
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailGeneralTab
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailHeader
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailTabBar
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailUsersTab
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.greenhouse.GreenhouseHierarchicalTab
import com.apptolast.greenhouse.admin.presentation.ui.components.common.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.components.common.LoadingContent
import com.apptolast.greenhouse.admin.presentation.ui.components.common.search.SearchNavigationTarget
import com.apptolast.greenhouse.admin.presentation.ui.components.common.search.SearchableTopBar
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.AlertFormDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.ClientFormDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.ClientFormMode
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.DeleteConfirmationDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.DeviceFormDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.GreenhouseFormDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.SectorFormDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.SettingFormDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.UserFormDialog
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailEvent
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailTab
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailUiState
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailViewModel
import com.apptolast.greenhouse.admin.presentation.viewmodel.SectorSubTab
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.app_name
import greenhouseadmin.composeapp.generated.resources.breadcrumb_clients
import greenhouseadmin.composeapp.generated.resources.error_unknown
import greenhouseadmin.composeapp.generated.resources.id_copied
import greenhouseadmin.composeapp.generated.resources.new_user
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
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
    viewModel: ClientDetailViewModel = koinViewModel { parametersOf(clientId.toLongOrNull() ?: 0L) },
    clipboardManager: ClipboardManager = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val idCopiedMessage = stringResource(Res.string.id_copied)

    // Handle navigation after successful delete
    LaunchedEffect(uiState.shouldNavigateBack) {
        if (uiState.shouldNavigateBack) {
            viewModel.onEvent(ClientDetailEvent.OnNavigationHandled)
            onNavigateBack()
        }
    }

    // Callback for copying ID to clipboard
    val onCopyId: (String) -> Unit = { id ->
        clipboardManager.copyToClipboard(id)
        scope.launch {
            snackbarHostState.showSnackbar(
                message = idCopiedMessage,
                duration = SnackbarDuration.Short
            )
        }
    }

    ClientDetailScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onCopyId = onCopyId,
        snackbarHostState = snackbarHostState
    )
}

/**
 * Stateless client detail screen content composable.
 */
@Composable
private fun ClientDetailScreenContent(
    uiState: ClientDetailUiState,
    onEvent: (ClientDetailEvent) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onCopyId: (String) -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val client = uiState.client

    Box(modifier = Modifier.fillMaxSize()) {
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

            SearchableTopBar(
            title = stringResource(Res.string.app_name),
            subtitle = breadcrumb,
            searchQuery = uiState.topBarSearchQuery,
                onSearchQueryChange = { onEvent(ClientDetailEvent.OnTopBarSearchQueryChanged(it)) },
                searchResults = uiState.topBarSearchResults,
                onResultSelected = { target -> handleSearchNavigation(target, onEvent) }
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
                        onNavigateBack = onNavigateBack,
                        onCopyId = onCopyId
                    )
                }
            }
        }
        }

        // Snackbar for copy feedback
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
    }

    // Edit Client Dialog
    if (uiState.showEditClientDialog && client != null) {
        ClientFormDialog(
            mode = ClientFormMode.Edit(client),
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
            onSubmit = { username, email, password, role, isActive ->
                onEvent(ClientDetailEvent.OnSubmitUserForm(username, email, password, role, isActive))
            },
            onDismiss = { onEvent(ClientDetailEvent.OnDismissUserFormDialog) }
        )
    }

    // User Delete Confirmation Dialog
    if (uiState.showDeleteUserConfirmation && uiState.userToDelete != null) {
        DeleteConfirmationDialog(
            clientName = uiState.userToDelete.username,
            isDeleting = uiState.isDeletingUser,
            error = uiState.deleteUserError,
            onConfirm = { onEvent(ClientDetailEvent.OnConfirmDeleteUser) },
            onDismiss = { onEvent(ClientDetailEvent.OnCancelDeleteUser) }
        )
    }

    // Greenhouse Form Dialog
    if (uiState.showGreenhouseFormDialog) {
        GreenhouseFormDialog(
            mode = uiState.greenhouseFormMode,
            isSubmitting = uiState.isSubmittingGreenhouse,
            error = uiState.submitGreenhouseError,
            onSubmit = { name, location, areaM2, timezone, isActive ->
                onEvent(ClientDetailEvent.OnSubmitGreenhouseForm(name, location, areaM2, timezone, isActive))
            },
            onDismiss = { onEvent(ClientDetailEvent.OnDismissGreenhouseFormDialog) }
        )
    }

    // Greenhouse Delete Confirmation Dialog
    if (uiState.showDeleteGreenhouseConfirmation && uiState.greenhouseToDelete != null) {
        DeleteConfirmationDialog(
            clientName = uiState.greenhouseToDelete.name,
            isDeleting = uiState.isDeletingGreenhouse,
            error = uiState.deleteGreenhouseError,
            onConfirm = { onEvent(ClientDetailEvent.OnConfirmDeleteGreenhouse) },
            onDismiss = { onEvent(ClientDetailEvent.OnCancelDeleteGreenhouse) }
        )
    }

    // Sector Form Dialog
    if (uiState.showSectorFormDialog) {
        SectorFormDialog(
            mode = uiState.sectorFormMode,
            isSubmitting = uiState.isSubmittingSector,
            error = uiState.submitSectorError,
            onSubmit = { name ->
                onEvent(ClientDetailEvent.OnSubmitSectorForm(name))
            },
            onDismiss = { onEvent(ClientDetailEvent.OnDismissSectorFormDialog) }
        )
    }

    // Sector Delete Confirmation Dialog
    if (uiState.showDeleteSectorConfirmation && uiState.sectorToDelete != null) {
        DeleteConfirmationDialog(
            clientName = uiState.sectorToDelete.displayName,
            isDeleting = uiState.isDeletingSector,
            error = uiState.deleteSectorError,
            onConfirm = { onEvent(ClientDetailEvent.OnConfirmDeleteSector) },
            onDismiss = { onEvent(ClientDetailEvent.OnCancelDeleteSector) }
        )
    }

    // Device Form Dialog
    if (uiState.showDeviceFormDialog) {
        DeviceFormDialog(
            mode = uiState.deviceFormMode,
            categories = uiState.deviceCategories,
            allTypes = uiState.deviceTypes,
            units = uiState.deviceUnits,
            isLoadingCatalog = uiState.isCatalogsLoading,
            isSubmitting = uiState.isSubmittingDevice,
            error = uiState.submitDeviceError,
            onSubmit = { name, categoryId, typeId, unitId, isActive ->
                onEvent(ClientDetailEvent.OnSubmitDeviceForm(name, categoryId, typeId, unitId, isActive))
            },
            onDismiss = { onEvent(ClientDetailEvent.OnDismissDeviceFormDialog) }
        )
    }

    // Device Delete Confirmation Dialog
    if (uiState.showDeleteDeviceConfirmation && uiState.deviceToDelete != null) {
        DeleteConfirmationDialog(
            clientName = uiState.deviceToDelete.displayName,
            isDeleting = uiState.isDeletingDevice,
            error = uiState.deleteDeviceError,
            onConfirm = { onEvent(ClientDetailEvent.OnConfirmDeleteDevice) },
            onDismiss = { onEvent(ClientDetailEvent.OnCancelDeleteDevice) }
        )
    }

    // Alert Form Dialog
    if (uiState.showAlertFormDialog) {
        AlertFormDialog(
            mode = uiState.alertFormMode,
            alertTypes = uiState.alertTypes,
            severities = uiState.alertSeverities,
            isLoadingCatalog = uiState.isCatalogsLoading,
            isSubmitting = uiState.isSubmittingAlert,
            error = uiState.submitAlertError,
            onSubmit = { alertTypeId, severityId, message, description ->
                onEvent(ClientDetailEvent.OnSubmitAlertForm(alertTypeId, severityId, message, description))
            },
            onDismiss = { onEvent(ClientDetailEvent.OnDismissAlertFormDialog) }
        )
    }

    // Alert Delete Confirmation Dialog
    if (uiState.showDeleteAlertConfirmation && uiState.alertToDelete != null) {
        DeleteConfirmationDialog(
            clientName = uiState.alertToDelete.displayText,
            isDeleting = uiState.isDeletingAlert,
            error = uiState.deleteAlertError,
            onConfirm = { onEvent(ClientDetailEvent.OnConfirmDeleteAlert) },
            onDismiss = { onEvent(ClientDetailEvent.OnCancelDeleteAlert) }
        )
    }

    // Setting Form Dialog
    if (uiState.showSettingFormDialog) {
        SettingFormDialog(
            mode = uiState.settingFormMode,
            parameters = uiState.deviceTypes,
            actuatorStates = uiState.actuatorStates,
            dataTypes = uiState.dataTypes,
            isLoadingCatalog = uiState.isCatalogsLoading,
            isSubmitting = uiState.isSubmittingSetting,
            error = uiState.submitSettingError,
            onSubmit = { parameterId, actuatorStateId, dataTypeId, description, isActive ->
                onEvent(
                    ClientDetailEvent.OnSubmitSettingForm(
                        parameterId = parameterId,
                        actuatorStateId = actuatorStateId,
                        dataTypeId = dataTypeId,
                        description = description,
                        isActive = isActive
                    )
                )
            },
            onDismiss = { onEvent(ClientDetailEvent.OnDismissSettingFormDialog) }
        )
    }

    // Setting Delete Confirmation Dialog
    if (uiState.showDeleteSettingConfirmation && uiState.settingToDelete != null) {
        DeleteConfirmationDialog(
            clientName = uiState.settingToDelete.displayName,
            isDeleting = uiState.isDeletingSetting,
            error = uiState.deleteSettingError,
            onConfirm = { onEvent(ClientDetailEvent.OnConfirmDeleteSetting) },
            onDismiss = { onEvent(ClientDetailEvent.OnCancelDeleteSetting) }
        )
    }
}

@Composable
private fun ClientDetailContent(
    uiState: ClientDetailUiState,
    client: Client,
    onEvent: (ClientDetailEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onCopyId: (String) -> Unit = {}
) {
    val contentPadding = AdaptiveDimens.contentPadding()
    val windowInfo = LocalAppWindowInfo.current

    // Show FAB on compact screens when on Users tab (other tabs have their own add buttons)
    val showFab = windowInfo.isCompact && uiState.selectedTab == ClientDetailTab.USERS

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
            // No verticalScroll here — each tab handles its own scrolling.
            // This gives bounded height constraints to GreenhouseHierarchicalTab.
        ) {
            // Header with client info and actions (pinned at top)
            ClientDetailHeader(
                client = client,
                onBackClick = onNavigateBack,
                onEditClick = { onEvent(ClientDetailEvent.OnEditClicked) },
                onDeleteClick = { onEvent(ClientDetailEvent.OnDeleteClicked) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab bar (pinned at top)
            ClientDetailTabBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { onEvent(ClientDetailEvent.OnTabSelected(it)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab content — each tab fills remaining space and scrolls independently
            when (uiState.selectedTab) {
                ClientDetailTab.GENERAL -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        ClientDetailGeneralTab(client = client)
                    }
                }

                ClientDetailTab.USERS -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
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
                }

                ClientDetailTab.GREENHOUSES -> {
                    GreenhouseHierarchicalTab(
                        uiState = uiState,
                        onEvent = onEvent,
                        onCopyId = onCopyId,
                        modifier = Modifier.weight(1f)
                    )
                }

                ClientDetailTab.ALERTS -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        ClientDetailAlertsTab(
                            alerts = uiState.filteredAlerts,
                            sectors = uiState.sectors,
                            greenhouses = uiState.greenhouses,
                            isLoading = uiState.isLoadingAlerts,
                            error = uiState.alertsError,
                            onAddAlert = { onEvent(ClientDetailEvent.OnAddAlertClicked) },
                            onEditAlert = { alert -> onEvent(ClientDetailEvent.OnEditAlertClicked(alert)) },
                            onDeleteAlert = { alert -> onEvent(ClientDetailEvent.OnDeleteAlertClicked(alert)) },
                            onResolveAlert = { alert -> onEvent(ClientDetailEvent.OnResolveAlertClicked(alert)) },
                            onReopenAlert = { alert -> onEvent(ClientDetailEvent.OnReopenAlertClicked(alert)) },
                            onCopyId = onCopyId,
                            onRetry = { onEvent(ClientDetailEvent.LoadAlerts) }
                        )
                    }
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

/**
 * Handles deep navigation from search results within the client detail screen.
 * Uses existing ViewModel events to expand the correct greenhouse, select the sector, and switch tabs.
 */
private fun handleSearchNavigation(
    target: SearchNavigationTarget,
    onEvent: (ClientDetailEvent) -> Unit
) {
    when (target) {
        is SearchNavigationTarget.ToGreenhouseTab -> {
            onEvent(ClientDetailEvent.OnTabSelected(ClientDetailTab.GREENHOUSES))
            onEvent(ClientDetailEvent.OnGreenhouseExpandToggle(target.greenhouseId))
            onEvent(ClientDetailEvent.OnGreenhouseSelected(target.greenhouseId))
        }

        is SearchNavigationTarget.ToSector -> {
            onEvent(ClientDetailEvent.OnTabSelected(ClientDetailTab.GREENHOUSES))
            onEvent(ClientDetailEvent.OnGreenhouseExpandToggle(target.greenhouseId))
            onEvent(ClientDetailEvent.OnGreenhouseSelected(target.greenhouseId))
            onEvent(ClientDetailEvent.OnSectorSelected(target.sectorId))
            target.subTab?.let { subTab ->
                val sectorSubTab = when (subTab) {
                    "devices" -> SectorSubTab.DEVICES
                    "alerts" -> SectorSubTab.ALERTS
                    "settings" -> SectorSubTab.SETTINGS
                    else -> return@let
                }
                onEvent(ClientDetailEvent.OnSectorSubTabSelected(sectorSubTab))
            }
        }

        is SearchNavigationTarget.ToUsersTab -> {
            onEvent(ClientDetailEvent.OnTabSelected(ClientDetailTab.USERS))
        }

        is SearchNavigationTarget.ToClient -> {
            // Already on client detail — no-op
        }
    }
}

private object ClientDetailScreenPreviewData {
    val sampleClient = Client(
        id = 12345L,
        code = "TNT-00001",
        name = "Fresh Vegetables Co.",
        email = "contact@freshveg.com",
        phone = "+34 612 345 678",
        province = "Almeria",
        country = "Spain",
        status = ClientStatus.ACTIVE
    )

    val sampleUsers = listOf(
        User(
            id = 1L,
            code = "USR-00001",
            username = "anamartinez",
            email = "ana@freshveg.com",
            role = UserRole.ADMIN,
            tenantId = 12345L,
            isActive = true
        ),
        User(
            id = 2L,
            code = "USR-00002",
            username = "carlosruiz",
            email = "carlos@freshveg.com",
            role = UserRole.OPERATOR,
            tenantId = 12345L,
            isActive = true
        )
    )

    val sampleGreenhouses = listOf(
        Greenhouse(
            id = 1L,
            code = "GRH-00001",
            name = "Invernadero Principal",
            tenantId = 12345L,
            location = null,
            areaM2 = 1500.0,
            timezone = "Europe/Madrid",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Greenhouse(
            id = 2L,
            code = "GRH-00002",
            name = "Invernadero Norte",
            tenantId = 12345L,
            location = null,
            areaM2 = 800.0,
            timezone = "Europe/Madrid",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )
    )

    val sampleDevices = listOf(
        Device(
            id = 1L,
            code = "DEV-00001",
            tenantId = 12345L,
            sectorId = 1L,
            sectorCode = "SEC-00001",
            categoryId = Device.CATEGORY_SENSOR,
            categoryName = "Sensor",
            typeId = 1,
            typeName = "Temperature",
            unitId = 1,
            unitSymbol = "°C",
            isActive = true
        ),
        Device(
            id = 2L,
            code = "DEV-00002",
            tenantId = 12345L,
            sectorId = 1L,
            sectorCode = "SEC-00001",
            categoryId = Device.CATEGORY_ACTUATOR,
            categoryName = "Actuator",
            typeId = 2,
            typeName = "Valve",
            unitId = null,
            unitSymbol = null,
            isActive = true
        ),
        Device(
            id = 3L,
            code = "DEV-00003",
            tenantId = 12345L,
            sectorId = 2L,
            sectorCode = "SEC-00002",
            categoryId = Device.CATEGORY_SENSOR,
            categoryName = "Sensor",
            typeId = 3,
            typeName = "CO2",
            unitId = 2,
            unitSymbol = "ppm",
            isActive = false
        )
    )
}

@Preview
@Composable
private fun ClientDetailScreenContentPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailScreenContent(
                uiState = ClientDetailUiState(
                    isLoading = false,
                    client = ClientDetailScreenPreviewData.sampleClient,
                    selectedTab = ClientDetailTab.GENERAL
                ),
                onEvent = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview
@Composable
private fun ClientDetailScreenContentUsersTabPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailScreenContent(
                uiState = ClientDetailUiState(
                    isLoading = false,
                    client = ClientDetailScreenPreviewData.sampleClient,
                    selectedTab = ClientDetailTab.USERS,
                    users = ClientDetailScreenPreviewData.sampleUsers
                ),
                onEvent = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview
@Composable
private fun ClientDetailScreenContentGreenhousesTabPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailScreenContent(
                uiState = ClientDetailUiState(
                    isLoading = false,
                    client = ClientDetailScreenPreviewData.sampleClient,
                    selectedTab = ClientDetailTab.GREENHOUSES,
                    greenhouses = ClientDetailScreenPreviewData.sampleGreenhouses
                ),
                onEvent = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview
@Composable
private fun ClientDetailScreenContentGreenhousesHierarchyPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailScreenContent(
                uiState = ClientDetailUiState(
                    isLoading = false,
                    client = ClientDetailScreenPreviewData.sampleClient,
                    selectedTab = ClientDetailTab.GREENHOUSES,
                    greenhouses = ClientDetailScreenPreviewData.sampleGreenhouses,
                    devices = ClientDetailScreenPreviewData.sampleDevices
                ),
                onEvent = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview
@Composable
private fun ClientDetailScreenContentLoadingPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailScreenContent(
                uiState = ClientDetailUiState(isLoading = true),
                onEvent = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview
@Composable
private fun ClientDetailScreenContentErrorPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailScreenContent(
                uiState = ClientDetailUiState(
                    isLoading = false,
                    error = "Failed to load client details"
                ),
                onEvent = {},
                onNavigateBack = {}
            )
        }
    }
}

