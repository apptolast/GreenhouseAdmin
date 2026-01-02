package com.apptolast.greenhouse.admin.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.apptolast.greenhouse.admin.data.model.StatCard
import com.apptolast.greenhouse.admin.data.model.StatCardIcon
import com.apptolast.greenhouse.admin.data.model.StatCardSubtitleColor
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.DashboardTopBar
import com.apptolast.greenhouse.admin.presentation.ui.components.common.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.components.common.LoadingContent
import com.apptolast.greenhouse.admin.presentation.ui.components.dashboard.StatsGrid
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
                    StatsGrid(
                        statCards = uiState.statCards,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
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
