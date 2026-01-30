package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.CopyableIdCell
import com.apptolast.greenhouse.admin.presentation.ui.components.common.StatusChip
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_actuator_state
import greenhouseadmin.composeapp.generated.resources.header_id
import greenhouseadmin.composeapp.generated.resources.header_parameter
import greenhouseadmin.composeapp.generated.resources.header_sector
import greenhouseadmin.composeapp.generated.resources.header_status
import greenhouseadmin.composeapp.generated.resources.header_value
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Enum representing sortable columns in the Settings table.
 */
private enum class SettingSortColumn {
    ID
}

/**
 * Adaptive component that shows a table on larger screens and cards on compact screens.
 */
@Composable
fun SettingsTableOrCards(
    settings: List<Setting>,
    sectors: List<Sector> = emptyList(),
    greenhouses: List<Greenhouse> = emptyList(),
    onEditSetting: (Setting) -> Unit = {},
    onDeleteSetting: (Setting) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    if (windowInfo.isCompact) {
        SettingsCardList(
            settings = settings,
            sectors = sectors,
            greenhouses = greenhouses,
            onEditSetting = onEditSetting,
            onDeleteSetting = onDeleteSetting,
            onCopyId = onCopyId,
            modifier = modifier
        )
    } else {
        SettingsTable(
            settings = settings,
            sectors = sectors,
            greenhouses = greenhouses,
            onEditSetting = onEditSetting,
            onDeleteSetting = onDeleteSetting,
            onCopyId = onCopyId,
            modifier = modifier
        )
    }
}

/**
 * Table displaying list of settings with headers and rows.
 * Structure: ID | PARAMETER | SECTOR | ACTUATOR STATE | VALUE | STATUS | ACTIONS
 * Sortable columns: ID
 */
@Composable
fun SettingsTable(
    settings: List<Setting>,
    sectors: List<Sector> = emptyList(),
    greenhouses: List<Greenhouse> = emptyList(),
    onEditSetting: (Setting) -> Unit = {},
    onDeleteSetting: (Setting) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var sortColumn by remember { mutableStateOf<SettingSortColumn?>(null) }
    var sortDirection by remember { mutableStateOf(SortDirection.ASCENDING) }

    // Sort settings based on selected column and direction
    val sortedSettings = remember(settings, sortColumn, sortDirection) {
        when (sortColumn) {
            SettingSortColumn.ID -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    settings.sortedBy { it.code }
                } else {
                    settings.sortedByDescending { it.code }
                }
            }

            null -> settings
        }
    }

    fun onHeaderClick(column: SettingSortColumn) {
        if (sortColumn == column) {
            sortDirection = sortDirection.toggle()
        } else {
            sortColumn = column
            sortDirection = SortDirection.ASCENDING
        }
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Header row with sorting
            SettingsTableHeader(
                sortColumn = sortColumn,
                sortDirection = sortDirection,
                onSortClick = ::onHeaderClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Setting rows
            sortedSettings.forEach { setting ->
                val sector = sectors.find { it.id == setting.sectorId }
                val greenhouse = sector?.let { s -> greenhouses.find { it.id == s.greenhouseId } }
                SettingTableRow(
                    setting = setting,
                    sectorName = sector?.displayName,
                    greenhouseName = greenhouse?.name,
                    onEdit = { onEditSetting(setting) },
                    onDelete = { onDeleteSetting(setting) },
                    onCopyId = onCopyId
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun SettingsTableHeader(
    sortColumn: SettingSortColumn?,
    sortDirection: SortDirection,
    onSortClick: (SettingSortColumn) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // ID - Sortable
        SettingSortableHeader(
            text = stringResource(Res.string.header_id),
            column = SettingSortColumn.ID,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(SettingSortColumn.ID) },
            modifier = Modifier.weight(0.4f)
        )
        // PARAMETER - Not sortable
        Text(
            text = stringResource(Res.string.header_parameter),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.9f)
        )
        // SECTOR - Not sortable
        Text(
            text = stringResource(Res.string.header_sector),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f)
        )
        // ACTUATOR STATE - Not sortable
        Text(
            text = stringResource(Res.string.header_actuator_state),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f)
        )
        // VALUE - Not sortable
        Text(
            text = stringResource(Res.string.header_value),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.5f)
        )
        // STATUS - Not sortable
        Text(
            text = stringResource(Res.string.header_status),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.5f)
        )
        // ACTIONS - Not sortable
        Text(
            text = stringResource(Res.string.header_actions),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Sortable header cell with sort icon for Settings table.
 */
@Composable
private fun SettingSortableHeader(
    text: String,
    column: SettingSortColumn,
    currentSortColumn: SettingSortColumn?,
    sortDirection: SortDirection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = currentSortColumn == column

    Row(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.width(4.dp))
        if (isActive) {
            Icon(
                imageVector = if (sortDirection == SortDirection.ASCENDING) {
                    Icons.Outlined.ArrowUpward
                } else {
                    Icons.Outlined.ArrowDownward
                },
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            // Show a subtle indicator that this column is sortable
            Icon(
                imageVector = Icons.Outlined.ArrowUpward,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun SettingTableRow(
    setting: Setting,
    sectorName: String? = null,
    greenhouseName: String? = null,
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
            id = setting.code,
            onCopyId = onCopyId,
            modifier = Modifier.weight(0.4f)
        )

        // PARAMETER
        Text(
            text = setting.displayName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.9f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // SECTOR - Sector name with greenhouse in subtitle
        Column(modifier = Modifier.weight(0.6f)) {
            Text(
                text = sectorName ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            greenhouseName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // ACTUATOR STATE badge
        ActuatorStateBadge(
            stateName = setting.actuatorStateDisplayName,
            modifier = Modifier.weight(0.6f).wrapContentWidth(align = Alignment.Start),
        )

        // VALUE
        Text(
            text = setting.valueDisplay,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // STATUS - Using StatusChip
        StatusChip(
            isActive = setting.isActive,
            modifier = Modifier.weight(0.5f).wrapContentWidth(align = Alignment.Start)
        )

        // ACTIONS
        Row(
            modifier = Modifier.width(80.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.action_edit),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.action_delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Avatar component for settings with colored background.
 */
@Composable
fun SettingAvatar(
    initials: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFF607D8B)), // Blue grey color for settings
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Badge component for displaying actuator state (ON/OFF/AUTO/etc.).
 */
@Composable
private fun ActuatorStateBadge(
    stateName: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (stateName.uppercase()) {
        "ON" -> Color(0xFF4CAF50).copy(alpha = 0.2f) // Green tint
        "OFF" -> Color(0xFFE53935).copy(alpha = 0.2f) // Red tint
        "AUTO" -> Color(0xFF2196F3).copy(alpha = 0.2f) // Blue tint
        "MANUAL" -> Color(0xFFFFB74D).copy(alpha = 0.2f) // Orange tint
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when (stateName.uppercase()) {
        "ON" -> Color(0xFF2E7D32) // Dark green
        "OFF" -> Color(0xFFC62828) // Dark red
        "AUTO" -> Color(0xFF1565C0) // Dark blue
        "MANUAL" -> Color(0xFFFF8F00) // Dark orange
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier.wrapContentWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = stateName,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Card list for displaying settings on compact screens.
 */
@Composable
private fun SettingsCardList(
    settings: List<Setting>,
    sectors: List<Sector> = emptyList(),
    greenhouses: List<Greenhouse> = emptyList(),
    onEditSetting: (Setting) -> Unit,
    onDeleteSetting: (Setting) -> Unit,
    onCopyId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        settings.forEach { setting ->
            val sector = sectors.find { it.id == setting.sectorId }
            val greenhouse = sector?.let { s -> greenhouses.find { it.id == s.greenhouseId } }
            SettingCard(
                setting = setting,
                sectorName = sector?.displayName,
                greenhouseName = greenhouse?.name,
                onEdit = { onEditSetting(setting) },
                onDelete = { onDeleteSetting(setting) },
                onCopyId = { onCopyId(setting.code) }
            )
        }
    }
}

/**
 * Individual setting card for compact screens with dropdown menu for actions.
 */
@Composable
private fun SettingCard(
    setting: Setting,
    sectorName: String? = null,
    greenhouseName: String? = null,
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
            // Header row: Icon, Parameter name, Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = setting.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = setting.valueDisplay,
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

            // Info row: Actuator State badge | Status chip
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActuatorStateBadge(stateName = setting.actuatorStateDisplayName)
                StatusChip(isActive = setting.isActive)
            }
        }
    }
}

private object SettingsTablePreviewData {
    val sampleSettings = listOf(
        Setting(
            id = 1L,
            code = "SET-00001",
            sectorId = 1L,
            sectorCode = "SEC-00001",
            tenantId = 1L,
            parameterId = 1,
            parameterName = "Temperature",
            actuatorStateId = 1,
            actuatorStateName = "ON",
            value = "25",
            description = null,
            isActive = true,
            createdAt = "2024-01-15T10:30:00Z"
        ),
        Setting(
            id = 2L,
            code = "SET-00002",
            sectorId = 1L,
            sectorCode = "SEC-00001",
            tenantId = 1L,
            parameterId = 2,
            parameterName = "Humidity",
            actuatorStateId = 2,
            actuatorStateName = "OFF",
            value = "80",
            description = "Max humidity threshold",
            isActive = true,
            createdAt = "2024-01-15T10:30:00Z"
        ),
        Setting(
            id = 3L,
            code = "SET-00003",
            sectorId = 2L,
            sectorCode = "SEC-00002",
            tenantId = 1L,
            parameterId = 1,
            parameterName = "Temperature",
            actuatorStateId = 3,
            actuatorStateName = "AUTO",
            value = null,
            description = null,
            isActive = false,
            createdAt = "2024-01-15T10:30:00Z"
        )
    )
}

@Preview
@Composable
private fun SettingsTablePreview() {
    GreenhouseAdminTheme {
        SettingsTable(settings = SettingsTablePreviewData.sampleSettings)
    }
}

@Preview
@Composable
private fun SettingAvatarPreview() {
    GreenhouseAdminTheme {
        Row {
            SettingAvatar(initials = "TE")
            Spacer(modifier = Modifier.width(8.dp))
            SettingAvatar(initials = "HU")
            Spacer(modifier = Modifier.width(8.dp))
            SettingAvatar(initials = "LI")
        }
    }
}

@Preview
@Composable
private fun ActuatorStateBadgePreview() {
    GreenhouseAdminTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActuatorStateBadge(stateName = "ON")
            ActuatorStateBadge(stateName = "OFF")
            ActuatorStateBadge(stateName = "AUTO")
            ActuatorStateBadge(stateName = "MANUAL")
        }
    }
}
