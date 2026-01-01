package com.apptolast.greenhouse.admin.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.StatCard
import com.apptolast.greenhouse.admin.data.model.StatCardIcon
import com.apptolast.greenhouse.admin.data.model.StatCardSubtitleColor
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Reusable stats card component for dashboard metrics.
 * Displays a title, value, subtitle, and icon with accent color.
 */
@Composable
fun StatsCard(
    statCard: StatCard,
    modifier: Modifier = Modifier
) {
    val subtitleColor = when (statCard.subtitleColor) {
        StatCardSubtitleColor.DEFAULT -> MaterialTheme.colorScheme.primary
        StatCardSubtitleColor.SUCCESS -> Color(0xFF00E676)
        StatCardSubtitleColor.WARNING -> Color(0xFFFFB74D)
    }

    val iconVector = getIconForStatCard(statCard.icon)
    val iconBackgroundColor = when (statCard.icon) {
        StatCardIcon.PEOPLE -> Color(0xFF1B5E20).copy(alpha = 0.3f)
        StatCardIcon.GREENHOUSE -> Color(0xFF1B5E20).copy(alpha = 0.3f)
        StatCardIcon.DEVICES -> Color(0xFF1B5E20).copy(alpha = 0.3f)
        StatCardIcon.ALERT -> Color(0xFFFF6F00).copy(alpha = 0.3f)
    }

    val iconTint = when (statCard.icon) {
        StatCardIcon.ALERT -> Color(0xFFFFB74D)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = statCard.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = iconBackgroundColor,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = statCard.value,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = statCard.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = subtitleColor
            )
        }
    }
}

private fun getIconForStatCard(icon: StatCardIcon): ImageVector {
    return when (icon) {
        StatCardIcon.PEOPLE -> Icons.Default.Person
        StatCardIcon.GREENHOUSE -> Icons.Outlined.Home
        StatCardIcon.DEVICES -> Icons.Default.Settings
        StatCardIcon.ALERT -> Icons.Default.Notifications
    }
}

private object StatsCardPreviewData {
    val sampleCard = StatCard(
        id = "1",
        title = "Total Clients",
        value = "156",
        subtitle = "+12% from last month",
        icon = StatCardIcon.PEOPLE,
        subtitleColor = StatCardSubtitleColor.SUCCESS
    )
    val alertCard = StatCard(
        id = "2",
        title = "Active Alerts",
        value = "8",
        subtitle = "3 critical",
        icon = StatCardIcon.ALERT,
        subtitleColor = StatCardSubtitleColor.WARNING
    )
}

@Preview
@Composable
private fun StatsCardPreview() {
    GreenhouseAdminTheme {
        StatsCard(
            statCard = StatsCardPreviewData.sampleCard,
            modifier = Modifier.width(280.dp)
        )
    }
}

@Preview
@Composable
private fun StatsCardAlertPreview() {
    GreenhouseAdminTheme {
        StatsCard(
            statCard = StatsCardPreviewData.alertCard,
            modifier = Modifier.width(280.dp)
        )
    }
}
