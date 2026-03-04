package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.TabContentWrapper
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.alerts_empty
import greenhouseadmin.composeapp.generated.resources.alerts_subtitle
import greenhouseadmin.composeapp.generated.resources.alerts_title
import greenhouseadmin.composeapp.generated.resources.new_alert
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
    TabContentWrapper(
        title = Res.string.alerts_title,
        subtitle = Res.string.alerts_subtitle,
        addButtonText = Res.string.new_alert,
        items = alerts,
        isLoading = isLoading,
        error = error,
        emptyMessage = Res.string.alerts_empty,
        onAdd = onAddAlert,
        onRetry = onRetry,
        modifier = modifier
    ) { alertList ->
        AlertsTableOrCards(
            alerts = alertList,
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
