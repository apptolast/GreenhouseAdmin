package com.apptolast.greenhouse.admin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.NavHostController
import androidx.navigation.bindToBrowserNavigation

@OptIn(ExperimentalBrowserHistoryApi::class)
@Composable
actual fun ConfigureWebNavigation(navController: NavHostController) {
    LaunchedEffect(navController) {
        navController.bindToBrowserNavigation()
    }
}
