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
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.copy_id
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_category
import greenhouseadmin.composeapp.generated.resources.header_id
import greenhouseadmin.composeapp.generated.resources.header_name
import greenhouseadmin.composeapp.generated.resources.header_status
import greenhouseadmin.composeapp.generated.resources.header_type
import greenhouseadmin.composeapp.generated.resources.header_unit
import greenhouseadmin.composeapp.generated.resources.status_active
import greenhouseadmin.composeapp.generated.resources.status_inactive
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Table displaying list of devices with headers and rows.
 * Columns: ID | NAME | CATEGORY | TYPE | UNIT | STATUS | ACTIONS
 */
@Composable
fun DevicesTable(
    devices: List<Device>,
    onEditDevice: (Device) -> Unit = {},
    onDeleteDevice: (Device) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Header row
            DevicesTableHeader()
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Device rows
            devices.forEach { device ->
                DeviceTableRow(
                    device = device,
                    onEdit = { onEditDevice(device) },
                    onDelete = { onDeleteDevice(device) },
                    onCopyId = { onCopyId(device.id) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun DevicesTableHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ID column
        Text(
            text = stringResource(Res.string.header_id),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1.2f)
        )

        // NAME column
        Text(
            text = stringResource(Res.string.header_name),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1.3f)
        )

        // CATEGORY column
        Text(
            text = stringResource(Res.string.header_category),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        // TYPE column
        Text(
            text = stringResource(Res.string.header_type),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        // UNIT column
        Text(
            text = stringResource(Res.string.header_unit),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.6f)
        )

        // STATUS column
        Text(
            text = stringResource(Res.string.header_status),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.8f)
        )

        // ACTIONS column
        Text(
            text = stringResource(Res.string.header_actions),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(88.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DeviceTableRow(
    device: Device,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyId: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ID - Full UUID with monospace font, smaller size to fit, with copy button
        Row(
            modifier = Modifier.weight(1.2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = device.id,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(
                onClick = onCopyId,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = stringResource(Res.string.copy_id),
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }

        // NAME - Device name (or empty if not set)
        Text(
            text = device.name ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = if (device.name != null) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.weight(1.3f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // CATEGORY - Plain text
        Text(
            text = device.categoryName ?: device.categoryDisplayName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // TYPE - Device type name
        Text(
            text = device.typeName ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // UNIT - Unit symbol
        Text(
            text = device.unitSymbol ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // STATUS - Dot indicator with text
        DeviceStatusIndicator(
            isActive = device.isActive,
            modifier = Modifier.weight(0.8f)
        )

        // ACTIONS
        Row(
            modifier = Modifier.width(88.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.action_edit),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.action_delete),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Status indicator with colored dot and text.
 */
@Composable
fun DeviceStatusIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val dotColor = if (isActive) {
        Color(0xFF00E676) // Green
    } else {
        Color(0xFFFF5252) // Red
    }

    val textRes = if (isActive) {
        Res.string.status_active
    } else {
        Res.string.status_inactive
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Dot indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.bodySmall,
            color = if (isActive) {
                Color(0xFF00E676)
            } else {
                MaterialTheme.colorScheme.error
            },
            fontWeight = FontWeight.Medium
        )
    }
}

private object DevicesTablePreviewData {
    val sampleDevices = listOf(
        Device(
            id = "550e8400-e29b-41d4-a716-446655440001",
            tenantId = "tenant1",
            greenhouseId = "gh1",
            name = "Sensor Temperatura Invernadero 1",
            categoryId = Device.CATEGORY_SENSOR,
            categoryName = "SENSOR",
            typeId = 1,
            typeName = "Temperature",
            unitId = 1,
            unitSymbol = "°C",
            isActive = true
        ),
        Device(
            id = "042e3d10-7041-4c32-abd7-063036ce24ba",
            tenantId = "tenant1",
            greenhouseId = "gh1",
            name = null, // Device without name
            categoryId = Device.CATEGORY_ACTUATOR,
            categoryName = "ACTUATOR",
            typeId = 2,
            typeName = "Valve",
            unitId = null,
            unitSymbol = null,
            isActive = true
        ),
        Device(
            id = "4c5a5263-68da-4688-93c7-ddf81b5dafb2",
            tenantId = "tenant1",
            greenhouseId = "gh1",
            name = "Sensor CO2 Norte",
            categoryId = Device.CATEGORY_SENSOR,
            categoryName = "SENSOR",
            typeId = 3,
            typeName = "CO2",
            unitId = 2,
            unitSymbol = "ppm",
            isActive = false
        )
    )
}

@Preview
@Composable
private fun DevicesTablePreview() {
    GreenhouseAdminTheme {
        DevicesTable(devices = DevicesTablePreviewData.sampleDevices)
    }
}

@Preview
@Composable
private fun DeviceStatusIndicatorPreview() {
    GreenhouseAdminTheme {
        Column {
            DeviceStatusIndicator(isActive = true)
            Spacer(modifier = Modifier.size(8.dp))
            DeviceStatusIndicator(isActive = false)
        }
    }
}
