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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.apptolast.greenhouse.admin.data.model.AlertSeverity
import com.apptolast.greenhouse.admin.data.model.AlertStatus
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.alert_status_dismissed
import greenhouseadmin.composeapp.generated.resources.alert_status_read
import greenhouseadmin.composeapp.generated.resources.alert_status_unread
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_date
import greenhouseadmin.composeapp.generated.resources.header_severity
import greenhouseadmin.composeapp.generated.resources.header_status
import greenhouseadmin.composeapp.generated.resources.header_title
import greenhouseadmin.composeapp.generated.resources.severity_critical
import greenhouseadmin.composeapp.generated.resources.severity_high
import greenhouseadmin.composeapp.generated.resources.severity_low
import greenhouseadmin.composeapp.generated.resources.severity_medium
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

/**
 * Table displaying list of alerts with headers and rows.
 */
@Composable
fun AlertsTable(
    alerts: List<Alert>,
    onEditAlert: (Alert) -> Unit = {},
    onDeleteAlert: (Alert) -> Unit = {},
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
                    onDelete = { onDeleteAlert(alert) }
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
            text = stringResource(Res.string.header_title),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = stringResource(Res.string.header_severity),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(Res.string.header_status),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(Res.string.header_date),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
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
private fun AlertTableRow(
    alert: Alert,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TITLE with avatar
        Row(
            modifier = Modifier.weight(1.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlertAvatar(
                initials = alert.initials,
                severity = alert.severity,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = alert.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // SEVERITY
        AlertSeverityBadge(
            severity = alert.severity,
            modifier = Modifier.weight(1f)
        )

        // STATUS
        AlertStatusBadge(
            status = alert.status,
            modifier = Modifier.weight(1f)
        )

        // DATE
        Text(
            text = formatRelativeTime(alert.createdAt),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
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
 * Formats a timestamp to relative time (e.g., "2 hours ago", "1 day ago").
 */
private fun formatRelativeTime(timestamp: Long): String {
    val now = Clock.System.now().toEpochMilliseconds()
    val diff = now - timestamp

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "${days}d ago"
        hours > 0 -> "${hours}h ago"
        minutes > 0 -> "${minutes}m ago"
        else -> "Just now"
    }
}

/**
 * Avatar component for alerts with colored background based on severity.
 */
@Composable
fun AlertAvatar(
    initials: String,
    severity: AlertSeverity,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (severity) {
        AlertSeverity.LOW -> Color(0xFF2196F3) // Blue
        AlertSeverity.MEDIUM -> Color(0xFFFF9800) // Orange
        AlertSeverity.HIGH -> Color(0xFFF44336) // Red
        AlertSeverity.CRITICAL -> Color(0xFF9C27B0) // Purple
    }

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
 * Severity badge for alerts.
 */
@Composable
fun AlertSeverityBadge(
    severity: AlertSeverity,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, textRes) = when (severity) {
        AlertSeverity.LOW -> Triple(
            Color(0xFF2196F3).copy(alpha = 0.15f),
            Color(0xFF2196F3),
            Res.string.severity_low
        )

        AlertSeverity.MEDIUM -> Triple(
            Color(0xFFFF9800).copy(alpha = 0.15f),
            Color(0xFFFF9800),
            Res.string.severity_medium
        )

        AlertSeverity.HIGH -> Triple(
            Color(0xFFF44336).copy(alpha = 0.15f),
            Color(0xFFF44336),
            Res.string.severity_high
        )

        AlertSeverity.CRITICAL -> Triple(
            Color(0xFF9C27B0).copy(alpha = 0.15f),
            Color(0xFF9C27B0),
            Res.string.severity_critical
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Status badge for alerts.
 */
@Composable
fun AlertStatusBadge(
    status: AlertStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, textRes) = when (status) {
        AlertStatus.UNREAD -> Triple(
            Color(0xFFF44336).copy(alpha = 0.15f),
            Color(0xFFF44336),
            Res.string.alert_status_unread
        )

        AlertStatus.READ -> Triple(
            Color(0xFF00E676).copy(alpha = 0.15f),
            Color(0xFF00E676),
            Res.string.alert_status_read
        )

        AlertStatus.DISMISSED -> Triple(
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.onSurfaceVariant,
            Res.string.alert_status_dismissed
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

private object AlertsTablePreviewData {
    val sampleAlerts = listOf(
        Alert(
            id = "1",
            title = "Temperatura alta en Sector A",
            severity = AlertSeverity.HIGH,
            status = AlertStatus.UNREAD,
            createdAt = Clock.System.now().toEpochMilliseconds() - 3600000,
            clientId = "client1"
        ),
        Alert(
            id = "2",
            title = "Humedad baja detectada",
            severity = AlertSeverity.MEDIUM,
            status = AlertStatus.READ,
            createdAt = Clock.System.now().toEpochMilliseconds() - 86400000,
            clientId = "client1"
        ),
        Alert(
            id = "3",
            title = "Sensor CO2 desconectado",
            severity = AlertSeverity.CRITICAL,
            status = AlertStatus.UNREAD,
            createdAt = Clock.System.now().toEpochMilliseconds() - 1800000,
            clientId = "client1"
        )
    )
}

@Preview
@Composable
private fun AlertsTablePreview() {
    GreenhouseAdminTheme {
        AlertsTable(alerts = AlertsTablePreviewData.sampleAlerts)
    }
}

@Preview
@Composable
private fun AlertAvatarPreview() {
    GreenhouseAdminTheme {
        Row {
            AlertAvatar(initials = "TA", severity = AlertSeverity.LOW)
            Spacer(modifier = Modifier.width(8.dp))
            AlertAvatar(initials = "HB", severity = AlertSeverity.MEDIUM)
            Spacer(modifier = Modifier.width(8.dp))
            AlertAvatar(initials = "SC", severity = AlertSeverity.HIGH)
            Spacer(modifier = Modifier.width(8.dp))
            AlertAvatar(initials = "CR", severity = AlertSeverity.CRITICAL)
        }
    }
}
