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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.alerts_empty
import greenhouseadmin.composeapp.generated.resources.alerts_subtitle
import greenhouseadmin.composeapp.generated.resources.alerts_title
import greenhouseadmin.composeapp.generated.resources.new_alert
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Alerts tab content for the client detail screen.
 * Displays a table of alerts with add/edit/delete/resolve/reopen functionality.
 * On compact screens, the add button is hidden (FAB is shown by parent).
 */
@Composable
fun ClientDetailAlertsTab(
    alerts: List<Alert>,
    sectors: List<Sector> = emptyList(),
    greenhouses: List<Greenhouse> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null,
    onAddAlert: () -> Unit = {},
    onEditAlert: (Alert) -> Unit = {},
    onDeleteAlert: (Alert) -> Unit = {},
    onResolveAlert: (Alert) -> Unit = {},
    onReopenAlert: (Alert) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Header with title and Add button (button hidden on compact - FAB shown by parent)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(Res.string.alerts_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(Res.string.alerts_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Show button only on expanded screens
            if (!windowInfo.isCompact) {
                Button(
                    onClick = onAddAlert,
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
                    Text(stringResource(Res.string.new_alert))
                }
            }
        }

        // Content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                ErrorContent(
                    message = error,
                    onRetry = onRetry
                )
            }

            alerts.isEmpty() -> {
                EmptyAlertsContent()
            }

            else -> {
                AlertsTableOrCards(
                    alerts = alerts,
                    sectors = sectors,
                    greenhouses = greenhouses,
                    onEditAlert = onEditAlert,
                    onDeleteAlert = onDeleteAlert,
                    onResolveAlert = onResolveAlert,
                    onReopenAlert = onReopenAlert,
                    onCopyId = onCopyId
                )
            }
        }
    }
}

@Composable
private fun EmptyAlertsContent(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.alerts_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private object ClientDetailAlertsTabPreviewData {
    val sampleAlerts = listOf(
        Alert(
            id = 1L,
            code = "ALT-00001",
            tenantId = 1L,
            sectorId = 1L,
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
            id = 2L,
            code = "ALT-00002",
            tenantId = 1L,
            sectorId = 1L,
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
        )
    )
}

@Preview
@Composable
private fun ClientDetailAlertsTabPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailAlertsTab(alerts = ClientDetailAlertsTabPreviewData.sampleAlerts)
        }
    }
}

@Preview
@Composable
private fun ClientDetailAlertsTabEmptyPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailAlertsTab(alerts = emptyList())
        }
    }
}

@Preview
@Composable
private fun ClientDetailAlertsTabLoadingPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailAlertsTab(alerts = emptyList(), isLoading = true)
        }
    }
}
