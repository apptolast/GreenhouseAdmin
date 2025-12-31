package com.apptolast.greenhouse.admin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.apptolast.greenhouse.admin.presentation.ui.screens.ClientDetailScreen
import com.apptolast.greenhouse.admin.presentation.ui.screens.ClientsScreen
import com.apptolast.greenhouse.admin.presentation.ui.screens.DashboardScreen
import com.apptolast.greenhouse.admin.presentation.ui.screens.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    ConfigureWebNavigation(navController)

    NavHost(
        navController = navController,
        startDestination = DashboardRoute
    ) {
        composable<DashboardRoute> {
            DashboardScreen(
                onNavigate = { route ->
                    when (route) {
                        "clients" -> navController.navigate(ClientsRoute)
                        "settings" -> navController.navigate(SettingsRoute)
                    }
                }
            )
        }

        composable<ClientsRoute> {
            ClientsScreen(
                onNavigate = { route ->
                    when {
                        route == "dashboard" -> navController.navigate(DashboardRoute) {
                            popUpTo(DashboardRoute) { inclusive = true }
                        }

                        route == "settings" -> navController.navigate(SettingsRoute)
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
                onNavigate = { routeName ->
                    when {
                        routeName == "back" -> navController.popBackStack()
                        routeName == "clients" -> navController.navigate(ClientsRoute) {
                            popUpTo(ClientsRoute) { inclusive = true }
                        }

                        routeName == "dashboard" -> navController.navigate(DashboardRoute) {
                            popUpTo(DashboardRoute) { inclusive = true }
                        }

                        routeName == "settings" -> navController.navigate(SettingsRoute)
                    }
                }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen()
        }
    }
}
