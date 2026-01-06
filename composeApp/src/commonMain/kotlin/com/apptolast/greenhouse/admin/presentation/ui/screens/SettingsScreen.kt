package com.apptolast.greenhouse.admin.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.dialogs.DeleteConfirmationDialog
import com.apptolast.greenhouse.admin.presentation.ui.components.settings.SettingsAccountTab
import com.apptolast.greenhouse.admin.presentation.ui.components.settings.SettingsAlertSeveritiesTab
import com.apptolast.greenhouse.admin.presentation.ui.components.settings.SettingsAlertTypesTab
import com.apptolast.greenhouse.admin.presentation.ui.components.settings.SettingsDeviceCategoriesTab
import com.apptolast.greenhouse.admin.presentation.ui.components.settings.SettingsDeviceTypesTab
import com.apptolast.greenhouse.admin.presentation.ui.components.settings.SettingsDeviceUnitsTab
import com.apptolast.greenhouse.admin.presentation.ui.components.settings.SettingsPeriodsTab
import com.apptolast.greenhouse.admin.presentation.ui.components.settings.SettingsTabBar
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.SettingsEvent
import com.apptolast.greenhouse.admin.presentation.viewmodel.SettingsTab
import com.apptolast.greenhouse.admin.presentation.viewmodel.SettingsUiState
import com.apptolast.greenhouse.admin.presentation.viewmodel.SettingsViewModel
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.logout_confirm_button
import greenhouseadmin.composeapp.generated.resources.logout_confirmation_message
import greenhouseadmin.composeapp.generated.resources.logout_confirmation_title
import greenhouseadmin.composeapp.generated.resources.settings_screen_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

/**
 * Settings screen with tabs for account and catalog management.
 * Responsive design for all screen sizes.
 */
@Composable
fun SettingsScreen(
    onLogoutSuccess: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle logout navigation
    LaunchedEffect(uiState.isLogoutSuccessful) {
        if (uiState.isLogoutSuccessful) {
            onLogoutSuccess()
            viewModel.onEvent(SettingsEvent.OnLogoutComplete)
        }
    }

    SettingsScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit = {}
) {
    val windowInfo = LocalAppWindowInfo.current

    // Logout confirmation dialog
    if (uiState.showLogoutConfirmation) {
        LogoutConfirmationDialog(
            onConfirm = { onEvent(SettingsEvent.OnConfirmLogout) },
            onDismiss = { onEvent(SettingsEvent.OnCancelLogout) }
        )
    }

    // Delete confirmation dialogs for each catalog type
    if (uiState.showDeleteDeviceCategoryConfirmation) {
        DeleteConfirmationDialog(
            clientName = uiState.deviceCategoryToDelete?.name ?: "",
            onConfirm = { onEvent(SettingsEvent.OnConfirmDeleteDeviceCategory) },
            onDismiss = { onEvent(SettingsEvent.OnCancelDeleteDeviceCategory) },
            isDeleting = uiState.isDeletingDeviceCategory
        )
    }

    if (uiState.showDeleteDeviceTypeConfirmation) {
        DeleteConfirmationDialog(
            clientName = uiState.deviceTypeToDelete?.name ?: "",
            onConfirm = { onEvent(SettingsEvent.OnConfirmDeleteDeviceType) },
            onDismiss = { onEvent(SettingsEvent.OnCancelDeleteDeviceType) },
            isDeleting = uiState.isDeletingDeviceType
        )
    }

    if (uiState.showDeleteAlertTypeConfirmation) {
        DeleteConfirmationDialog(
            clientName = uiState.alertTypeToDelete?.name ?: "",
            onConfirm = { onEvent(SettingsEvent.OnConfirmDeleteAlertType) },
            onDismiss = { onEvent(SettingsEvent.OnCancelDeleteAlertType) },
            isDeleting = uiState.isDeletingAlertType
        )
    }

    if (uiState.showDeleteAlertSeverityConfirmation) {
        DeleteConfirmationDialog(
            clientName = uiState.alertSeverityToDelete?.name ?: "",
            onConfirm = { onEvent(SettingsEvent.OnConfirmDeleteAlertSeverity) },
            onDismiss = { onEvent(SettingsEvent.OnCancelDeleteAlertSeverity) },
            isDeleting = uiState.isDeletingAlertSeverity
        )
    }

    if (uiState.showDeletePeriodConfirmation) {
        DeleteConfirmationDialog(
            clientName = uiState.periodToDelete?.name ?: "",
            onConfirm = { onEvent(SettingsEvent.OnConfirmDeletePeriod) },
            onDismiss = { onEvent(SettingsEvent.OnCancelDeletePeriod) },
            isDeleting = uiState.isDeletingPeriod
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = if (windowInfo.isCompact) 16.dp else 24.dp,
                        vertical = 24.dp
                    )
            ) {
                Text(
                    text = stringResource(Res.string.settings_screen_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tab bar
                SettingsTabBar(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = { tab -> onEvent(SettingsEvent.OnTabSelected(tab)) }
                )
            }

            // Content based on selected tab
            if (uiState.isCatalogsLoading && uiState.selectedTab != SettingsTab.ACCOUNT) {
                // Show loading only for catalog tabs
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                when (uiState.selectedTab) {
                    SettingsTab.ACCOUNT -> {
                        SettingsAccountTab(
                            username = uiState.username,
                            roles = uiState.roles,
                            isLoggingOut = uiState.isLoggingOut,
                            onLogoutClicked = { onEvent(SettingsEvent.OnLogoutClicked) }
                        )
                    }

                    SettingsTab.DEVICE_CATEGORIES -> {
                        SettingsDeviceCategoriesTab(
                            categories = uiState.deviceCategories,
                            showDialog = uiState.showDeviceCategoryDialog,
                            formMode = uiState.deviceCategoryFormMode,
                            isSubmitting = uiState.isSubmittingDeviceCategory,
                            submitError = uiState.submitDeviceCategoryError,
                            onAddClicked = { onEvent(SettingsEvent.OnAddDeviceCategoryClicked) },
                            onEditClicked = { onEvent(SettingsEvent.OnEditDeviceCategoryClicked(it)) },
                            onDeleteClicked = { onEvent(SettingsEvent.OnDeleteDeviceCategoryClicked(it)) },
                            onSubmit = { name -> onEvent(SettingsEvent.OnSubmitDeviceCategory(name)) },
                            onDismissDialog = { onEvent(SettingsEvent.OnDismissDeviceCategoryDialog) }
                        )
                    }

                    SettingsTab.DEVICE_TYPES -> {
                        SettingsDeviceTypesTab(
                            deviceTypes = uiState.deviceTypes,
                            categories = uiState.deviceCategories,
                            units = uiState.deviceUnits,
                            showDialog = uiState.showDeviceTypeDialog,
                            formMode = uiState.deviceTypeFormMode,
                            isSubmitting = uiState.isSubmittingDeviceType,
                            submitError = uiState.submitDeviceTypeError,
                            onAddClicked = { onEvent(SettingsEvent.OnAddDeviceTypeClicked) },
                            onEditClicked = { onEvent(SettingsEvent.OnEditDeviceTypeClicked(it)) },
                            onDeleteClicked = { onEvent(SettingsEvent.OnDeleteDeviceTypeClicked(it)) },
                            onActivateClicked = { onEvent(SettingsEvent.OnActivateDeviceType(it)) },
                            onDeactivateClicked = { onEvent(SettingsEvent.OnDeactivateDeviceType(it)) },
                            onSubmit = { name, desc, catId, unitId, dataType, minVal, maxVal, ctrlType, isActive ->
                                onEvent(
                                    SettingsEvent.OnSubmitDeviceType(
                                        name, desc, catId, unitId, dataType, minVal, maxVal, ctrlType, isActive
                                    )
                                )
                            },
                            onDismissDialog = { onEvent(SettingsEvent.OnDismissDeviceTypeDialog) }
                        )
                    }

                    SettingsTab.DEVICE_UNITS -> {
                        SettingsDeviceUnitsTab(
                            units = uiState.deviceUnits
                        )
                    }

                    SettingsTab.ALERT_TYPES -> {
                        SettingsAlertTypesTab(
                            alertTypes = uiState.alertTypes,
                            showDialog = uiState.showAlertTypeDialog,
                            formMode = uiState.alertTypeFormMode,
                            isSubmitting = uiState.isSubmittingAlertType,
                            submitError = uiState.submitAlertTypeError,
                            onAddClicked = { onEvent(SettingsEvent.OnAddAlertTypeClicked) },
                            onEditClicked = { onEvent(SettingsEvent.OnEditAlertTypeClicked(it)) },
                            onDeleteClicked = { onEvent(SettingsEvent.OnDeleteAlertTypeClicked(it)) },
                            onSubmit = { name, desc -> onEvent(SettingsEvent.OnSubmitAlertType(name, desc)) },
                            onDismissDialog = { onEvent(SettingsEvent.OnDismissAlertTypeDialog) }
                        )
                    }

                    SettingsTab.ALERT_SEVERITIES -> {
                        SettingsAlertSeveritiesTab(
                            severities = uiState.alertSeverities,
                            showDialog = uiState.showAlertSeverityDialog,
                            formMode = uiState.alertSeverityFormMode,
                            isSubmitting = uiState.isSubmittingAlertSeverity,
                            submitError = uiState.submitAlertSeverityError,
                            onAddClicked = { onEvent(SettingsEvent.OnAddAlertSeverityClicked) },
                            onEditClicked = { onEvent(SettingsEvent.OnEditAlertSeverityClicked(it)) },
                            onDeleteClicked = { onEvent(SettingsEvent.OnDeleteAlertSeverityClicked(it)) },
                            onSubmit = { name, level, desc, color, requiresAction, notifDelay ->
                                onEvent(
                                    SettingsEvent.OnSubmitAlertSeverity(
                                        name, level, desc, color, requiresAction, notifDelay
                                    )
                                )
                            },
                            onDismissDialog = { onEvent(SettingsEvent.OnDismissAlertSeverityDialog) }
                        )
                    }

                    SettingsTab.PERIODS -> {
                        SettingsPeriodsTab(
                            periods = uiState.periods,
                            showDialog = uiState.showPeriodDialog,
                            formMode = uiState.periodFormMode,
                            isSubmitting = uiState.isSubmittingPeriod,
                            submitError = uiState.submitPeriodError,
                            onAddClicked = { onEvent(SettingsEvent.OnAddPeriodClicked) },
                            onEditClicked = { onEvent(SettingsEvent.OnEditPeriodClicked(it)) },
                            onDeleteClicked = { onEvent(SettingsEvent.OnDeletePeriodClicked(it)) },
                            onSubmit = { name -> onEvent(SettingsEvent.OnSubmitPeriod(name)) },
                            onDismissDialog = { onEvent(SettingsEvent.OnDismissPeriodDialog) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = stringResource(Res.string.logout_confirmation_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.logout_confirmation_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(Res.string.logout_confirm_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(Res.string.button_cancel))
            }
        }
    )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            SettingsScreenContent(
                uiState = SettingsUiState(
                    username = "admin@greenhouse.com",
                    roles = listOf("ROLE_ADMIN"),
                    isCatalogsLoading = false
                )
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenCatalogsTabPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            SettingsScreenContent(
                uiState = SettingsUiState(
                    username = "admin@greenhouse.com",
                    roles = listOf("ROLE_ADMIN"),
                    selectedTab = SettingsTab.DEVICE_CATEGORIES,
                    isCatalogsLoading = false
                )
            )
        }
    }
}

@Preview
@Composable
private fun LogoutConfirmationDialogPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            LogoutConfirmationDialog()
        }
    }
}
