package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.Alert
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
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_date
import greenhouseadmin.composeapp.generated.resources.header_greenhouse
import greenhouseadmin.composeapp.generated.resources.header_id
import greenhouseadmin.composeapp.generated.resources.header_message
import greenhouseadmin.composeapp.generated.resources.header_severity
import greenhouseadmin.composeapp.generated.resources.header_status
import greenhouseadmin.composeapp.generated.resources.header_type
import greenhouseadmin.composeapp.generated.resources.label_not_assigned
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Table displaying list of alerts with headers and rows.
 * Columns: ID | MESSAGE | GREENHOUSE | TYPE | SEVERITY | STATUS | DATE | ACTIONS
 */
@Composable
fun AlertsTable(
    alerts: List<Alert>,
    onEditAlert: (Alert) -> Unit = {},
    onDeleteAlert: (Alert) -> Unit = {},
    onResolveAlert: (Alert) -> Unit = {},
    onReopenAlert: (Alert) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Header row
            AlertsTableHeader()
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Alert rows
            alerts.forEach { alert ->
                AlertTableRow(
                    alert = alert,
                    onEdit = { onEditAlert(alert) },
                    onDelete = { onDeleteAlert(alert) },
                    onResolve = { onResolveAlert(alert) },
                    onReopen = { onReopenAlert(alert) },
                    onCopyId = { onCopyId(alert.id) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun AlertsTableHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.header_id),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(Res.string.header_message),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = stringResource(Res.string.header_greenhouse),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = stringResource(Res.string.header_type),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = stringResource(Res.string.header_severity),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = stringResource(Res.string.header_status),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = stringResource(Res.string.header_date),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = stringResource(Res.string.header_actions),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(120.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AlertTableRow(
    alert: Alert,
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ID - Copyable
        CopyableIdCell(
            id = alert.id,
            onCopyId = onCopyId,
            modifier = Modifier.weight(1f)
        )

        // MESSAGE with avatar
        Row(
            modifier = Modifier.weight(1.3f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlertAvatar(
                initials = alert.initials,
                severityLevel = alert.severityLevel,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // GREENHOUSE
        Text(
            text = alert.greenhouseName ?: notAssignedText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.9f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // TYPE
        Text(
            text = alert.alertTypeName ?: notAssignedText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // SEVERITY - Using new SeverityChip
        SeverityChip(
            name = alert.severityName,
            level = alert.severityLevel,
            modifier = Modifier.weight(0.8f)
        )

        // STATUS - Using new StatusChip (inverted: resolved=active, active=inactive)
        StatusChip(
            isActive = !alert.isResolved,
            activeText = activeText,
            inactiveText = resolvedText,
            modifier = Modifier.weight(0.8f)
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

@Preview
@Composable
private fun AlertsTablePreview() {
    GreenhouseAdminTheme {
        AlertsTable(
            alerts = listOf(
                Alert(
                    id = "1",
                    tenantId = "t1",
                    greenhouseId = "g1",
                    greenhouseName = "Greenhouse A",
                    alertTypeId = 1,
                    alertTypeName = "Temperature",
                    severityId = 3,
                    severityName = "High",
                    severityLevel = 3,
                    message = "Temperature exceeds threshold in Sector A",
                    isResolved = false,
                    resolvedAt = null,
                    resolvedByUserName = null,
                    createdAt = "2024-01-15T10:30:00Z"
                ),
                Alert(
                    id = "2",
                    tenantId = "t1",
                    greenhouseId = "g1",
                    greenhouseName = "Greenhouse A",
                    alertTypeId = 2,
                    alertTypeName = "Humidity",
                    severityId = 2,
                    severityName = "Medium",
                    severityLevel = 2,
                    message = "Humidity below optimal range",
                    isResolved = true,
                    resolvedAt = "2024-01-15T12:00:00Z",
                    resolvedByUserName = "Admin User",
                    createdAt = "2024-01-14T08:00:00Z"
                ),
                Alert(
                    id = "3",
                    tenantId = "t1",
                    greenhouseId = "g2",
                    greenhouseName = "Greenhouse B",
                    alertTypeId = null,
                    alertTypeName = null,
                    severityId = 4,
                    severityName = "Critical",
                    severityLevel = 4,
                    message = "CO2 sensor disconnected",
                    isResolved = false,
                    resolvedAt = null,
                    resolvedByUserName = null,
                    createdAt = "2024-01-15T09:45:00Z"
                )
            )
        )
    }
}

@Preview
@Composable
private fun AlertAvatarPreview() {
    GreenhouseAdminTheme {
        Row {
            AlertAvatar(initials = "TA", severityLevel = 1)
            Spacer(modifier = Modifier.width(8.dp))
            AlertAvatar(initials = "HB", severityLevel = 2)
            Spacer(modifier = Modifier.width(8.dp))
            AlertAvatar(initials = "SC", severityLevel = 3)
            Spacer(modifier = Modifier.width(8.dp))
            AlertAvatar(initials = "CR", severityLevel = 4)
        }
    }
}
