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
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.GreenhouseStatus
import com.apptolast.greenhouse.admin.data.model.Location
import com.apptolast.greenhouse.admin.data.model.status
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_area
import greenhouseadmin.composeapp.generated.resources.header_location
import greenhouseadmin.composeapp.generated.resources.header_name
import greenhouseadmin.composeapp.generated.resources.header_status
import greenhouseadmin.composeapp.generated.resources.status_active
import greenhouseadmin.composeapp.generated.resources.status_inactive
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Table displaying list of greenhouses with headers and rows.
 */
@Composable
fun GreenhousesTable(
    greenhouses: List<Greenhouse>,
    onEditGreenhouse: (Greenhouse) -> Unit = {},
    onDeleteGreenhouse: (Greenhouse) -> Unit = {},
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
            GreenhousesTableHeader()
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Greenhouse rows
            greenhouses.forEach { greenhouse ->
                GreenhouseTableRow(
                    greenhouse = greenhouse,
                    onEdit = { onEditGreenhouse(greenhouse) },
                    onDelete = { onDeleteGreenhouse(greenhouse) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun GreenhousesTableHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.header_name),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = stringResource(Res.string.header_location),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = stringResource(Res.string.header_area),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
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
private fun GreenhouseTableRow(
    greenhouse: Greenhouse,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // NAME with avatar
        Row(
            modifier = Modifier.weight(1.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GreenhouseAvatar(
                initial = greenhouse.initial,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = greenhouse.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // LOCATION
        Text(
            text = greenhouse.locationDisplay,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // AREA
        Text(
            text = greenhouse.areaDisplay,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // STATUS
        GreenhouseStatusBadge(
            status = greenhouse.status,
            modifier = Modifier.weight(1f)
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
 * Avatar component for greenhouses with colored background.
 */
@Composable
fun GreenhouseAvatar(
    initial: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFF4CAF50)), // Green for greenhouses
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Status badge for greenhouses.
 */
@Composable
fun GreenhouseStatusBadge(
    status: GreenhouseStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, textRes) = when (status) {
        GreenhouseStatus.ACTIVE -> Triple(
            Color(0xFF00E676).copy(alpha = 0.15f),
            Color(0xFF00E676),
            Res.string.status_active
        )

        GreenhouseStatus.INACTIVE -> Triple(
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.onSurfaceVariant,
            Res.string.status_inactive
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

private object GreenhousesTablePreviewData {
    val sampleGreenhouses = listOf(
        Greenhouse(
            id = "1",
            name = "Invernadero Principal",
            tenantId = "client1",
            location = Location(lat = 36.8381, lon = -2.4597),
            areaM2 = 1500.0,
            timezone = "Europe/Madrid",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Greenhouse(
            id = "2",
            name = "Invernadero Norte",
            tenantId = "client1",
            location = Location(lat = 36.8400, lon = -2.4600),
            areaM2 = 800.0,
            timezone = "Europe/Madrid",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Greenhouse(
            id = "3",
            name = "Invernadero Experimental",
            tenantId = "client1",
            location = null,
            areaM2 = null,
            timezone = "Europe/Madrid",
            isActive = false,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )
    )
}

@Preview
@Composable
private fun GreenhousesTablePreview() {
    GreenhouseAdminTheme {
        GreenhousesTable(greenhouses = GreenhousesTablePreviewData.sampleGreenhouses)
    }
}

@Preview
@Composable
private fun GreenhouseAvatarPreview() {
    GreenhouseAdminTheme {
        GreenhouseAvatar(initial = "I")
    }
}
