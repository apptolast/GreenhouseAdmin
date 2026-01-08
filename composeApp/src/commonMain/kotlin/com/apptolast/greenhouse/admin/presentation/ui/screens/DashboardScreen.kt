package com.apptolast.greenhouse.admin.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.RecentAlert
import com.apptolast.greenhouse.admin.data.model.RecentClient
import com.apptolast.greenhouse.admin.data.model.StatCard
import com.apptolast.greenhouse.admin.data.model.StatCardIcon
import com.apptolast.greenhouse.admin.data.model.StatCardSubtitleColor
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.AdaptiveDimens
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.DashboardTopBar
import com.apptolast.greenhouse.admin.presentation.ui.components.common.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.components.common.LoadingContent
import com.apptolast.greenhouse.admin.presentation.ui.components.common.StatusChip
import com.apptolast.greenhouse.admin.presentation.ui.components.dashboard.StatsCard
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.DashboardEvent
import com.apptolast.greenhouse.admin.presentation.viewmodel.DashboardUiState
import com.apptolast.greenhouse.admin.presentation.viewmodel.DashboardViewModel
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.app_name
import greenhouseadmin.composeapp.generated.resources.dashboard_overview
import greenhouseadmin.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main dashboard screen composable.
 * Follows MVI pattern - receives ViewModel from Koin, no state parameters.
 * Navigation is handled by AdaptiveScaffold at the app level.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

/**
 * Stateless dashboard content composable.
 * Receives state and event handler for testability.
 */
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onEvent: (DashboardEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar
        DashboardTopBar(
            title = stringResource(Res.string.app_name),
            subtitle = stringResource(Res.string.dashboard_overview),
            searchQuery = uiState.searchQuery,
            alertCount = uiState.alertCount,
            onSearchQueryChange = { onEvent(DashboardEvent.OnSearchQueryChanged(it)) },
            onAlertClick = { onEvent(DashboardEvent.OnAlertIconClicked) }
        )

        // Content area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }

                uiState.isError -> {
                    ErrorContent(
                        message = uiState.error ?: stringResource(Res.string.error_unknown),
                        onRetry = { onEvent(DashboardEvent.LoadDashboard) }
                    )
                }

                else -> {
                    DashboardContentBody(uiState = uiState)
                }
            }
        }
    }
}

/**
 * Main dashboard body with all sections.
 * Adapts layout based on screen size - stacks elements vertically on compact screens.
 */
@Composable
private fun DashboardContentBody(
    uiState: DashboardUiState,
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current
    val contentPadding = AdaptiveDimens.contentPadding()
    val spacing = AdaptiveDimens.verticalSpacing()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(contentPadding),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Main KPIs Grid - adapts columns based on screen size
        item {
            val gridHeight = if (windowInfo.isCompact) 420.dp else 200.dp
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 220.dp),
                modifier = Modifier.fillMaxWidth().height(gridHeight),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                items(
                    items = uiState.statCards,
                    key = { it.id }
                ) { card ->
                    StatsCard(
                        statCard = card,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Secondary KPIs - Row on desktop, Column on compact
        item {
            SecondaryKpisSection(
                uiState = uiState,
                isCompact = windowInfo.isCompact,
                spacing = spacing
            )
        }

        // Recent sections - Row on desktop, Column on compact
        item {
            RecentSectionsLayout(
                alerts = uiState.recentAlerts,
                clients = uiState.recentClients,
                isCompact = windowInfo.isCompact,
                spacing = spacing
            )
        }
    }
}

/**
 * Secondary KPIs section that adapts to screen size.
 */
@Composable
private fun SecondaryKpisSection(
    uiState: DashboardUiState,
    isCompact: Boolean,
    spacing: androidx.compose.ui.unit.Dp
) {
    val usersCard = @Composable { modifier: Modifier ->
        StatsCard(
            statCard = StatCard(
                id = "users",
                title = "Total Users",
                value = uiState.totalUsers.toString(),
                subtitle = "Registered users",
                subtitleColor = StatCardSubtitleColor.DEFAULT,
                icon = StatCardIcon.USERS
            ),
            modifier = modifier
        )
    }

    val deviceBreakdownCard = @Composable { modifier: Modifier ->
        StatsCard(
            statCard = StatCard(
                id = "device_breakdown",
                title = "Device Breakdown",
                value = "${uiState.deviceBreakdown.sensors}/${uiState.deviceBreakdown.actuators}",
                subtitle = "Sensors / Actuators",
                subtitleColor = StatCardSubtitleColor.DEFAULT,
                icon = StatCardIcon.DEVICES
            ),
            modifier = modifier
        )
    }

    if (isCompact) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            usersCard(Modifier.fillMaxWidth())
            deviceBreakdownCard(Modifier.fillMaxWidth())
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            usersCard(Modifier.weight(1f))
            deviceBreakdownCard(Modifier.weight(1f))
        }
    }
}

/**
 * Recent sections layout that adapts to screen size.
 */
@Composable
private fun RecentSectionsLayout(
    alerts: List<RecentAlert>,
    clients: List<RecentClient>,
    isCompact: Boolean,
    spacing: androidx.compose.ui.unit.Dp
) {
    if (isCompact) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            RecentAlertsCard(
                alerts = alerts,
                modifier = Modifier.fillMaxWidth()
            )
            RecentClientsCard(
                clients = clients,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            RecentAlertsCard(
                alerts = alerts,
                modifier = Modifier.weight(1f)
            )
            RecentClientsCard(
                clients = clients,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Card showing recent alerts list.
 */
@Composable
private fun RecentAlertsCard(
    alerts: List<RecentAlert>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recent Alerts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (alerts.isEmpty()) {
                Text(
                    text = "No active alerts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                alerts.forEach { alert ->
                    RecentAlertItem(alert = alert)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Single alert item in the recent alerts list.
 */
@Composable
private fun RecentAlertItem(
    alert: RecentAlert,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Severity indicator
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(getSeverityColor(alert.severityLevel)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${alert.tenantName} - ${alert.greenhouseName ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = alert.severityName ?: "Unknown",
            style = MaterialTheme.typography.labelSmall,
            color = getSeverityColor(alert.severityLevel)
        )
    }
}

/**
 * Card showing recent clients list.
 */
@Composable
private fun RecentClientsCard(
    clients: List<RecentClient>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recent Clients",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (clients.isEmpty()) {
                Text(
                    text = "No clients found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                clients.forEach { client ->
                    RecentClientItem(client = client)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Single client item in the recent clients list.
 */
@Composable
private fun RecentClientItem(
    client: RecentClient,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Client avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = client.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = client.province ?: "No province",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        StatusChip(isActive = client.isActive)
    }
}

/**
 * Get color based on severity level.
 */
@Composable
private fun getSeverityColor(level: Short?): Color {
    return when {
        level == null -> MaterialTheme.colorScheme.outline
        level >= 3 -> Color(0xFFE53935) // Red for critical
        level >= 2 -> Color(0xFFFFA726) // Orange for warning
        else -> Color(0xFF42A5F5) // Blue for info
    }
}

private object DashboardScreenPreviewData {
    val sampleStatCards = listOf(
        StatCard(
            id = "1",
            title = "Total Clients",
            value = "156",
            subtitle = "+12% this month",
            subtitleColor = StatCardSubtitleColor.SUCCESS,
            icon = StatCardIcon.PEOPLE
        ),
        StatCard(
            id = "2",
            title = "Greenhouses",
            value = "423",
            subtitle = "+5% this month",
            subtitleColor = StatCardSubtitleColor.DEFAULT,
            icon = StatCardIcon.GREENHOUSE
        ),
        StatCard(
            id = "3",
            title = "Active Devices",
            value = "1,247",
            subtitle = "98% online",
            subtitleColor = StatCardSubtitleColor.SUCCESS,
            icon = StatCardIcon.DEVICES
        ),
        StatCard(
            id = "4",
            title = "Alerts",
            value = "23",
            subtitle = "5 critical",
            subtitleColor = StatCardSubtitleColor.WARNING,
            icon = StatCardIcon.ALERT
        )
    )
}

@Preview
@Composable
private fun DashboardContentPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            DashboardContent(
                uiState = DashboardUiState(
                    isLoading = false,
                    statCards = DashboardScreenPreviewData.sampleStatCards,
                    alertCount = 23
                ),
                onEvent = {}
            )
        }
    }
}

@Preview
@Composable
private fun DashboardContentLoadingPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            DashboardContent(
                uiState = DashboardUiState(isLoading = true),
                onEvent = {}
            )
        }
    }
}

@Preview
@Composable
private fun DashboardContentErrorPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            DashboardContent(
                uiState = DashboardUiState(
                    isLoading = false,
                    error = "Failed to load dashboard data"
                ),
                onEvent = {}
            )
        }
    }
}
