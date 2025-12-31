package com.apptolast.greenhouse.admin.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.apptolast.greenhouse.admin.presentation.ui.components.DashboardTopBar
import com.apptolast.greenhouse.admin.presentation.ui.components.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.components.LoadingContent
import com.apptolast.greenhouse.admin.presentation.ui.components.SidebarNavigation
import com.apptolast.greenhouse.admin.presentation.ui.components.StatsGrid
import com.apptolast.greenhouse.admin.presentation.viewmodel.DashboardEvent
import com.apptolast.greenhouse.admin.presentation.viewmodel.DashboardUiState
import com.apptolast.greenhouse.admin.presentation.viewmodel.DashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main dashboard screen composable.
 * Follows MVI pattern - receives ViewModel from Koin, no state parameters.
 */
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigate = onNavigate
    )
}

/**
 * Stateless dashboard content composable.
 * Receives state and event handler for testability.
 */
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onEvent: (DashboardEvent) -> Unit,
    onNavigate: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sidebar navigation
        SidebarNavigation(
            menuItems = uiState.menuItems,
            selectedItemId = uiState.selectedMenuId,
            onItemSelected = { itemId ->
                onEvent(DashboardEvent.OnMenuItemSelected(itemId))
                // Navigate to the selected route
                val route = uiState.menuItems.find { it.id == itemId }?.route
                if (route != null && route != "dashboard") {
                    onNavigate(route)
                }
            }
        )

        // Main content area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            // Top bar
            DashboardTopBar(
                title = "Greenhouse Admin",
                subtitle = "Dashboard Overview",
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
                            message = uiState.error ?: "Unknown error occurred",
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
}
