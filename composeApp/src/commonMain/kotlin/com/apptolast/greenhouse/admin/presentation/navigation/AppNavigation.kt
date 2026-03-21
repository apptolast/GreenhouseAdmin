package com.apptolast.greenhouse.admin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.apptolast.greenhouse.admin.data.local.TokenStorage
import com.apptolast.greenhouse.admin.data.model.MenuIcon
import com.apptolast.greenhouse.admin.data.model.MenuItem
import com.apptolast.greenhouse.admin.domain.auth.AuthEvent
import com.apptolast.greenhouse.admin.domain.auth.AuthEventManager
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.AdaptiveScaffold
import com.apptolast.greenhouse.admin.presentation.ui.components.common.search.SearchNavigationTarget
import com.apptolast.greenhouse.admin.presentation.ui.screens.ClientDetailScreen
import com.apptolast.greenhouse.admin.presentation.ui.screens.ClientsScreen
import com.apptolast.greenhouse.admin.presentation.ui.screens.DashboardScreen
import com.apptolast.greenhouse.admin.presentation.ui.screens.LoginScreen
import com.apptolast.greenhouse.admin.presentation.ui.screens.SettingsScreen
import org.koin.compose.koinInject

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
    val authEventManager: AuthEventManager = koinInject()

    ConfigureWebNavigation(navController)

    // Listen for session expiration events and redirect to login
    LaunchedEffect(Unit) {
        authEventManager.authEvents.collect { event ->
            when (event) {
                is AuthEvent.SessionExpired -> {
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    // Get current route for navigation selection
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if we're on the login screen (or null which means we're starting)
    val isLoginScreen = currentRoute == null || currentRoute.contains("LoginRoute")

    // Determine selected menu item based on current route
    val selectedItemId = when {
        currentRoute?.contains("DashboardRoute") == true -> "dashboard"
        currentRoute?.contains("ClientsRoute") == true -> "clients"
        currentRoute?.contains("ClientDetailRoute") == true -> "clients"
        currentRoute?.contains("SettingsRoute") == true -> "settings"
        else -> "dashboard"
    }

    if (isLoginScreen) {
        // Login screen without scaffold
        AppNavHost(navController = navController)
    } else {
        // Main app with scaffold
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
            AppNavHost(navController = navController)
        }
    }
}

@Composable
private fun AppNavHost(navController: NavHostController) {
    val tokenStorage: TokenStorage = koinInject()
    val startDestination = if (tokenStorage.isAuthenticated()) DashboardRoute else LoginRoute

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(DashboardRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

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
                },
                onSearchNavigate = { target ->
                    handleSearchNavigation(navController, target)
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
            SettingsScreen(
                onLogoutSuccess = {
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

private fun handleSearchNavigation(
    navController: NavHostController,
    target: SearchNavigationTarget
) {
    when (target) {
        is SearchNavigationTarget.ToClient ->
            navController.navigate(ClientDetailRoute(target.clientId.toString()))

        is SearchNavigationTarget.ToGreenhouseTab ->
            navController.navigate(ClientDetailRoute(target.clientId.toString()))

        is SearchNavigationTarget.ToSector ->
            navController.navigate(ClientDetailRoute(target.clientId.toString()))

        is SearchNavigationTarget.ToUsersTab ->
            navController.navigate(ClientDetailRoute(target.clientId.toString()))
    }
}
