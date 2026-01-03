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
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.DeviceStatus
import com.apptolast.greenhouse.admin.data.model.DeviceType
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.device_status_offline
import greenhouseadmin.composeapp.generated.resources.device_status_online
import greenhouseadmin.composeapp.generated.resources.device_type_actuator
import greenhouseadmin.composeapp.generated.resources.device_type_sensor
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_name
import greenhouseadmin.composeapp.generated.resources.header_status
import greenhouseadmin.composeapp.generated.resources.header_type
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Table displaying list of devices with headers and rows.
 */
@Composable
fun DevicesTable(
    devices: List<Device>,
    onEditDevice: (Device) -> Unit = {},
    onDeleteDevice: (Device) -> Unit = {},
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
            DevicesTableHeader()
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Device rows
            devices.forEach { device ->
                DeviceTableRow(
                    device = device,
                    onEdit = { onEditDevice(device) },
                    onDelete = { onDeleteDevice(device) }
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
            text = stringResource(Res.string.header_type),
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
private fun DeviceTableRow(
    device: Device,
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
            DeviceAvatar(
                initials = device.initials,
                deviceType = device.type,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = device.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // TYPE
        DeviceTypeBadge(
            type = device.type,
            modifier = Modifier.weight(1f)
        )

        // STATUS
        DeviceStatusBadge(
            status = device.status,
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
 * Avatar component for devices with colored background based on type.
 */
@Composable
fun DeviceAvatar(
    initials: String,
    deviceType: DeviceType,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (deviceType) {
        DeviceType.SENSOR -> Color(0xFF9C27B0) // Purple for sensors
        DeviceType.ACTUATOR -> Color(0xFFFF9800) // Orange for actuators
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
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
 * Type badge for devices.
 */
@Composable
fun DeviceTypeBadge(
    type: DeviceType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, textRes) = when (type) {
        DeviceType.SENSOR -> Triple(
            Color(0xFF9C27B0).copy(alpha = 0.15f),
            Color(0xFF9C27B0),
            Res.string.device_type_sensor
        )

        DeviceType.ACTUATOR -> Triple(
            Color(0xFFFF9800).copy(alpha = 0.15f),
            Color(0xFFFF9800),
            Res.string.device_type_actuator
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

/**
 * Status badge for devices.
 */
@Composable
fun DeviceStatusBadge(
    status: DeviceStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, textRes) = when (status) {
        DeviceStatus.ONLINE -> Triple(
            Color(0xFF00E676).copy(alpha = 0.15f),
            Color(0xFF00E676),
            Res.string.device_status_online
        )

        DeviceStatus.OFFLINE -> Triple(
            MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.error,
            Res.string.device_status_offline
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

private object DevicesTablePreviewData {
    val sampleDevices = listOf(
        Device(
            id = "1",
            name = "Sensor Temperatura A1",
            type = DeviceType.SENSOR,
            status = DeviceStatus.ONLINE,
            clientId = "client1"
        ),
        Device(
            id = "2",
            name = "Valvula Riego Norte",
            type = DeviceType.ACTUATOR,
            status = DeviceStatus.ONLINE,
            clientId = "client1"
        ),
        Device(
            id = "3",
            name = "Sensor CO2 B2",
            type = DeviceType.SENSOR,
            status = DeviceStatus.OFFLINE,
            clientId = "client1"
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
private fun DeviceAvatarPreview() {
    GreenhouseAdminTheme {
        Row {
            DeviceAvatar(initials = "ST", deviceType = DeviceType.SENSOR)
            Spacer(modifier = Modifier.width(8.dp))
            DeviceAvatar(initials = "VR", deviceType = DeviceType.ACTUATOR)
        }
    }
}
