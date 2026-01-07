package com.apptolast.greenhouse.admin.presentation.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.SettingsTab
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.settings_tab_account
import greenhouseadmin.composeapp.generated.resources.settings_tab_alert_severities
import greenhouseadmin.composeapp.generated.resources.settings_tab_alert_types
import greenhouseadmin.composeapp.generated.resources.settings_tab_device_categories
import greenhouseadmin.composeapp.generated.resources.settings_tab_device_types
import greenhouseadmin.composeapp.generated.resources.settings_tab_device_units
import greenhouseadmin.composeapp.generated.resources.settings_tab_periods
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Tab bar for settings screen navigation.
 * Adapts based on screen size:
 * - Compact: ScrollableTabRow (horizontally scrollable tabs)
 * - Expanded: Row with spacing (all tabs visible)
 */
@Composable
fun SettingsTabBar(
    selectedTab: SettingsTab,
    onTabSelected: (SettingsTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    if (windowInfo.isCompact) {
        // Scrollable tabs for mobile
        SecondaryScrollableTabRow(
            selectedTabIndex = SettingsTab.entries.indexOf(selectedTab),
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSurface,
            edgePadding = 0.dp,
            divider = {}
        ) {
            SettingsTab.entries.forEach { tab ->
                val tabText = getTabText(tab)
                Tab(
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = tabText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (tab == selectedTab) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                )
            }
        }
    } else {
        // Original Row layout for desktop
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsTab.entries.forEach { tab ->
                TabItem(
                    tab = tab,
                    isSelected = tab == selectedTab,
                    onClick = { onTabSelected(tab) }
                )
                if (tab != SettingsTab.entries.last()) {
                    Spacer(modifier = Modifier.width(24.dp))
                }
            }
        }
    }
}

@Composable
private fun getTabText(tab: SettingsTab): String {
    return when (tab) {
        SettingsTab.ACCOUNT -> stringResource(Res.string.settings_tab_account)
        SettingsTab.DEVICE_CATEGORIES -> stringResource(Res.string.settings_tab_device_categories)
        SettingsTab.DEVICE_TYPES -> stringResource(Res.string.settings_tab_device_types)
        SettingsTab.DEVICE_UNITS -> stringResource(Res.string.settings_tab_device_units)
        SettingsTab.ALERT_TYPES -> stringResource(Res.string.settings_tab_alert_types)
        SettingsTab.ALERT_SEVERITIES -> stringResource(Res.string.settings_tab_alert_severities)
        SettingsTab.PERIODS -> stringResource(Res.string.settings_tab_periods)
    }
}

@Composable
private fun TabItem(
    tab: SettingsTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabText = getTabText(tab)

    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tabText,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Preview
@Composable
private fun SettingsTabBarPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            SettingsTabBar(selectedTab = SettingsTab.ACCOUNT)
        }
    }
}

@Preview
@Composable
private fun SettingsTabBarDeviceCategoriesPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            SettingsTabBar(selectedTab = SettingsTab.DEVICE_CATEGORIES)
        }
    }
}
