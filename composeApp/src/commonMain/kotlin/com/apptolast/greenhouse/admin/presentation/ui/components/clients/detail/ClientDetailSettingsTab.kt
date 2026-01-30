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
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.new_setting
import greenhouseadmin.composeapp.generated.resources.settings_empty
import greenhouseadmin.composeapp.generated.resources.settings_subtitle
import greenhouseadmin.composeapp.generated.resources.settings_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Settings tab content for the client detail screen.
 * Displays a table of settings with add/edit/delete functionality.
 * On compact screens, the add button is hidden (FAB is shown by parent).
 */
@Composable
fun ClientDetailSettingsTab(
    settings: List<Setting>,
    sectors: List<Sector> = emptyList(),
    greenhouses: List<Greenhouse> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null,
    onAddSetting: () -> Unit = {},
    onEditSetting: (Setting) -> Unit = {},
    onDeleteSetting: (Setting) -> Unit = {},
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
                    text = stringResource(Res.string.settings_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(Res.string.settings_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Show button only on expanded screens
            if (!windowInfo.isCompact) {
                Button(
                    onClick = onAddSetting,
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
                    Text(stringResource(Res.string.new_setting))
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

            settings.isEmpty() -> {
                EmptySettingsContent()
            }

            else -> {
                SettingsTableOrCards(
                    settings = settings,
                    sectors = sectors,
                    greenhouses = greenhouses,
                    onEditSetting = onEditSetting,
                    onDeleteSetting = onDeleteSetting,
                    onCopyId = onCopyId
                )
            }
        }
    }
}

@Composable
private fun EmptySettingsContent(modifier: Modifier = Modifier) {
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
                text = stringResource(Res.string.settings_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private object ClientDetailSettingsTabPreviewData {
    val sampleSettings = listOf(
        Setting(
            id = 1L,
            code = "SET-00001",
            sectorId = 1L,
            sectorCode = "SEC-00001",
            tenantId = 1L,
            parameterId = 1,
            parameterName = "Temperature",
            actuatorStateId = 1,
            actuatorStateName = "ON",
            value = "25",
            description = null,
            isActive = true,
            createdAt = "2024-01-15T10:30:00Z"
        ),
        Setting(
            id = 2L,
            code = "SET-00002",
            sectorId = 1L,
            sectorCode = "SEC-00001",
            tenantId = 1L,
            parameterId = 2,
            parameterName = "Humidity",
            actuatorStateId = 2,
            actuatorStateName = "OFF",
            value = "80",
            description = null,
            isActive = true,
            createdAt = "2024-01-15T10:30:00Z"
        )
    )
}

@Preview
@Composable
private fun ClientDetailSettingsTabPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailSettingsTab(settings = ClientDetailSettingsTabPreviewData.sampleSettings)
        }
    }
}

@Preview
@Composable
private fun ClientDetailSettingsTabEmptyPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailSettingsTab(settings = emptyList())
        }
    }
}

@Preview
@Composable
private fun ClientDetailSettingsTabLoadingPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailSettingsTab(settings = emptyList(), isLoading = true)
        }
    }
}
