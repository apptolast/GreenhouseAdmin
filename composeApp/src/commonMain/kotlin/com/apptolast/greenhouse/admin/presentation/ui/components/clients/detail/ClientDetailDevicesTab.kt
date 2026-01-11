package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.devices_empty
import greenhouseadmin.composeapp.generated.resources.devices_subtitle
import greenhouseadmin.composeapp.generated.resources.devices_title
import greenhouseadmin.composeapp.generated.resources.new_device
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Devices tab content for the client detail screen.
 * Displays a table of devices with add/edit/delete functionality.
 * On compact screens, the add button is hidden (FAB is shown by parent).
 */
@Composable
fun ClientDetailDevicesTab(
    devices: List<Device>,
    isLoading: Boolean = false,
    error: String? = null,
    onAddDevice: () -> Unit = {},
    onEditDevice: (Device) -> Unit = {},
    onDeleteDevice: (Device) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Header with title and Add button (button hidden on compact - FAB shown by parent)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(Res.string.devices_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(Res.string.devices_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Show button only on expanded screens
            if (!windowInfo.isCompact) {
                Button(
                    onClick = onAddDevice,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.new_device))
                }
            }
        }

        // Content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                ErrorContent(
                    message = error,
                    onRetry = onRetry
                )
            }

            devices.isEmpty() -> {
                EmptyDevicesContent()
            }

            else -> {
                DevicesTableOrCards(
                    devices = devices,
                    onEditDevice = onEditDevice,
                    onDeleteDevice = onDeleteDevice,
                    onCopyId = onCopyId
                )
            }
        }
    }
}

@Composable
private fun EmptyDevicesContent(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.devices_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private object ClientDetailDevicesTabPreviewData {
    val sampleDevices = listOf(
        Device(
            id = 1L,
            code = "DEV-00001",
            tenantId = 1L,
            greenhouseId = 1L,
            categoryId = Device.CATEGORY_SENSOR,
            categoryName = "Sensor",
            typeId = 1,
            typeName = "Temperature",
            unitId = 1,
            unitSymbol = "°C",
            isActive = true
        ),
        Device(
            id = 2L,
            code = "DEV-00002",
            tenantId = 1L,
            greenhouseId = 1L,
            categoryId = Device.CATEGORY_ACTUATOR,
            categoryName = "Actuator",
            typeId = 2,
            typeName = "Valve",
            unitId = null,
            unitSymbol = null,
            isActive = true
        )
    )
}

@Preview
@Composable
private fun ClientDetailDevicesTabPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailDevicesTab(devices = ClientDetailDevicesTabPreviewData.sampleDevices)
        }
    }
}

@Preview
@Composable
private fun ClientDetailDevicesTabEmptyPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailDevicesTab(devices = emptyList())
        }
    }
}

@Preview
@Composable
private fun ClientDetailDevicesTabLoadingPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailDevicesTab(devices = emptyList(), isLoading = true)
        }
    }
}
