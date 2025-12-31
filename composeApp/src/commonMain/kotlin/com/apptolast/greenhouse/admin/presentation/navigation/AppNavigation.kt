package com.apptolast.greenhouse.admin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            ClientsScreen()
        }

        composable<SettingsRoute> {
            SettingsScreen()
        }
    }
}
