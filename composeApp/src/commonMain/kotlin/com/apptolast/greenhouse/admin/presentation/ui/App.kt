package com.apptolast.greenhouse.admin.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.apptolast.greenhouse.admin.data.local.LocaleManager
import com.apptolast.greenhouse.admin.presentation.navigation.AppNavigation
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    val localeManager: LocaleManager = koinInject()
    val currentLanguage by localeManager.currentLanguage.collectAsState()

    key(currentLanguage) {
        GreenhouseAdminTheme {
            ProvideAppWindowInfo {
                AppNavigation()
            }
        }
    }
}
