package com.apptolast.greenhouse.admin.presentation.ui.components.dialogs

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.AlertFormData
import com.apptolast.greenhouse.admin.data.model.AlertSeverityCatalog
import com.apptolast.greenhouse.admin.data.model.AlertType
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.AlertFormMode
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.button_create_alert
import greenhouseadmin.composeapp.generated.resources.button_save
import greenhouseadmin.composeapp.generated.resources.dialog_edit_alert_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_edit_alert_title
import greenhouseadmin.composeapp.generated.resources.dialog_new_alert_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_new_alert_title
import greenhouseadmin.composeapp.generated.resources.error_content_required
import greenhouseadmin.composeapp.generated.resources.error_sector_required
import greenhouseadmin.composeapp.generated.resources.field_alert_type
import greenhouseadmin.composeapp.generated.resources.field_message
import greenhouseadmin.composeapp.generated.resources.field_sector
import greenhouseadmin.composeapp.generated.resources.field_severity
import greenhouseadmin.composeapp.generated.resources.label_description
import greenhouseadmin.composeapp.generated.resources.label_loading
import greenhouseadmin.composeapp.generated.resources.label_none
import greenhouseadmin.composeapp.generated.resources.label_select
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dialog for creating or editing an alert.
 * Uses API catalogs for AlertType and Severity dropdowns.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertFormDialog(
    mode: AlertFormMode,
    sectors: List<Sector> = emptyList(),
    alertTypes: List<AlertType> = emptyList(),
    severities: List<AlertSeverityCatalog> = emptyList(),
    isLoadingCatalog: Boolean = false,
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (sectorId: Long?, alertTypeId: Short?, severityId: Short?, message: String?, description: String?) -> Unit = { _, _, _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is AlertFormMode.Create -> AlertFormData()
            is AlertFormMode.Edit -> AlertFormData(
                sectorId = mode.alert.sectorId,
                alertTypeId = mode.alert.alertTypeId,
                severityId = mode.alert.severityId,
                message = mode.alert.message ?: "",
                description = mode.alert.description ?: ""
            )
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(AlertFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var sectorExpanded by remember { mutableStateOf(false) }
    var alertTypeExpanded by remember { mutableStateOf(false) }
    var severityExpanded by remember { mutableStateOf(false) }

    val sectorRequiredMsg = stringResource(Res.string.error_sector_required)
    val contentRequiredMsg = stringResource(Res.string.error_content_required)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_sector_required" -> sectorRequiredMsg
            "error_content_required" -> contentRequiredMsg
            else -> null
        }
    }

    val isEditMode = mode is AlertFormMode.Edit
    val dialogTitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_alert_title)
    } else {
        stringResource(Res.string.dialog_new_alert_title)
    }
    val dialogSubtitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_alert_subtitle)
    } else {
        stringResource(Res.string.dialog_new_alert_subtitle)
    }
    val submitButtonText = if (isEditMode) {
        stringResource(Res.string.button_save)
    } else {
        stringResource(Res.string.button_create_alert)
    }

    val selectText = stringResource(Res.string.label_select)
    val noneText = stringResource(Res.string.label_none)
    val loadingText = stringResource(Res.string.label_loading)
    val descriptionLabel = stringResource(Res.string.label_description)

    // Find selected items for display
    val selectedSector = sectors.find { it.id == formData.sectorId }
    val selectedAlertType = alertTypes.find { it.id == formData.alertTypeId }
    val selectedSeverity = severities.find { it.id == formData.severityId }

    Dialog(onDismissRequest = { if (!isSubmitting) onDismiss() }) {
        Card(
            modifier = modifier.width(480.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Subtitle
                Text(
                    text = dialogSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sector dropdown (required)
                AlertFormDropdown(
                    label = stringResource(Res.string.field_sector),
                    expanded = sectorExpanded,
                    onExpandedChange = { if (!isSubmitting && !isLoadingCatalog) sectorExpanded = it },
                    selectedText = selectedSector?.let { it.name ?: it.code ?: "Sector ${it.id}" } ?: selectText,
                    isLoading = isLoadingCatalog,
                    loadingText = loadingText,
                    enabled = !isSubmitting,
                    isError = validationErrors.sectorId != null,
                    errorMessage = getErrorMessage(validationErrors.sectorId)
                ) {
                    sectors.forEach { sector ->
                        DropdownMenuItem(
                            text = { Text(sector.name ?: sector.code ?: "Sector ${sector.id}") },
                            onClick = {
                                formData = formData.copy(sectorId = sector.id)
                                sectorExpanded = false
                                if (hasAttemptedSubmit) validationErrors = formData.validate()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Alert Type dropdown (optional)
                AlertFormDropdown(
                    label = stringResource(Res.string.field_alert_type),
                    expanded = alertTypeExpanded,
                    onExpandedChange = { if (!isSubmitting && !isLoadingCatalog) alertTypeExpanded = it },
                    selectedText = selectedAlertType?.name ?: noneText,
                    isLoading = isLoadingCatalog,
                    loadingText = loadingText,
                    enabled = !isSubmitting
                ) {
                    // Add "None" option
                    DropdownMenuItem(
                        text = { Text(noneText) },
                        onClick = {
                            formData = formData.copy(alertTypeId = null)
                            alertTypeExpanded = false
                        }
                    )
                    alertTypes.forEach { alertType ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(alertType.name)
                                    alertType.description?.let { desc ->
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            onClick = {
                                formData = formData.copy(alertTypeId = alertType.id)
                                alertTypeExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Severity dropdown (optional, with color indicators)
                AlertFormDropdown(
                    label = stringResource(Res.string.field_severity),
                    expanded = severityExpanded,
                    onExpandedChange = { if (!isSubmitting && !isLoadingCatalog) severityExpanded = it },
                    selectedText = selectedSeverity?.name ?: noneText,
                    isLoading = isLoadingCatalog,
                    loadingText = loadingText,
                    enabled = !isSubmitting,
                    leadingIcon = selectedSeverity?.color?.let { colorHex ->
                        {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(parseColor(colorHex), CircleShape)
                            )
                        }
                    }
                ) {
                    // Add "None" option
                    DropdownMenuItem(
                        text = { Text(noneText) },
                        onClick = {
                            formData = formData.copy(severityId = null)
                            severityExpanded = false
                        }
                    )
                    severities.forEach { severity ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    severity.color?.let { colorHex ->
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(parseColor(colorHex), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text(severity.name)
                                }
                            },
                            onClick = {
                                formData = formData.copy(severityId = severity.id)
                                severityExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Message field (optional - but message OR description required)
                AlertFormTextField(
                    value = formData.message,
                    onValueChange = {
                        formData = formData.copy(message = it)
                        if (hasAttemptedSubmit) validationErrors = formData.validate()
                    },
                    label = stringResource(Res.string.field_message),
                    error = null, // Error shown on description field
                    enabled = !isSubmitting,
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description field (optional - but message OR description required)
                AlertFormTextField(
                    value = formData.description,
                    onValueChange = {
                        formData = formData.copy(description = it)
                        if (hasAttemptedSubmit) validationErrors = formData.validate()
                    },
                    label = descriptionLabel,
                    error = getErrorMessage(validationErrors.content),
                    enabled = !isSubmitting,
                    minLines = 3,
                    maxLines = 5
                )

                // Error message
                if (error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(Res.string.button_cancel))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            hasAttemptedSubmit = true
                            validationErrors = formData.validate()
                            if (!validationErrors.hasErrors) {
                                onSubmit(
                                    formData.sectorId,
                                    formData.alertTypeId,
                                    formData.severityId,
                                    formData.message.ifBlank { null },
                                    formData.description.ifBlank { null }
                                )
                            }
                        },
                        enabled = !isSubmitting && !isLoadingCatalog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(submitButtonText)
                    }
                }
            }
        }
    }
}

/**
 * Dropdown field component for alert forms.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlertFormDropdown(
    label: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedText: String,
    isLoading: Boolean,
    loadingText: String,
    enabled: Boolean,
    isError: Boolean = false,
    errorMessage: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    menuContent: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = if (isLoading) loadingText else selectedText,
                onValueChange = {},
                readOnly = true,
                enabled = enabled && !isLoading,
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                leadingIcon = leadingIcon,
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    errorBorderColor = MaterialTheme.colorScheme.error
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                menuContent()
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Text field component for alert forms.
 */
@Composable
private fun AlertFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    enabled: Boolean,
    minLines: Int = 1,
    maxLines: Int = 1,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = error != null,
            minLines = minLines,
            maxLines = maxLines,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                errorBorderColor = MaterialTheme.colorScheme.error
            )
        )

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Parse a hex color string to a Color.
 */
private fun parseColor(hexColor: String): Color {
    return try {
        val hex = hexColor.removePrefix("#")
        val colorLong = hex.toLong(16)
        when (hex.length) {
            6 -> Color(0xFF000000 or colorLong)
            8 -> Color(colorLong)
            else -> Color.Gray
        }
    } catch (e: Exception) {
        Color.Gray
    }
}

@Preview
@Composable
private fun AlertFormDialogCreatePreview() {
    GreenhouseAdminTheme {
        AlertFormDialog(
            mode = AlertFormMode.Create,
            sectors = listOf(
                Sector(id = 1L, code = "SEC-00001", tenantId = 1L, greenhouseId = 1L, name = "Tomato"),
                Sector(id = 2L, code = "SEC-00002", tenantId = 1L, greenhouseId = 1L, name = "Pepper")
            ),
            alertTypes = listOf(
                AlertType(id = 1, name = "Temperature", description = "Temperature alerts"),
                AlertType(id = 2, name = "Humidity", description = "Humidity alerts")
            ),
            severities = listOf(
                AlertSeverityCatalog(
                    id = 1,
                    name = "Low",
                    level = 1,
                    description = null,
                    color = "#4CAF50",
                    requiresAction = false
                ),
                AlertSeverityCatalog(
                    id = 2,
                    name = "Medium",
                    level = 2,
                    description = null,
                    color = "#FF9800",
                    requiresAction = false
                ),
                AlertSeverityCatalog(
                    id = 3,
                    name = "High",
                    level = 3,
                    description = null,
                    color = "#F44336",
                    requiresAction = true
                )
            )
        )
    }
}

@Preview
@Composable
private fun AlertFormDialogEditPreview() {
    GreenhouseAdminTheme {
        AlertFormDialog(
            mode = AlertFormMode.Edit(
                Alert(
                    id = 1L,
                    code = "ALT-00001",
                    tenantId = 1L,
                    sectorId = 1L,
                    sectorCode = "SEC-00001",
                    alertTypeId = 1,
                    alertTypeName = "Temperature",
                    severityId = 2,
                    severityName = "Medium",
                    severityLevel = 2,
                    message = "Temperature exceeds threshold",
                    description = null,
                    isResolved = false,
                    resolvedAt = null,
                    resolvedByUserName = null,
                    createdAt = "2024-01-15T10:30:00Z"
                )
            ),
            sectors = listOf(
                Sector(id = 1L, code = "SEC-00001", tenantId = 1L, greenhouseId = 1L, name = "Tomato"),
                Sector(id = 2L, code = "SEC-00002", tenantId = 1L, greenhouseId = 1L, name = "Pepper")
            ),
            alertTypes = listOf(
                AlertType(id = 1, name = "Temperature", description = "Temperature alerts"),
                AlertType(id = 2, name = "Humidity", description = "Humidity alerts")
            ),
            severities = listOf(
                AlertSeverityCatalog(
                    id = 1,
                    name = "Low",
                    level = 1,
                    description = null,
                    color = "#4CAF50",
                    requiresAction = false
                ),
                AlertSeverityCatalog(
                    id = 2,
                    name = "Medium",
                    level = 2,
                    description = null,
                    color = "#FF9800",
                    requiresAction = false
                ),
                AlertSeverityCatalog(
                    id = 3,
                    name = "High",
                    level = 3,
                    description = null,
                    color = "#F44336",
                    requiresAction = true
                )
            )
        )
    }
}
