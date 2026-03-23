package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.CopyableIdCell
import com.apptolast.greenhouse.admin.presentation.ui.components.common.StatusChip
import com.apptolast.greenhouse.admin.presentation.ui.components.common.table.SortDirection
import com.apptolast.greenhouse.admin.presentation.ui.components.common.table.SortableColumnHeader
import com.apptolast.greenhouse.admin.presentation.ui.components.common.table.TableRowActions
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_category
import greenhouseadmin.composeapp.generated.resources.header_client_name
import greenhouseadmin.composeapp.generated.resources.header_id
import greenhouseadmin.composeapp.generated.resources.header_name
import greenhouseadmin.composeapp.generated.resources.header_status
import greenhouseadmin.composeapp.generated.resources.header_type
import greenhouseadmin.composeapp.generated.resources.header_unit
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Enum representing sortable columns in the Devices table.
 */
private enum class DeviceSortColumn {
    ID, NAME
}

/**
 * Adaptive component that shows a table on larger screens and cards on compact screens.
 */
@Composable
fun DevicesTableOrCards(
    devices: List<Device>,
    onEditDevice: (Device) -> Unit = {},
    onDeleteDevice: (Device) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    if (windowInfo.isCompact) {
        DevicesCardList(
            devices = devices,
            onEditDevice = onEditDevice,
            onDeleteDevice = onDeleteDevice,
            onCopyId = onCopyId,
            modifier = modifier
        )
    } else {
        DevicesTable(
            devices = devices,
            onEditDevice = onEditDevice,
            onDeleteDevice = onDeleteDevice,
            onCopyId = onCopyId,
            modifier = modifier
        )
    }
}

/**
 * Table displaying list of devices with headers and rows.
 * Columns: ID | NAME | CATEGORY | TYPE | UNIT | STATUS | ACTIONS
 * Sortable columns: ID, NAME
 */
@Composable
fun DevicesTable(
    devices: List<Device>,
    onEditDevice: (Device) -> Unit = {},
    onDeleteDevice: (Device) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var sortColumn by remember { mutableStateOf<DeviceSortColumn?>(null) }
    var sortDirection by remember { mutableStateOf(SortDirection.ASCENDING) }

    // Sort devices based on selected column and direction
    val sortedDevices = remember(devices, sortColumn, sortDirection) {
        when (sortColumn) {
            DeviceSortColumn.ID -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    devices.sortedBy { it.code }
                } else {
                    devices.sortedByDescending { it.code }
                }
            }

            DeviceSortColumn.NAME -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    devices.sortedBy { it.name?.lowercase() ?: "" }
                } else {
                    devices.sortedByDescending { it.name?.lowercase() ?: "" }
                }
            }

            null -> devices
        }
    }

    fun onHeaderClick(column: DeviceSortColumn) {
        if (sortColumn == column) {
            sortDirection = sortDirection.toggle()
        } else {
            sortColumn = column
            sortDirection = SortDirection.ASCENDING
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Header row with sorting
            DevicesTableHeader(
                sortColumn = sortColumn,
                sortDirection = sortDirection,
                onSortClick = ::onHeaderClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Device rows
            sortedDevices.forEach { device ->
                DeviceTableRow(
                    device = device,
                    onEdit = { onEditDevice(device) },
                    onDelete = { onDeleteDevice(device) },
                    onCopyId = { onCopyId(device.code) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun DevicesTableHeader(
    sortColumn: DeviceSortColumn?,
    sortDirection: SortDirection,
    onSortClick: (DeviceSortColumn) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // ID column - Sortable
        SortableColumnHeader(
            text = stringResource(Res.string.header_id),
            column = DeviceSortColumn.ID,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(DeviceSortColumn.ID) },
            modifier = Modifier.weight(0.4f)
        )

        // NAME column - Sortable
        SortableColumnHeader(
            text = stringResource(Res.string.header_name),
            column = DeviceSortColumn.NAME,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(DeviceSortColumn.NAME) },
            modifier = Modifier.weight(0.9f)
        )

        // CLIENT NAME column - Not sortable
        Text(
            text = stringResource(Res.string.header_client_name),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.9f)
        )

        // CATEGORY column - Not sortable
        Text(
            text = stringResource(Res.string.header_category),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f)
        )

        // TYPE column - Not sortable
        Text(
            text = stringResource(Res.string.header_type),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.7f)
        )

        // UNIT column - Not sortable
        Text(
            text = stringResource(Res.string.header_unit),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )

        // STATUS column - Not sortable
        Text(
            text = stringResource(Res.string.header_status),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f)
        )

        // ACTIONS column - Not sortable
        Text(
            text = stringResource(Res.string.header_actions),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DeviceTableRow(
    device: Device,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // ID - Copyable code
        CopyableIdCell(
            id = device.code,
            onCopyId = onCopyId,
            modifier = Modifier.weight(0.4f)
        )

        // NAME - Device name (or empty if not set)
        Text(
            text = device.name ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = if (device.name != null) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.weight(0.9f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // CLIENT NAME - Display name for end users
        Text(
            text = device.clientName ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = if (device.clientName != null) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.weight(0.9f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // CATEGORY - Plain text
        Text(
            text = device.categoryName ?: device.categoryDisplayName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // TYPE - Device type name
        Text(
            text = device.typeName ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // UNIT - Unit symbol
        Text(
            text = device.unitSymbol ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // STATUS - Status chip with wrapContentWidth
        StatusChip(
            isActive = device.isActive,
            modifier = Modifier.weight(0.6f).wrapContentWidth(align = Alignment.Start)
        )

        // ACTIONS
        TableRowActions(onEdit = onEdit, onDelete = onDelete)
    }
}

/**
 * Card list for displaying devices on compact screens.
 */
@Composable
private fun DevicesCardList(
    devices: List<Device>,
    modifier: Modifier = Modifier,
    onEditDevice: (Device) -> Unit = {},
    onDeleteDevice: (Device) -> Unit = {},
    onCopyId: (String) -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        devices.forEach { device ->
            DeviceCard(
                device = device,
                onEdit = { onEditDevice(device) },
                onDelete = { onDeleteDevice(device) },
                onCopyId = { onCopyId(device.code) }
            )
        }
    }
}

/**
 * Individual device card for compact screens with dropdown menu for actions.
 */
@Composable
private fun DeviceCard(
    device: Device,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyId: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row: Icon, Name/ID, Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = device.name ?: "Device",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = device.code,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(Res.string.header_actions),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.action_edit)) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.action_delete)) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info row: Category | Type | Unit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.header_category),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = device.categoryName ?: device.categoryDisplayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column {
                    Text(
                        text = stringResource(Res.string.header_type),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = device.typeName ?: "-",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (device.unitSymbol != null) {
                    Column {
                        Text(
                            text = stringResource(Res.string.header_unit),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = device.unitSymbol,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status chip
            StatusChip(isActive = device.isActive)
        }
    }
}

private object DevicesTablePreviewData {
    val sampleDevices = listOf(
        Device(
            id = 1L,
            code = "DEV-00001",
            tenantId = 1L,
            sectorId = 1L,
            sectorCode = "SEC-00001",
            name = "Sensor Temperatura Invernadero 1",
            categoryId = Device.CATEGORY_SENSOR,
            categoryName = "SENSOR",
            typeId = 1,
            typeName = "Temperature",
            unitId = 1,
            unitSymbol = "°C",
            isActive = true
        ),
        Device(
            id = 2L,
            code = "DEV-00002",
            tenantId = 1L,
            sectorId = 1L,
            sectorCode = "SEC-00001",
            name = null, // Device without name
            categoryId = Device.CATEGORY_ACTUATOR,
            categoryName = "ACTUATOR",
            typeId = 2,
            typeName = "Valve",
            unitId = null,
            unitSymbol = null,
            isActive = true
        ),
        Device(
            id = 3L,
            code = "DEV-00003",
            tenantId = 1L,
            sectorId = 2L,
            sectorCode = "SEC-00002",
            name = "Sensor CO2 Norte",
            categoryId = Device.CATEGORY_SENSOR,
            categoryName = "SENSOR",
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
private fun DevicesTablePreview() {
    GreenhouseAdminTheme {
        DevicesTable(devices = DevicesTablePreviewData.sampleDevices)
    }
}


@Preview
@Composable
private fun DevicesCardListPreview() {
    GreenhouseAdminTheme {
        DevicesCardList(devices = DevicesTablePreviewData.sampleDevices)
    }
}

