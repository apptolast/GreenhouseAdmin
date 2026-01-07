package com.apptolast.greenhouse.admin.presentation.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.previewparams.SeverityChipProvider
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.label_not_assigned
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter

/**
 * Severity chip component for displaying alert severity levels.
 * Uses a consistent design: colored dot + text + semi-transparent background.
 *
 * Severity levels:
 * - 1: INFO (Blue)
 * - 2: WARNING (Orange)
 * - 3: ERROR (Red)
 * - 4: CRITICAL (Purple)
 * - null/other: Unknown (Gray)
 */
@Composable
fun SeverityChip(
    name: String?,
    level: Short?,
    modifier: Modifier = Modifier
) {
    val notAssignedText = stringResource(Res.string.label_not_assigned)
    val severityColor = getSeverityColor(level)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(severityColor.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(severityColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = name ?: notAssignedText,
            style = MaterialTheme.typography.labelSmall,
            color = severityColor,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Returns the color associated with a severity level.
 */
private fun getSeverityColor(level: Short?): Color {
    return when (level?.toInt()) {
        1 -> Color(0xFF2196F3) // Blue - Info
        2 -> Color(0xFFFF9800) // Orange - Warning
        3 -> Color(0xFFF44336) // Red - Error
        4 -> Color(0xFF9C27B0) // Purple - Critical
        else -> Color(0xFF757575) // Gray - Unknown/Not assigned
    }
}

@Preview
@Composable
fun SeverityChipPreview(
    @PreviewParameter(SeverityChipProvider::class) state: Short
) {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                SeverityChip(
                    name = "Active",
                    level = state
                )
            }
        }
    }
}
