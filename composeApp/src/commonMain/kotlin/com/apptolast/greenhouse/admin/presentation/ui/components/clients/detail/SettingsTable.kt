package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.presentation.ui.components.common.CopyableIdCell
import com.apptolast.greenhouse.admin.presentation.ui.components.common.StatusChip
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_id
import greenhouseadmin.composeapp.generated.resources.header_parameter
import greenhouseadmin.composeapp.generated.resources.header_period
import greenhouseadmin.composeapp.generated.resources.header_range
import greenhouseadmin.composeapp.generated.resources.header_status
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Table displaying list of settings with headers and rows.
 * Structure: ID | PARAMETER | PERIOD | RANGE | STATUS | ACTIONS
 */
@Composable
fun SettingsTable(
    settings: List<Setting>,
    onEditSetting: (Setting) -> Unit = {},
    onDeleteSetting: (Setting) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Header row
            SettingsTableHeader()
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Setting rows
            settings.forEach { setting ->
                SettingTableRow(
                    setting = setting,
                    onEdit = { onEditSetting(setting) },
                    onDelete = { onDeleteSetting(setting) },
                    onCopyId = onCopyId
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun SettingsTableHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(Res.string.header_id),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(Res.string.header_parameter),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = stringResource(Res.string.header_period),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f)
        )
        Text(
            text = stringResource(Res.string.header_range),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f)
        )
        Text(
            text = stringResource(Res.string.header_status),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(Res.string.header_actions),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SettingTableRow(
    setting: Setting,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // ID - Copyable
        CopyableIdCell(
            id = setting.id,
            onCopyId = onCopyId,
            modifier = Modifier.weight(1f)
        )

        // PARAMETER with avatar
        Row(
            modifier = Modifier.weight(0.8f),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            SettingAvatar(
//                initials = setting.initials,
//                modifier = Modifier.size(36.dp)
//            )
//            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = setting.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // PERIOD badge
        PeriodBadge(
            periodName = setting.periodDisplayName,
            modifier = Modifier.weight(0.6f).wrapContentWidth(align = Alignment.Start),
        )

        // RANGE
        Text(
            text = setting.rangeDisplay,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // STATUS - Using StatusChip
        StatusChip(
            isActive = setting.isActive,
            modifier = Modifier.weight(1f).wrapContentWidth(align = Alignment.Start)
        )

        // ACTIONS
        Row(
            modifier = Modifier.width(80.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.action_edit),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.action_delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Avatar component for settings with colored background.
 */
@Composable
fun SettingAvatar(
    initials: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFF607D8B)), // Blue grey color for settings
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
 * Badge component for displaying period (DAY/NIGHT/ALL).
 */
@Composable
private fun PeriodBadge(
    periodName: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (periodName.uppercase()) {
        "DAY" -> Color(0xFFFFB74D).copy(alpha = 0.2f) // Orange tint
        "NIGHT" -> Color(0xFF5C6BC0).copy(alpha = 0.2f) // Indigo tint
        "ALL", "ALL DAY" -> Color(0xFF4CAF50).copy(alpha = 0.2f) // Green tint
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when (periodName.uppercase()) {
        "DAY" -> Color(0xFFFF8F00) // Dark orange
        "NIGHT" -> Color(0xFF3949AB) // Dark indigo
        "ALL", "ALL DAY" -> Color(0xFF2E7D32) // Dark green
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier.wrapContentWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = periodName,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

private object SettingsTablePreviewData {
    val sampleSettings = listOf(
        Setting(
            id = "1",
            greenhouseId = "gh1",
            greenhouseName = "Greenhouse A",
            tenantId = "t1",
            parameterId = 1,
            parameterName = "Temperature",
            periodId = 1,
            periodName = "DAY",
            minValue = 18.0,
            maxValue = 25.0,
            isActive = true,
            createdAt = "2024-01-15T10:30:00Z"
        ),
        Setting(
            id = "2",
            greenhouseId = "gh1",
            greenhouseName = "Greenhouse A",
            tenantId = "t1",
            parameterId = 2,
            parameterName = "Humidity",
            periodId = 2,
            periodName = "NIGHT",
            minValue = 60.0,
            maxValue = 80.0,
            isActive = true,
            createdAt = "2024-01-15T10:30:00Z"
        ),
        Setting(
            id = "3",
            greenhouseId = "gh1",
            greenhouseName = "Greenhouse A",
            tenantId = "t1",
            parameterId = 1,
            parameterName = "Temperature",
            periodId = 3,
            periodName = "ALL",
            minValue = 15.0,
            maxValue = null,
            isActive = false,
            createdAt = "2024-01-15T10:30:00Z"
        )
    )
}

@Preview
@Composable
private fun SettingsTablePreview() {
    GreenhouseAdminTheme {
        SettingsTable(settings = SettingsTablePreviewData.sampleSettings)
    }
}

@Preview
@Composable
private fun SettingAvatarPreview() {
    GreenhouseAdminTheme {
        Row {
            SettingAvatar(initials = "TE")
            Spacer(modifier = Modifier.width(8.dp))
            SettingAvatar(initials = "HU")
            Spacer(modifier = Modifier.width(8.dp))
            SettingAvatar(initials = "LI")
        }
    }
}

@Preview
@Composable
private fun PeriodBadgePreview() {
    GreenhouseAdminTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PeriodBadge(periodName = "Day")
            PeriodBadge(periodName = "Night")
            PeriodBadge(periodName = "All Day")
        }
    }
}
