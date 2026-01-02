package com.apptolast.greenhouse.admin.presentation.ui

import androidx.compose.runtime.Composable
import com.apptolast.greenhouse.admin.presentation.navigation.AppNavigation
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme

@Composable
fun App() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            AppNavigation()
        }
    }
}
