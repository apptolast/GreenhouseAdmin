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
import com.apptolast.greenhouse.admin.presentation.ui.components.previewparams.ActiveParamsParams
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.status_active
import greenhouseadmin.composeapp.generated.resources.status_inactive
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter

/**
 * Unified status chip component for displaying active/inactive states.
 * Uses a consistent design across all tables: colored dot + text + semi-transparent background.
 */
@Composable
fun StatusChip(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    activeText: String = stringResource(Res.string.status_active),
    inactiveText: String = stringResource(Res.string.status_inactive)
) {
    val (backgroundColor, dotColor, textColor) = if (isActive) {
        Triple(
            Color(0xFF00E676).copy(alpha = 0.15f),
            Color(0xFF00E676),
            Color(0xFF00E676)
        )
    } else {
        Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isActive) activeText else inactiveText,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
fun StatusChipPreview(
    @PreviewParameter(provider = ActiveParamsParams::class) state: Boolean
) {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            StatusChip(isActive = state)
        }
    }
}