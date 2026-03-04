package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.greenhouse

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.LoadingContent
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailEvent
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailUiState

/**
 * Hierarchical tab for the Greenhouses section.
 *
 * Desktop/Tablet (>=600dp): two-column master-detail layout
 *   - Left: GreenhouseTreePanel (300dp fixed width)
 *   - Right: SectorDetailPanel (remaining space)
 *
 * Compact (<600dp): drill-down with animated transitions
 *   - Default: GreenhouseTreePanel (full width)
 *   - On sector selected: SectorDetailPanel slides in from right
 */
@Composable
fun GreenhouseHierarchicalTab(
    uiState: ClientDetailUiState,
    onEvent: (ClientDetailEvent) -> Unit,
    onCopyId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoadingGreenhouses && uiState.greenhouses.isEmpty()) {
        LoadingContent()
        return
    }

    val windowInfo = LocalAppWindowInfo.current

    if (windowInfo.isCompact) {
        CompactHierarchicalLayout(
            uiState = uiState,
            onEvent = onEvent,
            onCopyId = onCopyId,
            modifier = modifier
        )
    } else {
        ExpandedHierarchicalLayout(
            uiState = uiState,
            onEvent = onEvent,
            onCopyId = onCopyId,
            modifier = modifier
        )
    }
}

/**
 * Two-column layout for medium and expanded screens.
 */
@Composable
private fun ExpandedHierarchicalLayout(
    uiState: ClientDetailUiState,
    onEvent: (ClientDetailEvent) -> Unit,
    onCopyId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        GreenhouseTreePanel(
            greenhouses = uiState.greenhouses,
            sectorsForGreenhouse = { uiState.sectorsForGreenhouse(it) },
            expandedGreenhouseIds = uiState.expandedGreenhouseIds,
            selectedGreenhouseId = uiState.selectedGreenhouseId,
            selectedSectorId = uiState.selectedSectorId,
            onToggleExpand = { onEvent(ClientDetailEvent.OnGreenhouseExpandToggle(it)) },
            onSelectGreenhouse = { onEvent(ClientDetailEvent.OnGreenhouseSelected(it)) },
            onSelectSector = { onEvent(ClientDetailEvent.OnSectorSelected(it)) },
            onAddGreenhouse = { onEvent(ClientDetailEvent.OnAddGreenhouseClicked) },
            onEditGreenhouse = { onEvent(ClientDetailEvent.OnEditGreenhouseClicked(it)) },
            onDeleteGreenhouse = { onEvent(ClientDetailEvent.OnDeleteGreenhouseClicked(it)) },
            onAddSector = { ghId ->
                onEvent(ClientDetailEvent.OnGreenhouseSelected(ghId))
                onEvent(ClientDetailEvent.OnAddSectorClicked)
            },
            onEditSector = { onEvent(ClientDetailEvent.OnEditSectorClicked(it)) },
            onDeleteSector = { onEvent(ClientDetailEvent.OnDeleteSectorClicked(it)) },
            modifier = Modifier.width(300.dp)
        )

        VerticalDivider()

        SectorDetailPanel(
            selectedGreenhouse = uiState.selectedGreenhouse,
            selectedSector = uiState.selectedSector,
            sectorSubTab = uiState.sectorSubTab,
            devices = uiState.selectedSectorId?.let { uiState.devicesForSector(it) } ?: emptyList(),
            alerts = uiState.selectedSectorId?.let { uiState.alertsForSector(it) } ?: emptyList(),
            settings = uiState.selectedSectorId?.let { uiState.settingsForSector(it) } ?: emptyList(),
            onSectorSubTabSelected = { onEvent(ClientDetailEvent.OnSectorSubTabSelected(it)) },
            onAddDevice = { onEvent(ClientDetailEvent.OnAddDeviceClicked) },
            onEditDevice = { onEvent(ClientDetailEvent.OnEditDeviceClicked(it)) },
            onDeleteDevice = { onEvent(ClientDetailEvent.OnDeleteDeviceClicked(it)) },
            onAddAlert = { onEvent(ClientDetailEvent.OnAddAlertClicked) },
            onEditAlert = { onEvent(ClientDetailEvent.OnEditAlertClicked(it)) },
            onDeleteAlert = { onEvent(ClientDetailEvent.OnDeleteAlertClicked(it)) },
            onResolveAlert = { onEvent(ClientDetailEvent.OnResolveAlertClicked(it)) },
            onReopenAlert = { onEvent(ClientDetailEvent.OnReopenAlertClicked(it)) },
            onAddSetting = { onEvent(ClientDetailEvent.OnAddSettingClicked) },
            onEditSetting = { onEvent(ClientDetailEvent.OnEditSettingClicked(it)) },
            onDeleteSetting = { onEvent(ClientDetailEvent.OnDeleteSettingClicked(it)) },
            onCopyId = onCopyId,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Drill-down layout for compact screens.
 * Shows the tree panel by default, slides to sector detail when a sector is selected.
 */
@Composable
private fun CompactHierarchicalLayout(
    uiState: ClientDetailUiState,
    onEvent: (ClientDetailEvent) -> Unit,
    onCopyId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine which view to show: detail panel if sector is selected, tree otherwise
    val showDetail = uiState.selectedSectorId != null

    AnimatedContent(
        targetState = showDetail,
        transitionSpec = {
            if (targetState) {
                // Navigating to detail: slide in from right
                slideInHorizontally { fullWidth -> fullWidth } togetherWith
                        slideOutHorizontally { fullWidth -> -fullWidth }
            } else {
                // Navigating back to tree: slide in from left
                slideInHorizontally { fullWidth -> -fullWidth } togetherWith
                        slideOutHorizontally { fullWidth -> fullWidth }
            }
        },
        modifier = modifier.fillMaxSize(),
        label = "greenhouse_hierarchy_transition"
    ) { isDetail ->
        if (isDetail) {
            Box(modifier = Modifier.fillMaxSize()) {
                SectorDetailPanel(
                    selectedGreenhouse = uiState.selectedGreenhouse,
                    selectedSector = uiState.selectedSector,
                    sectorSubTab = uiState.sectorSubTab,
                    devices = uiState.selectedSectorId?.let { uiState.devicesForSector(it) } ?: emptyList(),
                    alerts = uiState.selectedSectorId?.let { uiState.alertsForSector(it) } ?: emptyList(),
                    settings = uiState.selectedSectorId?.let { uiState.settingsForSector(it) } ?: emptyList(),
                    onSectorSubTabSelected = { onEvent(ClientDetailEvent.OnSectorSubTabSelected(it)) },
                    onAddDevice = { onEvent(ClientDetailEvent.OnAddDeviceClicked) },
                    onEditDevice = { onEvent(ClientDetailEvent.OnEditDeviceClicked(it)) },
                    onDeleteDevice = { onEvent(ClientDetailEvent.OnDeleteDeviceClicked(it)) },
                    onAddAlert = { onEvent(ClientDetailEvent.OnAddAlertClicked) },
                    onEditAlert = { onEvent(ClientDetailEvent.OnEditAlertClicked(it)) },
                    onDeleteAlert = { onEvent(ClientDetailEvent.OnDeleteAlertClicked(it)) },
                    onResolveAlert = { onEvent(ClientDetailEvent.OnResolveAlertClicked(it)) },
                    onReopenAlert = { onEvent(ClientDetailEvent.OnReopenAlertClicked(it)) },
                    onAddSetting = { onEvent(ClientDetailEvent.OnAddSettingClicked) },
                    onEditSetting = { onEvent(ClientDetailEvent.OnEditSettingClicked(it)) },
                    onDeleteSetting = { onEvent(ClientDetailEvent.OnDeleteSettingClicked(it)) },
                    onCopyId = onCopyId,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            GreenhouseTreePanel(
                greenhouses = uiState.greenhouses,
                sectorsForGreenhouse = { uiState.sectorsForGreenhouse(it) },
                expandedGreenhouseIds = uiState.expandedGreenhouseIds,
                selectedGreenhouseId = uiState.selectedGreenhouseId,
                selectedSectorId = uiState.selectedSectorId,
                onToggleExpand = { onEvent(ClientDetailEvent.OnGreenhouseExpandToggle(it)) },
                onSelectGreenhouse = { onEvent(ClientDetailEvent.OnGreenhouseSelected(it)) },
                onSelectSector = { onEvent(ClientDetailEvent.OnSectorSelected(it)) },
                onAddGreenhouse = { onEvent(ClientDetailEvent.OnAddGreenhouseClicked) },
                onEditGreenhouse = { onEvent(ClientDetailEvent.OnEditGreenhouseClicked(it)) },
                onDeleteGreenhouse = { onEvent(ClientDetailEvent.OnDeleteGreenhouseClicked(it)) },
                onAddSector = { ghId ->
                    onEvent(ClientDetailEvent.OnGreenhouseSelected(ghId))
                    onEvent(ClientDetailEvent.OnAddSectorClicked)
                },
                onEditSector = { onEvent(ClientDetailEvent.OnEditSectorClicked(it)) },
                onDeleteSector = { onEvent(ClientDetailEvent.OnDeleteSectorClicked(it)) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
