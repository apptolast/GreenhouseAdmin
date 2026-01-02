package com.apptolast.greenhouse.admin.presentation.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.MenuIcon
import com.apptolast.greenhouse.admin.data.model.MenuItem
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.app_name
import greenhouseadmin.composeapp.generated.resources.menu_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Sidebar navigation component for the dashboard.
 * Displays app branding and menu items with selection state.
 */
@Composable
fun SidebarNavigation(
    menuItems: List<MenuItem>,
    selectedItemId: String,
    onItemSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // App branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu label
        Text(
            text = stringResource(Res.string.menu_label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
        )

        // Menu items
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            menuItems.forEach { item ->
                val isSelected = item.id == selectedItemId
                SidebarMenuItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onItemSelected(item.id) }
                )
            }
        }
    }
}

@Composable
private fun SidebarMenuItem(
    item: MenuItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val icon = getIconForMenuItem(item.icon)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )
    }
}

private fun getIconForMenuItem(icon: MenuIcon): ImageVector {
    return when (icon) {
        MenuIcon.DASHBOARD -> Icons.Default.Home
        MenuIcon.CLIENTS -> Icons.Default.Person
        MenuIcon.SETTINGS -> Icons.Default.Settings
    }
}

private object SidebarPreviewData {
    val menuItems = listOf(
        MenuItem(id = "dashboard", title = "Dashboard", icon = MenuIcon.DASHBOARD, route = "dashboard"),
        MenuItem(id = "clients", title = "Clients", icon = MenuIcon.CLIENTS, route = "clients"),
        MenuItem(id = "settings", title = "Settings", icon = MenuIcon.SETTINGS, route = "settings")
    )
}

@Preview
@Composable
private fun SidebarNavigationPreview() {
    GreenhouseAdminTheme {
        SidebarNavigation(
            menuItems = SidebarPreviewData.menuItems,
            selectedItemId = "dashboard"
        )
    }
}

@Preview
@Composable
private fun SidebarNavigationClientsSelectedPreview() {
    GreenhouseAdminTheme {
        SidebarNavigation(
            menuItems = SidebarPreviewData.menuItems,
            selectedItemId = "clients"
        )
    }
}
