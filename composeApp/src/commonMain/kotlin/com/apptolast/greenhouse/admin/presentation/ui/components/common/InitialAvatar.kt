package com.apptolast.greenhouse.admin.presentation.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/**
 * Generic avatar component that displays initials with a colored circular background.
 * Replaces UserAvatar, GreenhouseAvatar, SectorAvatar, and SettingAvatar.
 *
 * @param initials The text to display inside the avatar (typically 1-2 characters).
 * @param color The background color. Use [avatarColorForInitial] for automatic color mapping.
 * @param modifier Modifier for sizing and layout.
 */
@Composable
fun InitialAvatar(
    initials: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Returns a consistent color based on the first character of the initial.
 * Provides good visual differentiation across the alphabet.
 */
fun avatarColorForInitial(initial: String): Color {
    return when (initial.firstOrNull()?.uppercaseChar()) {
        'A' -> Color(0xFFE91E63)
        'B' -> Color(0xFF9C27B0)
        'C' -> Color(0xFF795548)
        'D' -> Color(0xFF009688)
        'E' -> Color(0xFF3F51B5)
        'F' -> Color(0xFF4CAF50)
        'G' -> Color(0xFFCDDC39)
        'H' -> Color(0xFF2196F3)
        'I' -> Color(0xFF00BCD4)
        'J' -> Color(0xFFFF5722)
        'K' -> Color(0xFF673AB7)
        'L' -> Color(0xFF9C27B0)
        'M' -> Color(0xFFFF9800)
        'N' -> Color(0xFF8BC34A)
        'O' -> Color(0xFFFFEB3B)
        'P' -> Color(0xFF4CAF50)
        'Q' -> Color(0xFF607D8B)
        'R' -> Color(0xFFF44336)
        'S' -> Color(0xFF03A9F4)
        'T' -> Color(0xFF00BCD4)
        'U' -> Color(0xFF9E9E9E)
        'V' -> Color(0xFFFF5722)
        'W' -> Color(0xFF795548)
        'X' -> Color(0xFF607D8B)
        'Y' -> Color(0xFFFFC107)
        'Z' -> Color(0xFF9E9E9E)
        else -> Color(0xFF607D8B)
    }
}
