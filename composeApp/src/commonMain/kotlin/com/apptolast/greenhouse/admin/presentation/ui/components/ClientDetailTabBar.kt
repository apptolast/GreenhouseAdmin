package com.apptolast.greenhouse.admin.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.ClientDetailTab
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.tab_alerts
import greenhouseadmin.composeapp.generated.resources.tab_devices
import greenhouseadmin.composeapp.generated.resources.tab_general
import greenhouseadmin.composeapp.generated.resources.tab_greenhouses
import greenhouseadmin.composeapp.generated.resources.tab_sectors
import greenhouseadmin.composeapp.generated.resources.tab_settings
import greenhouseadmin.composeapp.generated.resources.tab_users
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Tab bar for client detail screen navigation.
 * Adapts based on screen size:
 * - Compact: ScrollableTabRow (horizontally scrollable tabs)
 * - Expanded: Row with spacing (all tabs visible)
 */
@Composable
fun ClientDetailTabBar(
    selectedTab: ClientDetailTab,
    onTabSelected: (ClientDetailTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    if (windowInfo.isCompact) {
        // Scrollable tabs for mobile
        ScrollableTabRow(
            selectedTabIndex = ClientDetailTab.entries.indexOf(selectedTab),
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSurface,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                if (tabPositions.isNotEmpty()) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[ClientDetailTab.entries.indexOf(selectedTab)]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            divider = {}
        ) {
            ClientDetailTab.entries.forEach { tab ->
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
            ClientDetailTab.entries.forEach { tab ->
                TabItem(
                    tab = tab,
                    isSelected = tab == selectedTab,
                    onClick = { onTabSelected(tab) }
                )
                if (tab != ClientDetailTab.entries.last()) {
                    Spacer(modifier = Modifier.width(24.dp))
                }
            }
        }
    }
}

@Composable
private fun getTabText(tab: ClientDetailTab): String {
    return when (tab) {
        ClientDetailTab.GENERAL -> stringResource(Res.string.tab_general)
        ClientDetailTab.USERS -> stringResource(Res.string.tab_users)
        ClientDetailTab.GREENHOUSES -> stringResource(Res.string.tab_greenhouses)
        ClientDetailTab.SECTORS -> stringResource(Res.string.tab_sectors)
        ClientDetailTab.DEVICES -> stringResource(Res.string.tab_devices)
        ClientDetailTab.ALERTS -> stringResource(Res.string.tab_alerts)
        ClientDetailTab.SETTINGS -> stringResource(Res.string.tab_settings)
    }
}

@Composable
private fun TabItem(
    tab: ClientDetailTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabText = getTabText(tab)

    Box(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp)
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
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(top = 4.dp)
                    .width(tabText.length.dp * 8)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Preview
@Composable
private fun ClientDetailTabBarPreview() {
    GreenhouseAdminTheme {
        ClientDetailTabBar(selectedTab = ClientDetailTab.GENERAL)
    }
}

@Preview
@Composable
private fun ClientDetailTabBarUsersSelectedPreview() {
    GreenhouseAdminTheme {
        ClientDetailTabBar(selectedTab = ClientDetailTab.USERS)
    }
}
