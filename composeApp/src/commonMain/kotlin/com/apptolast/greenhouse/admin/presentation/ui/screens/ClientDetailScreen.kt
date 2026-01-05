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
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailDevicesTab
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailGeneralTab
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailGreenhousesTab
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailHeader
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailSectorsTab
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailSettingsTab
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailTabBar
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.ClientDetailUsersTab
import com.apptolast.greenhouse.admin.presentation.ui.components.common.DashboardTopBar
import com.apptolast.greenhouse.admin.presentation.ui.components.common.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.components.common.LoadingContent
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
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.app_name
import greenhouseadmin.composeapp.generated.resources.breadcrumb_clients
import greenhouseadmin.composeapp.generated.resources.error_unknown
import greenhouseadmin.composeapp.generated.resources.new_alert
import greenhouseadmin.composeapp.generated.resources.new_device
import greenhouseadmin.composeapp.generated.resources.new_greenhouse
import greenhouseadmin.composeapp.generated.resources.new_sector
import greenhouseadmin.composeapp.generated.resources.new_setting
import greenhouseadmin.composeapp.generated.resources.new_user
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
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
    onEvent: (ClientDetailEvent) -> Unit = {},
    onNavigateBack: () -> Unit = {},
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
            greenhouses = uiState.greenhouses,
            isSubmitting = uiState.isSubmittingSector,
            error = uiState.submitSectorError,
            onSubmit = { greenhouseId, variety ->
                onEvent(ClientDetailEvent.OnSubmitSectorForm(greenhouseId, variety))
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
            greenhouses = uiState.greenhouses,
            categories = uiState.deviceCategories,
            types = uiState.deviceTypes,
            units = uiState.deviceUnits,
            isLoadingCatalog = uiState.isLoadingDeviceCatalog,
            isSubmitting = uiState.isSubmittingDevice,
            error = uiState.submitDeviceError,
            onCategoryChanged = { categoryId ->
                onEvent(ClientDetailEvent.OnDeviceCategoryChanged(categoryId))
            },
            onSubmit = { greenhouseId, name, categoryId, typeId, unitId, isActive ->
                onEvent(ClientDetailEvent.OnSubmitDeviceForm(greenhouseId, name, categoryId, typeId, unitId, isActive))
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
            greenhouses = uiState.greenhouses,
            alertTypes = uiState.alertTypes,
            severities = uiState.alertSeverities,
            isLoadingCatalog = uiState.isLoadingAlertCatalog,
            isSubmitting = uiState.isSubmittingAlert,
            error = uiState.submitAlertError,
            onSubmit = { greenhouseId, alertTypeId, severityId, message ->
                onEvent(ClientDetailEvent.OnSubmitAlertForm(greenhouseId, alertTypeId, severityId, message))
            },
            onDismiss = { onEvent(ClientDetailEvent.OnDismissAlertFormDialog) }
        )
    }

    // Alert Delete Confirmation Dialog
    if (uiState.showDeleteAlertConfirmation && uiState.alertToDelete != null) {
        DeleteConfirmationDialog(
            clientName = uiState.alertToDelete.message,
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
            greenhouses = uiState.greenhouses,
            parameters = uiState.deviceTypes,
            periods = uiState.periods,
            isLoadingCatalog = uiState.isLoadingSettingCatalog || uiState.isLoadingDeviceCatalog,
            isSubmitting = uiState.isSubmittingSetting,
            error = uiState.submitSettingError,
            onSubmit = { greenhouseId, parameterId, periodId, minValue, maxValue, isActive ->
                onEvent(
                    ClientDetailEvent.OnSubmitSettingForm(
                        greenhouseId = greenhouseId,
                        parameterId = parameterId,
                        periodId = periodId,
                        minValue = minValue,
                        maxValue = maxValue,
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
    onNavigateBack: () -> Unit
) {
    val contentPadding = AdaptiveDimens.contentPadding()
    val windowInfo = LocalAppWindowInfo.current

    // Show FAB on compact screens when on tabs that support adding items
    val showFab = windowInfo.isCompact && uiState.selectedTab in listOf(
        ClientDetailTab.USERS,
        ClientDetailTab.GREENHOUSES,
        ClientDetailTab.SECTORS,
        ClientDetailTab.DEVICES,
        ClientDetailTab.ALERTS,
        ClientDetailTab.SETTINGS
    )

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
                    ClientDetailGreenhousesTab(
                        greenhouses = uiState.greenhouses,
                        isLoading = uiState.isLoadingGreenhouses,
                        error = uiState.greenhousesError,
                        onAddGreenhouse = { onEvent(ClientDetailEvent.OnAddGreenhouseClicked) },
                        onEditGreenhouse = { greenhouse -> onEvent(ClientDetailEvent.OnEditGreenhouseClicked(greenhouse)) },
                        onDeleteGreenhouse = { greenhouse ->
                            onEvent(
                                ClientDetailEvent.OnDeleteGreenhouseClicked(
                                    greenhouse
                                )
                            )
                        },
                        onRetry = { onEvent(ClientDetailEvent.LoadGreenhouses) }
                    )
                }

                ClientDetailTab.SECTORS -> {
                    ClientDetailSectorsTab(
                        sectors = uiState.sectors,
                        greenhouses = uiState.greenhouses,
                        isLoading = uiState.isLoadingSectors,
                        error = uiState.sectorsError,
                        onAddSector = { onEvent(ClientDetailEvent.OnAddSectorClicked) },
                        onEditSector = { sector -> onEvent(ClientDetailEvent.OnEditSectorClicked(sector)) },
                        onDeleteSector = { sector -> onEvent(ClientDetailEvent.OnDeleteSectorClicked(sector)) },
                        onRetry = { onEvent(ClientDetailEvent.LoadSectors) }
                    )
                }

                ClientDetailTab.DEVICES -> {
                    ClientDetailDevicesTab(
                        devices = uiState.devices,
                        isLoading = uiState.isLoadingDevices,
                        error = uiState.devicesError,
                        onAddDevice = { onEvent(ClientDetailEvent.OnAddDeviceClicked) },
                        onEditDevice = { device -> onEvent(ClientDetailEvent.OnEditDeviceClicked(device)) },
                        onDeleteDevice = { device -> onEvent(ClientDetailEvent.OnDeleteDeviceClicked(device)) },
                        onRetry = { onEvent(ClientDetailEvent.LoadDevices) }
                    )
                }

                ClientDetailTab.ALERTS -> {
                    ClientDetailAlertsTab(
                        alerts = uiState.alerts,
                        isLoading = uiState.isLoadingAlerts,
                        error = uiState.alertsError,
                        onAddAlert = { onEvent(ClientDetailEvent.OnAddAlertClicked) },
                        onEditAlert = { alert -> onEvent(ClientDetailEvent.OnEditAlertClicked(alert)) },
                        onDeleteAlert = { alert -> onEvent(ClientDetailEvent.OnDeleteAlertClicked(alert)) },
                        onResolveAlert = { alert -> onEvent(ClientDetailEvent.OnResolveAlertClicked(alert)) },
                        onReopenAlert = { alert -> onEvent(ClientDetailEvent.OnReopenAlertClicked(alert)) },
                        onRetry = { onEvent(ClientDetailEvent.LoadAlerts) }
                    )
                }

                ClientDetailTab.SETTINGS -> {
                    ClientDetailSettingsTab(
                        settings = uiState.settings,
                        isLoading = uiState.isLoadingSettings,
                        error = uiState.settingsError,
                        onAddSetting = { onEvent(ClientDetailEvent.OnAddSettingClicked) },
                        onEditSetting = { setting -> onEvent(ClientDetailEvent.OnEditSettingClicked(setting)) },
                        onDeleteSetting = { setting -> onEvent(ClientDetailEvent.OnDeleteSettingClicked(setting)) },
                        onRetry = { onEvent(ClientDetailEvent.LoadSettings) }
                    )
                }
            }
        }

        // FAB for adding items on compact screens
        if (showFab) {
            val fabContentDescription = when (uiState.selectedTab) {
                ClientDetailTab.USERS -> stringResource(Res.string.new_user)
                ClientDetailTab.GREENHOUSES -> stringResource(Res.string.new_greenhouse)
                ClientDetailTab.SECTORS -> stringResource(Res.string.new_sector)
                ClientDetailTab.DEVICES -> stringResource(Res.string.new_device)
                ClientDetailTab.ALERTS -> stringResource(Res.string.new_alert)
                ClientDetailTab.SETTINGS -> stringResource(Res.string.new_setting)
                else -> ""
            }
            val fabOnClick: () -> Unit = when (uiState.selectedTab) {
                ClientDetailTab.USERS -> {
                    { onEvent(ClientDetailEvent.OnAddUserClicked) }
                }

                ClientDetailTab.GREENHOUSES -> {
                    { onEvent(ClientDetailEvent.OnAddGreenhouseClicked) }
                }

                ClientDetailTab.SECTORS -> {
                    { onEvent(ClientDetailEvent.OnAddSectorClicked) }
                }

                ClientDetailTab.DEVICES -> {
                    { onEvent(ClientDetailEvent.OnAddDeviceClicked) }
                }

                ClientDetailTab.ALERTS -> {
                    { onEvent(ClientDetailEvent.OnAddAlertClicked) }
                }

                ClientDetailTab.SETTINGS -> {
                    { onEvent(ClientDetailEvent.OnAddSettingClicked) }
                }

                else -> {
                    {}
                }
            }

            FloatingActionButton(
                onClick = fabOnClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = fabContentDescription
                )
            }
        }
    }
}

private object ClientDetailScreenPreviewData {
    val sampleClient = Client(
        id = "12345",
        name = "Fresh Vegetables Co.",
        email = "contact@freshveg.com",
        phone = "+34 612 345 678",
        province = "Almeria",
        country = "Spain",
        status = ClientStatus.ACTIVE
    )

    val sampleUsers = listOf(
        User(
            id = "1",
            username = "anamartinez",
            email = "ana@freshveg.com",
            role = UserRole.ADMIN,
            tenantId = "12345",
            isActive = true
        ),
        User(
            id = "2",
            username = "carlosruiz",
            email = "carlos@freshveg.com",
            role = UserRole.OPERATOR,
            tenantId = "12345",
            isActive = true
        )
    )

    val sampleGreenhouses = listOf(
        Greenhouse(
            id = "1",
            name = "Invernadero Principal",
            tenantId = "12345",
            location = null,
            areaM2 = 1500.0,
            timezone = "Europe/Madrid",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Greenhouse(
            id = "2",
            name = "Invernadero Norte",
            tenantId = "12345",
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
            id = "1",
            tenantId = "12345",
            greenhouseId = "gh1",
            categoryId = Device.CATEGORY_SENSOR,
            categoryName = "Sensor",
            typeId = 1,
            typeName = "Temperature",
            unitId = 1,
            unitSymbol = "°C",
            isActive = true
        ),
        Device(
            id = "2",
            tenantId = "12345",
            greenhouseId = "gh1",
            categoryId = Device.CATEGORY_ACTUATOR,
            categoryName = "Actuator",
            typeId = 2,
            typeName = "Valve",
            unitId = null,
            unitSymbol = null,
            isActive = true
        ),
        Device(
            id = "3",
            tenantId = "12345",
            greenhouseId = "gh1",
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
private fun ClientDetailScreenContentDevicesTabPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailScreenContent(
                uiState = ClientDetailUiState(
                    isLoading = false,
                    client = ClientDetailScreenPreviewData.sampleClient,
                    selectedTab = ClientDetailTab.DEVICES,
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

