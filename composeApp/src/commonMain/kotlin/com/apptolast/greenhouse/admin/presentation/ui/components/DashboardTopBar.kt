package com.apptolast.greenhouse.admin.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.AdaptiveDimens
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.alerts
import greenhouseadmin.composeapp.generated.resources.search
import greenhouseadmin.composeapp.generated.resources.search_dashboard_placeholder
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Top bar component for the dashboard.
 * Contains title, subtitle, search bar, and alert icon with badge.
 * Adapts layout based on screen size:
 * - Compact: Hides search bar, shows only title, subtitle and alerts
 * - Medium: Shows smaller search field
 * - Expanded: Shows full search field (280dp)
 */
@Composable
fun DashboardTopBar(
    title: String,
    subtitle: String,
    searchQuery: String,
    alertCount: Int,
    onSearchQueryChange: (String) -> Unit = {},
    onAlertClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current
    val horizontalPadding = AdaptiveDimens.horizontalPadding()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title section - takes less space on compact
        Column(
            modifier = if (windowInfo.isCompact) Modifier.weight(1f) else Modifier
        ) {
            Text(
                text = title,
                style = if (windowInfo.isCompact) {
                    MaterialTheme.typography.titleLarge
                } else {
                    MaterialTheme.typography.headlineSmall
                },
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Search and alerts section
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search field - hidden on compact, smaller on medium
            if (!windowInfo.isCompact) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .width(if (windowInfo.isMedium) 200.dp else 280.dp)
                        .height(48.dp),
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.search_dashboard_placeholder),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(Res.string.search),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))
            }

            // Alert icon with badge
            BadgedBox(
                badge = {
                    if (alertCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                text = alertCount.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            ) {
                IconButton(onClick = onAlertClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(Res.string.alerts),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(if (windowInfo.isCompact) 24.dp else 28.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DashboardTopBarPreview() {
    GreenhouseAdminTheme {
        DashboardTopBar(
            title = "Dashboard",
            subtitle = "Welcome back, Admin",
            searchQuery = "",
            alertCount = 5
        )
    }
}

@Preview
@Composable
private fun DashboardTopBarNoAlertsPreview() {
    GreenhouseAdminTheme {
        DashboardTopBar(
            title = "Dashboard",
            subtitle = "Welcome back, Admin",
            searchQuery = "greenhouse",
            alertCount = 0
        )
    }
}
