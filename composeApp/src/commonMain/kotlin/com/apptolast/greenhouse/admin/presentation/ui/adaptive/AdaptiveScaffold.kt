package com.apptolast.greenhouse.admin.presentation.ui.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.MenuIcon
import com.apptolast.greenhouse.admin.data.model.MenuItem
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.app_name
import greenhouseadmin.composeapp.generated.resources.menu_label
import org.jetbrains.compose.resources.stringResource

/**
 * Adaptive scaffold that automatically switches navigation mode based on window size:
 * - Compact: Bottom Navigation Bar (mobile)
 * - Medium: Navigation Rail (tablet)
 * - Expanded: Full Sidebar (desktop)
 */
@Composable
fun AdaptiveScaffold(
    menuItems: List<MenuItem>,
    selectedItemId: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val windowInfo = LocalAppWindowInfo.current

    when {
        windowInfo.isCompact -> {
            CompactScaffold(
                menuItems = menuItems,
                selectedItemId = selectedItemId,
                onItemSelected = onItemSelected,
                modifier = modifier,
                content = content
            )
        }

        windowInfo.isMedium -> {
            MediumScaffold(
                menuItems = menuItems,
                selectedItemId = selectedItemId,
                onItemSelected = onItemSelected,
                modifier = modifier,
                content = content
            )
        }

        else -> {
            ExpandedScaffold(
                menuItems = menuItems,
                selectedItemId = selectedItemId,
                onItemSelected = onItemSelected,
                modifier = modifier,
                content = content
            )
        }
    }
}

/**
 * Compact layout with bottom navigation bar for mobile devices.
 */
@Composable
private fun CompactScaffold(
    menuItems: List<MenuItem>,
    selectedItemId: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                menuItems = menuItems,
                selectedItemId = selectedItemId,
                onItemSelected = onItemSelected
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
}

/**
 * Medium layout with navigation rail for tablets.
 */
@Composable
private fun MediumScaffold(
    menuItems: List<MenuItem>,
    selectedItemId: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AdaptiveNavigationRail(
            menuItems = menuItems,
            selectedItemId = selectedItemId,
            onItemSelected = onItemSelected
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            content()
        }
    }
}

/**
 * Expanded layout with full sidebar for desktop.
 */
@Composable
private fun ExpandedScaffold(
    menuItems: List<MenuItem>,
    selectedItemId: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ExpandedSidebar(
            menuItems = menuItems,
            selectedItemId = selectedItemId,
            onItemSelected = onItemSelected
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            content()
        }
    }
}

/**
 * Bottom navigation bar for compact (mobile) layout.
 */
@Composable
private fun BottomNavigationBar(
    menuItems: List<MenuItem>,
    selectedItemId: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        menuItems.forEach { item ->
            val isSelected = item.id == selectedItemId
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item.id) },
                icon = {
                    Icon(
                        imageVector = getIconForMenuItem(item.icon),
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/**
 * Navigation rail for medium (tablet) layout.
 */
@Composable
private fun AdaptiveNavigationRail(
    menuItems: List<MenuItem>,
    selectedItemId: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        menuItems.forEach { item ->
            val isSelected = item.id == selectedItemId
            NavigationRailItem(
                selected = isSelected,
                onClick = { onItemSelected(item.id) },
                icon = {
                    Icon(
                        imageVector = getIconForMenuItem(item.icon),
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/**
 * Full sidebar for expanded (desktop) layout.
 * Replicates the original SidebarNavigation design.
 */
@Composable
private fun ExpandedSidebar(
    menuItems: List<MenuItem>,
    selectedItemId: String,
    onItemSelected: (String) -> Unit,
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
                ExpandedSidebarMenuItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onItemSelected(item.id) }
                )
            }
        }
    }
}

@Composable
private fun ExpandedSidebarMenuItem(
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
