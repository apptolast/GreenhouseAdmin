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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.CopyableIdCell
import com.apptolast.greenhouse.admin.presentation.ui.components.common.SeverityChip
import com.apptolast.greenhouse.admin.presentation.ui.components.common.StatusChip
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.action_reopen
import greenhouseadmin.composeapp.generated.resources.action_resolve
import greenhouseadmin.composeapp.generated.resources.alert_active
import greenhouseadmin.composeapp.generated.resources.alert_resolved
import greenhouseadmin.composeapp.generated.resources.copy_id
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_date
import greenhouseadmin.composeapp.generated.resources.header_id
import greenhouseadmin.composeapp.generated.resources.header_message
import greenhouseadmin.composeapp.generated.resources.header_sector
import greenhouseadmin.composeapp.generated.resources.header_severity
import greenhouseadmin.composeapp.generated.resources.header_status
import greenhouseadmin.composeapp.generated.resources.header_type
import greenhouseadmin.composeapp.generated.resources.label_not_assigned
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Enum representing sortable columns in the Alerts table.
 */
enum class AlertSortColumn {
    ID, MESSAGE, SEVERITY, DATE
}

/**
 * Enum representing sort direction.
 */
enum class SortDirection {
    ASCENDING, DESCENDING;

    fun toggle(): SortDirection = when (this) {
        ASCENDING -> DESCENDING
        DESCENDING -> ASCENDING
    }
}

/**
 * Adaptive component that shows a table on larger screens and cards on compact screens.
 */
@Composable
fun AlertsTableOrCards(
    alerts: List<Alert>,
    sectors: List<Sector> = emptyList(),
    greenhouses: List<Greenhouse> = emptyList(),
    onEditAlert: (Alert) -> Unit = {},
    onDeleteAlert: (Alert) -> Unit = {},
    onResolveAlert: (Alert) -> Unit = {},
    onReopenAlert: (Alert) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    if (windowInfo.isCompact) {
        AlertsCardList(
            alerts = alerts,
            sectors = sectors,
            greenhouses = greenhouses,
            onEditAlert = onEditAlert,
            onDeleteAlert = onDeleteAlert,
            onResolveAlert = onResolveAlert,
            onReopenAlert = onReopenAlert,
            onCopyId = onCopyId,
            modifier = modifier
        )
    } else {
        AlertsTable(
            alerts = alerts,
            sectors = sectors,
            greenhouses = greenhouses,
            onEditAlert = onEditAlert,
            onDeleteAlert = onDeleteAlert,
            onResolveAlert = onResolveAlert,
            onReopenAlert = onReopenAlert,
            onCopyId = onCopyId,
            modifier = modifier
        )
    }
}

/**
 * Table displaying list of alerts with headers and rows.
 * Columns: ID | MESSAGE | SECTOR | TYPE | SEVERITY | STATUS | DATE | ACTIONS
 * Sortable columns: ID, MESSAGE, SEVERITY, DATE
 */
@Composable
fun AlertsTable(
    alerts: List<Alert>,
    sectors: List<Sector> = emptyList(),
    greenhouses: List<Greenhouse> = emptyList(),
    onEditAlert: (Alert) -> Unit = {},
    onDeleteAlert: (Alert) -> Unit = {},
    onResolveAlert: (Alert) -> Unit = {},
    onReopenAlert: (Alert) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var sortColumn by remember { mutableStateOf<AlertSortColumn?>(null) }
    var sortDirection by remember { mutableStateOf(SortDirection.ASCENDING) }

    // Sort alerts based on selected column and direction
    val sortedAlerts = remember(alerts, sortColumn, sortDirection) {
        when (sortColumn) {
            AlertSortColumn.ID -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    alerts.sortedBy { it.code }
                } else {
                    alerts.sortedByDescending { it.code }
                }
            }

            AlertSortColumn.MESSAGE -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    alerts.sortedBy { it.displayText.lowercase() }
                } else {
                    alerts.sortedByDescending { it.displayText.lowercase() }
                }
            }

            AlertSortColumn.SEVERITY -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    alerts.sortedBy { it.severityLevel ?: Short.MAX_VALUE }
                } else {
                    alerts.sortedByDescending { it.severityLevel ?: Short.MIN_VALUE }
                }
            }

            AlertSortColumn.DATE -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    alerts.sortedBy { it.createdAt }
                } else {
                    alerts.sortedByDescending { it.createdAt }
                }
            }

            null -> alerts
        }
    }

    fun onHeaderClick(column: AlertSortColumn) {
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
            AlertsTableHeader(
                sortColumn = sortColumn,
                sortDirection = sortDirection,
                onSortClick = ::onHeaderClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Alert rows
            sortedAlerts.forEach { alert ->
                val sector = sectors.find { it.id == alert.sectorId }
                val greenhouse = sector?.let { s -> greenhouses.find { it.id == s.greenhouseId } }
                AlertTableRow(
                    alert = alert,
                    sectorName = sector?.displayName,
                    greenhouseName = greenhouse?.name,
                    onEdit = { onEditAlert(alert) },
                    onDelete = { onDeleteAlert(alert) },
                    onResolve = { onResolveAlert(alert) },
                    onReopen = { onReopenAlert(alert) },
                    onCopyId = { onCopyId(alert.code) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun AlertsTableHeader(
    sortColumn: AlertSortColumn?,
    sortDirection: SortDirection,
    onSortClick: (AlertSortColumn) -> Unit,
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
        SortableHeader(
            text = stringResource(Res.string.header_id),
            column = AlertSortColumn.ID,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(AlertSortColumn.ID) },
            modifier = Modifier.weight(1f)
        )
        // MESSAGE - Sortable
        SortableHeader(
            text = stringResource(Res.string.header_message),
            column = AlertSortColumn.MESSAGE,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(AlertSortColumn.MESSAGE) },
            modifier = Modifier.weight(1f)
        )
        // SECTOR - Not sortable
        Text(
            text = stringResource(Res.string.header_sector),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.9f)
        )
        // TYPE - Not sortable
        Text(
            text = stringResource(Res.string.header_type),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f)
        )
        // SEVERITY - Sortable
        SortableHeader(
            text = stringResource(Res.string.header_severity),
            column = AlertSortColumn.SEVERITY,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(AlertSortColumn.SEVERITY) },
            modifier = Modifier.weight(0.7f)
        )
        // STATUS - Not sortable
        Text(
            text = stringResource(Res.string.header_status),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f)
        )
        // DATE - Sortable
        SortableHeader(
            text = stringResource(Res.string.header_date),
            column = AlertSortColumn.DATE,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(AlertSortColumn.DATE) },
            modifier = Modifier.weight(0.8f)
        )
        // ACTIONS - Not sortable
        Text(
            text = stringResource(Res.string.header_actions),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(120.dp),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Sortable header cell with sort icon.
 */
@Composable
private fun SortableHeader(
    text: String,
    column: AlertSortColumn,
    currentSortColumn: AlertSortColumn?,
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
private fun AlertTableRow(
    alert: Alert,
    sectorName: String? = null,
    greenhouseName: String? = null,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onResolve: () -> Unit,
    onReopen: () -> Unit,
    onCopyId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val notAssignedText = stringResource(Res.string.label_not_assigned)
    val activeText = stringResource(Res.string.alert_active)
    val resolvedText = stringResource(Res.string.alert_resolved)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // ID - Copyable code
        CopyableIdCell(
            id = alert.code,
            onCopyId = onCopyId,
            modifier = Modifier.weight(1f)
        )

        // MESSAGE with avatar
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            AlertAvatar(
//                initials = alert.initials,
//                severityLevel = alert.severityLevel,
//                modifier = Modifier.size(32.dp)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = alert.displayText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // SECTOR - Sector name with greenhouse in subtitle
        Column(modifier = Modifier.weight(0.9f)) {
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

        // TYPE
        Text(
            text = alert.alertTypeName ?: notAssignedText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // SEVERITY - Using SeverityChip with wrapContentWidth
        SeverityChip(
            name = alert.severityName,
            level = alert.severityLevel,
            modifier = Modifier.weight(0.7f).wrapContentWidth(align = Alignment.Start)
        )

        // STATUS - Using StatusChip with wrapContentWidth
        StatusChip(
            isActive = !alert.isResolved,
            activeText = activeText,
            inactiveText = resolvedText,
            modifier = Modifier.weight(0.6f).wrapContentWidth(align = Alignment.Start)
        )

        // DATE
        Text(
            text = formatDateString(alert.createdAt),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f)
        )

        // ACTIONS
        Row(
            modifier = Modifier.width(120.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Resolve/Reopen button
            if (alert.isResolved) {
                IconButton(
                    onClick = onReopen,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(Res.string.action_reopen),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onResolve,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(Res.string.action_resolve),
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
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
 * Formats an ISO date string to a more readable format.
 */
private fun formatDateString(dateString: String): String {
    return try {
        // Parse ISO format: "2024-01-15T10:30:00Z"
        val parts = dateString.split("T")
        if (parts.size >= 2) {
            val datePart = parts[0] // "2024-01-15"
            val timePart = parts[1].split(":").take(2).joinToString(":") // "10:30"
            "$datePart $timePart"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

/**
 * Avatar component for alerts with colored background based on severity level.
 */
@Composable
fun AlertAvatar(
    initials: String,
    severityLevel: Short?,
    modifier: Modifier = Modifier
) {
    val backgroundColor = getSeverityColor(severityLevel)

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
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
 * Get color based on severity level.
 * Default colors when API color is not available.
 */
private fun getSeverityColor(severityLevel: Short?): Color {
    return when (severityLevel?.toInt()) {
        1 -> Color(0xFF2196F3) // Blue - Info/Low
        2 -> Color(0xFFFF9800) // Orange - Medium/Warning
        3 -> Color(0xFFF44336) // Red - High
        4 -> Color(0xFF9C27B0) // Purple - Critical
        else -> Color(0xFF757575) // Gray - Unknown/Not assigned
    }
}

/**
 * Card list for displaying alerts on compact screens.
 */
@Composable
private fun AlertsCardList(
    alerts: List<Alert>,
    sectors: List<Sector> = emptyList(),
    greenhouses: List<Greenhouse> = emptyList(),
    modifier: Modifier = Modifier,
    onEditAlert: (Alert) -> Unit = {},
    onDeleteAlert: (Alert) -> Unit = {},
    onResolveAlert: (Alert) -> Unit = {},
    onReopenAlert: (Alert) -> Unit = {},
    onCopyId: (String) -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        alerts.forEach { alert ->
            val sector = sectors.find { it.id == alert.sectorId }
            val greenhouse = sector?.let { s -> greenhouses.find { it.id == s.greenhouseId } }
            AlertCard(
                alert = alert,
                sectorName = sector?.displayName,
                greenhouseName = greenhouse?.name,
                onEdit = { onEditAlert(alert) },
                onDelete = { onDeleteAlert(alert) },
                onResolve = { onResolveAlert(alert) },
                onReopen = { onReopenAlert(alert) },
                onCopyId = { onCopyId(alert.code) }
            )
        }
    }
}

/**
 * Individual alert card for compact screens with dropdown menu for actions.
 */
@Composable
private fun AlertCard(
    alert: Alert,
    sectorName: String? = null,
    greenhouseName: String? = null,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onResolve: () -> Unit,
    onReopen: () -> Unit,
    onCopyId: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val activeText = stringResource(Res.string.alert_active)
    val resolvedText = stringResource(Res.string.alert_resolved)
    val notAssignedText = stringResource(Res.string.label_not_assigned)

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
            // Header row: Severity icon, Message, Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(getSeverityColor(alert.severityLevel)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alert.displayText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    // ID row with copy functionality
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onCopyId,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = stringResource(Res.string.copy_id),
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = alert.code,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
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

            // Info row: Sector | Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.header_sector),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = sectorName ?: notAssignedText,
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.header_type),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = alert.alertTypeName ?: notAssignedText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer row: Chips + Resolve/Reopen action + Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SeverityChip(
                        name = alert.severityName,
                        level = alert.severityLevel
                    )
                    StatusChip(
                        isActive = !alert.isResolved,
                        activeText = activeText,
                        inactiveText = resolvedText
                    )
                    // Resolve/Reopen action button
                    if (alert.isResolved) {
                        IconButton(
                            onClick = onReopen,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(Res.string.action_reopen),
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onResolve,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = stringResource(Res.string.action_resolve),
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
                Text(
                    text = formatDateString(alert.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

val mockAlerts = listOf(
    Alert(
        id = 1,
        code = "ALT-00001",
        tenantId = 1,
        sectorId = 1,
        sectorCode = "SEC-00001",
        alertTypeId = 1,
        alertTypeName = "Temperature",
        severityId = 3,
        severityName = "High",
        severityLevel = 3,
        message = "Temperature exceeds threshold in Sector A",
        description = null,
        isResolved = false,
        resolvedAt = null,
        resolvedByUserName = null,
        createdAt = "2024-01-15T10:30:00Z"
    ),
    Alert(
        id = 2,
        code = "ALT-00002",
        tenantId = 1,
        sectorId = 1,
        sectorCode = "SEC-00001",
        alertTypeId = 2,
        alertTypeName = "Humidity",
        severityId = 2,
        severityName = "Medium",
        severityLevel = 2,
        message = "Humidity below optimal range",
        description = null,
        isResolved = true,
        resolvedAt = "2024-01-15T12:00:00Z",
        resolvedByUserName = "Admin User",
        createdAt = "2024-01-14T08:00:00Z"
    ),
    Alert(
        id = 3,
        code = "ALT-00003",
        tenantId = 1,
        sectorId = 2,
        sectorCode = "SEC-00002",
        alertTypeId = null,
        alertTypeName = null,
        severityId = 4,
        severityName = "Critical",
        severityLevel = 4,
        message = null,
        description = "CO2 sensor disconnected - requires immediate attention",
        isResolved = false,
        resolvedAt = null,
        resolvedByUserName = null,
        createdAt = "2024-01-15T09:45:00Z"
    )
)

@Preview
@Composable
private fun AlertsTablePreview() {
    GreenhouseAdminTheme {
        AlertsTable(
            alerts = mockAlerts
        )
    }
}

@Preview
@Composable
private fun AlertsCardListPreview() {
    GreenhouseAdminTheme {
        AlertsCardList(
            alerts = mockAlerts
        )
    }
}
