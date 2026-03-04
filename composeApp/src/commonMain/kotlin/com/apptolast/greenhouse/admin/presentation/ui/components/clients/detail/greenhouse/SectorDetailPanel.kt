package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.greenhouse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.AlertsTableOrCards
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.DevicesTableOrCards
import com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail.SettingsTableOrCards
import com.apptolast.greenhouse.admin.presentation.ui.components.common.EmptyContent
import com.apptolast.greenhouse.admin.presentation.viewmodel.SectorSubTab
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.greenhouse_info_area
import greenhouseadmin.composeapp.generated.resources.greenhouse_info_code
import greenhouseadmin.composeapp.generated.resources.greenhouse_info_location
import greenhouseadmin.composeapp.generated.resources.greenhouse_info_status
import greenhouseadmin.composeapp.generated.resources.greenhouse_info_timezone
import greenhouseadmin.composeapp.generated.resources.greenhouse_select_sector_hint
import greenhouseadmin.composeapp.generated.resources.new_alert
import greenhouseadmin.composeapp.generated.resources.new_device
import greenhouseadmin.composeapp.generated.resources.new_setting
import greenhouseadmin.composeapp.generated.resources.sector_no_alerts
import greenhouseadmin.composeapp.generated.resources.sector_no_devices
import greenhouseadmin.composeapp.generated.resources.sector_no_settings
import greenhouseadmin.composeapp.generated.resources.select_item_prompt
import greenhouseadmin.composeapp.generated.resources.status_active
import greenhouseadmin.composeapp.generated.resources.status_inactive
import greenhouseadmin.composeapp.generated.resources.sub_tab_alerts
import greenhouseadmin.composeapp.generated.resources.sub_tab_devices
import greenhouseadmin.composeapp.generated.resources.sub_tab_settings
import org.jetbrains.compose.resources.stringResource

/**
 * Right panel showing either sector detail, greenhouse info, or a placeholder.
 *
 * Three states:
 * 1. Sector selected: sector header + sub-tabs (Devices | Alerts | Settings) + filtered table + add button
 * 2. Greenhouse selected but no sector: greenhouse info card
 * 3. Nothing selected: placeholder text
 */
@Composable
fun SectorDetailPanel(
    selectedGreenhouse: Greenhouse?,
    selectedSector: Sector?,
    sectorSubTab: SectorSubTab,
    devices: List<Device>,
    alerts: List<Alert>,
    settings: List<Setting>,
    onSectorSubTabSelected: (SectorSubTab) -> Unit,
    onAddDevice: () -> Unit,
    onEditDevice: (Device) -> Unit,
    onDeleteDevice: (Device) -> Unit,
    onAddAlert: () -> Unit,
    onEditAlert: (Alert) -> Unit,
    onDeleteAlert: (Alert) -> Unit,
    onResolveAlert: (Alert) -> Unit,
    onReopenAlert: (Alert) -> Unit,
    onAddSetting: () -> Unit,
    onEditSetting: (Setting) -> Unit,
    onDeleteSetting: (Setting) -> Unit,
    onCopyId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        selectedSector != null -> {
            SectorDetailContent(
                sector = selectedSector,
                selectedGreenhouse = selectedGreenhouse,
                sectorSubTab = sectorSubTab,
                devices = devices,
                alerts = alerts,
                settings = settings,
                onSectorSubTabSelected = onSectorSubTabSelected,
                onAddDevice = onAddDevice,
                onEditDevice = onEditDevice,
                onDeleteDevice = onDeleteDevice,
                onAddAlert = onAddAlert,
                onEditAlert = onEditAlert,
                onDeleteAlert = onDeleteAlert,
                onResolveAlert = onResolveAlert,
                onReopenAlert = onReopenAlert,
                onAddSetting = onAddSetting,
                onEditSetting = onEditSetting,
                onDeleteSetting = onDeleteSetting,
                onCopyId = onCopyId,
                modifier = modifier
            )
        }

        selectedGreenhouse != null -> {
            GreenhouseInfoContent(
                greenhouse = selectedGreenhouse,
                modifier = modifier
            )
        }

        else -> {
            PlaceholderContent(modifier = modifier)
        }
    }
}

/**
 * Detail content when a sector is selected: header, sub-tabs, and table content.
 */
@Composable
private fun SectorDetailContent(
    sector: Sector,
    selectedGreenhouse: Greenhouse?,
    sectorSubTab: SectorSubTab,
    devices: List<Device>,
    alerts: List<Alert>,
    settings: List<Setting>,
    onSectorSubTabSelected: (SectorSubTab) -> Unit,
    onAddDevice: () -> Unit,
    onEditDevice: (Device) -> Unit,
    onDeleteDevice: (Device) -> Unit,
    onAddAlert: () -> Unit,
    onEditAlert: (Alert) -> Unit,
    onDeleteAlert: (Alert) -> Unit,
    onResolveAlert: (Alert) -> Unit,
    onReopenAlert: (Alert) -> Unit,
    onAddSetting: () -> Unit,
    onEditSetting: (Setting) -> Unit,
    onDeleteSetting: (Setting) -> Unit,
    onCopyId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Sector header with name and add button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = sector.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sector.code,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Add button for the active sub-tab
            val addButtonText = when (sectorSubTab) {
                SectorSubTab.DEVICES -> stringResource(Res.string.new_device)
                SectorSubTab.ALERTS -> stringResource(Res.string.new_alert)
                SectorSubTab.SETTINGS -> stringResource(Res.string.new_setting)
            }
            val addAction = when (sectorSubTab) {
                SectorSubTab.DEVICES -> onAddDevice
                SectorSubTab.ALERTS -> onAddAlert
                SectorSubTab.SETTINGS -> onAddSetting
            }
            Button(
                onClick = addAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(addButtonText)
            }
        }

        // Sub-tabs: Devices | Alerts | Settings
        val subTabs = SectorSubTab.entries
        ScrollableTabRow(
            selectedTabIndex = subTabs.indexOf(sectorSubTab),
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSurface,
            edgePadding = 16.dp,
            divider = {}
        ) {
            subTabs.forEach { tab ->
                val label = when (tab) {
                    SectorSubTab.DEVICES -> stringResource(Res.string.sub_tab_devices)
                    SectorSubTab.ALERTS -> stringResource(Res.string.sub_tab_alerts)
                    SectorSubTab.SETTINGS -> stringResource(Res.string.sub_tab_settings)
                }
                Tab(
                    selected = tab == sectorSubTab,
                    onClick = { onSectorSubTabSelected(tab) },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (tab == sectorSubTab) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

        Spacer(modifier = Modifier.height(16.dp))

        // Sub-tab content with table or empty state
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (sectorSubTab) {
                SectorSubTab.DEVICES -> {
                    if (devices.isEmpty()) {
                        EmptyContent(message = stringResource(Res.string.sector_no_devices))
                    } else {
                        DevicesTableOrCards(
                            devices = devices,
                            onEditDevice = onEditDevice,
                            onDeleteDevice = onDeleteDevice,
                            onCopyId = onCopyId
                        )
                    }
                }

                SectorSubTab.ALERTS -> {
                    if (alerts.isEmpty()) {
                        EmptyContent(message = stringResource(Res.string.sector_no_alerts))
                    } else {
                        AlertsTableOrCards(
                            alerts = alerts,
                            sectors = listOf(sector),
                            greenhouses = listOfNotNull(selectedGreenhouse),
                            onEditAlert = onEditAlert,
                            onDeleteAlert = onDeleteAlert,
                            onResolveAlert = onResolveAlert,
                            onReopenAlert = onReopenAlert,
                            onCopyId = onCopyId
                        )
                    }
                }

                SectorSubTab.SETTINGS -> {
                    if (settings.isEmpty()) {
                        EmptyContent(message = stringResource(Res.string.sector_no_settings))
                    } else {
                        SettingsTableOrCards(
                            settings = settings,
                            onEditSetting = onEditSetting,
                            onDeleteSetting = onDeleteSetting,
                            onCopyId = onCopyId
                        )
                    }
                }
            }
        }
    }
}

/**
 * Greenhouse info card shown when a greenhouse is selected but no sector.
 * Displays name, code, area, timezone, status, and location.
 */
@Composable
private fun GreenhouseInfoContent(
    greenhouse: Greenhouse,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = greenhouse.name,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(
                    label = stringResource(Res.string.greenhouse_info_code),
                    value = greenhouse.code
                )
                InfoRow(
                    label = stringResource(Res.string.greenhouse_info_area),
                    value = greenhouse.areaDisplay
                )
                InfoRow(
                    label = stringResource(Res.string.greenhouse_info_timezone),
                    value = greenhouse.timezone ?: "-"
                )
                InfoRow(
                    label = stringResource(Res.string.greenhouse_info_status),
                    value = if (greenhouse.isActive) {
                        stringResource(Res.string.status_active)
                    } else {
                        stringResource(Res.string.status_inactive)
                    }
                )
                InfoRow(
                    label = stringResource(Res.string.greenhouse_info_location),
                    value = greenhouse.locationDisplay
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.greenhouse_select_sector_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Simple label-value row used inside the greenhouse info card.
 */
@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Placeholder shown when no greenhouse or sector is selected.
 */
@Composable
private fun PlaceholderContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.select_item_prompt),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
