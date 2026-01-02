package com.apptolast.greenhouse.admin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.apptolast.greenhouse.admin.data.model.MenuIcon
import com.apptolast.greenhouse.admin.data.model.MenuItem
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.AdaptiveScaffold
import com.apptolast.greenhouse.admin.presentation.ui.screens.ClientDetailScreen
import com.apptolast.greenhouse.admin.presentation.ui.screens.ClientsScreen
import com.apptolast.greenhouse.admin.presentation.ui.screens.DashboardScreen
import com.apptolast.greenhouse.admin.presentation.ui.screens.SettingsScreen

/**
 * Static menu items for the application navigation.
 */
private val menuItems = listOf(
    MenuItem(
        id = "dashboard",
        title = "Dashboard",
        icon = MenuIcon.DASHBOARD,
        route = "dashboard"
    ),
    MenuItem(
        id = "clients",
        title = "Clients",
        icon = MenuIcon.CLIENTS,
        route = "clients"
    ),
    MenuItem(
        id = "settings",
        title = "Settings",
        icon = MenuIcon.SETTINGS,
        route = "settings"
    )
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    ConfigureWebNavigation(navController)

    // Get current route for navigation selection
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine selected menu item based on current route
    val selectedItemId = when {
        currentRoute?.contains("DashboardRoute") == true -> "dashboard"
        currentRoute?.contains("ClientsRoute") == true -> "clients"
        currentRoute?.contains("ClientDetailRoute") == true -> "clients"
        currentRoute?.contains("SettingsRoute") == true -> "settings"
        else -> "dashboard"
    }

    AdaptiveScaffold(
        menuItems = menuItems,
        selectedItemId = selectedItemId,
        onItemSelected = { itemId ->
            when (itemId) {
                "dashboard" -> navController.navigate(DashboardRoute) {
                    popUpTo(DashboardRoute) { inclusive = true }
                }

                "clients" -> navController.navigate(ClientsRoute) {
                    popUpTo(DashboardRoute) { inclusive = false }
                }

                "settings" -> navController.navigate(SettingsRoute) {
                    popUpTo(DashboardRoute) { inclusive = false }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = DashboardRoute
        ) {
            composable<DashboardRoute> {
                DashboardScreen()
            }

            composable<ClientsRoute> {
                ClientsScreen(
                    onNavigate = { route ->
                        when {
                            route.startsWith("client_detail/") -> {
                                val clientId = route.removePrefix("client_detail/")
                                navController.navigate(ClientDetailRoute(clientId))
                            }
                        }
                    }
                )
            }

            composable<ClientDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ClientDetailRoute>()
                ClientDetailScreen(
                    clientId = route.clientId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<SettingsRoute> {
                SettingsScreen()
            }
        }
    }
}
