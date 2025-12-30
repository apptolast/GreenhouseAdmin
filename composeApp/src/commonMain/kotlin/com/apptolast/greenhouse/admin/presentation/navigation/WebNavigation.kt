package com.apptolast.greenhouse.admin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

/**
 * Platform-specific web navigation configuration.
 * On web platforms, binds the NavController to browser history.
 * On other platforms, this is a no-op.
 */
@Composable
expect fun ConfigureWebNavigation(navController: NavHostController)
