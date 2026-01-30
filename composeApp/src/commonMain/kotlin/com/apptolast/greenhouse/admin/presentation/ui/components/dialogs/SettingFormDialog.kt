package com.apptolast.greenhouse.admin.presentation.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apptolast.greenhouse.admin.data.model.ActuatorState
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogType
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.data.model.SettingFormData
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.SettingFormMode
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.button_create_setting
import greenhouseadmin.composeapp.generated.resources.button_save
import greenhouseadmin.composeapp.generated.resources.dialog_edit_setting_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_edit_setting_title
import greenhouseadmin.composeapp.generated.resources.dialog_new_setting_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_new_setting_title
import greenhouseadmin.composeapp.generated.resources.error_actuator_state_required
import greenhouseadmin.composeapp.generated.resources.error_parameter_required
import greenhouseadmin.composeapp.generated.resources.error_sector_required
import greenhouseadmin.composeapp.generated.resources.field_sector
import greenhouseadmin.composeapp.generated.resources.label_actuator_state
import greenhouseadmin.composeapp.generated.resources.label_description
import greenhouseadmin.composeapp.generated.resources.label_loading
import greenhouseadmin.composeapp.generated.resources.label_parameter
import greenhouseadmin.composeapp.generated.resources.label_select
import greenhouseadmin.composeapp.generated.resources.label_status
import greenhouseadmin.composeapp.generated.resources.label_value
import greenhouseadmin.composeapp.generated.resources.status_active
import greenhouseadmin.composeapp.generated.resources.status_inactive
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dialog for creating or editing a setting (parameter threshold configuration).
 * Uses API catalogs for Parameter (DeviceCatalogType) and ActuatorState dropdowns.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingFormDialog(
    mode: SettingFormMode,
    sectors: List<Sector> = emptyList(),
    greenhouses: List<Greenhouse> = emptyList(),
    parameters: List<DeviceCatalogType> = emptyList(),
    actuatorStates: List<ActuatorState> = emptyList(),
    isLoadingCatalog: Boolean = false,
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (sectorId: Long?, parameterId: Short, actuatorStateId: Short, value: String, description: String?, isActive: Boolean) -> Unit = { _, _, _, _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is SettingFormMode.Create -> SettingFormData()
            is SettingFormMode.Edit -> SettingFormData(
                sectorId = mode.setting.sectorId,
                parameterId = mode.setting.parameterId,
                actuatorStateId = mode.setting.actuatorStateId,
                value = mode.setting.value ?: "",
                description = mode.setting.description ?: "",
                isActive = mode.setting.isActive
            )
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(SettingFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var sectorExpanded by remember { mutableStateOf(false) }
    var parameterExpanded by remember { mutableStateOf(false) }
    var actuatorStateExpanded by remember { mutableStateOf(false) }

    val sectorRequiredMsg = stringResource(Res.string.error_sector_required)
    val parameterRequiredMsg = stringResource(Res.string.error_parameter_required)
    val actuatorStateRequiredMsg = stringResource(Res.string.error_actuator_state_required)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_sector_required" -> sectorRequiredMsg
            "error_parameter_required" -> parameterRequiredMsg
            "error_actuator_state_required" -> actuatorStateRequiredMsg
            else -> null
        }
    }

    val isEditMode = mode is SettingFormMode.Edit
    val dialogTitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_setting_title)
    } else {
        stringResource(Res.string.dialog_new_setting_title)
    }
    val dialogSubtitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_setting_subtitle)
    } else {
        stringResource(Res.string.dialog_new_setting_subtitle)
    }
    val submitButtonText = if (isEditMode) {
        stringResource(Res.string.button_save)
    } else {
        stringResource(Res.string.button_create_setting)
    }

    val selectText = stringResource(Res.string.label_select)
    val loadingText = stringResource(Res.string.label_loading)
    val descriptionLabel = stringResource(Res.string.label_description)

    // Find selected items for display
    val selectedSector = sectors.find { it.id == formData.sectorId }
    val selectedParameter = parameters.find { it.id == formData.parameterId }
    val selectedActuatorState = actuatorStates.find { it.id == formData.actuatorStateId }

    // Helper function to get sector display text with greenhouse name
    fun getSectorDisplayText(sector: Sector): String {
        val greenhouse = greenhouses.find { it.id == sector.greenhouseId }
        return "${sector.displayName} (${greenhouse?.name ?: "Unknown"})"
    }

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
                SettingFormDropdown(
                    label = stringResource(Res.string.field_sector),
                    expanded = sectorExpanded,
                    onExpandedChange = { if (!isSubmitting && !isLoadingCatalog) sectorExpanded = it },
                    selectedText = selectedSector?.let { getSectorDisplayText(it) } ?: selectText,
                    isLoading = isLoadingCatalog,
                    loadingText = loadingText,
                    enabled = !isSubmitting && !isEditMode, // Sector not editable in edit mode
                    isError = validationErrors.sectorId != null,
                    errorMessage = getErrorMessage(validationErrors.sectorId)
                ) {
                    sectors.forEach { sector ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(sector.displayName)
                                    val greenhouse = greenhouses.find { it.id == sector.greenhouseId }
                                    greenhouse?.let {
                                        Text(
                                            text = it.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            onClick = {
                                formData = formData.copy(sectorId = sector.id)
                                sectorExpanded = false
                                if (hasAttemptedSubmit) validationErrors = formData.validate()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Parameter dropdown (required) - uses DeviceCatalogType
                SettingFormDropdown(
                    label = stringResource(Res.string.label_parameter),
                    expanded = parameterExpanded,
                    onExpandedChange = { if (!isSubmitting && !isLoadingCatalog) parameterExpanded = it },
                    selectedText = selectedParameter?.name ?: selectText,
                    isLoading = isLoadingCatalog,
                    loadingText = loadingText,
                    enabled = !isSubmitting,
                    isError = validationErrors.parameterId != null,
                    errorMessage = getErrorMessage(validationErrors.parameterId)
                ) {
                    parameters.forEach { parameter ->
                        DropdownMenuItem(
                            text = { Text(parameter.name) },
                            onClick = {
                                formData = formData.copy(parameterId = parameter.id)
                                parameterExpanded = false
                                if (hasAttemptedSubmit) validationErrors = formData.validate()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actuator State dropdown (required)
                SettingFormDropdown(
                    label = stringResource(Res.string.label_actuator_state),
                    expanded = actuatorStateExpanded,
                    onExpandedChange = { if (!isSubmitting && !isLoadingCatalog) actuatorStateExpanded = it },
                    selectedText = selectedActuatorState?.name ?: selectText,
                    isLoading = isLoadingCatalog,
                    loadingText = loadingText,
                    enabled = !isSubmitting,
                    isError = validationErrors.actuatorStateId != null,
                    errorMessage = getErrorMessage(validationErrors.actuatorStateId)
                ) {
                    actuatorStates.forEach { actuatorState ->
                        DropdownMenuItem(
                            text = { Text(actuatorState.name) },
                            onClick = {
                                formData = formData.copy(actuatorStateId = actuatorState.id)
                                actuatorStateExpanded = false
                                if (hasAttemptedSubmit) validationErrors = formData.validate()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Value field
                SettingFormTextField(
                    value = formData.value,
                    onValueChange = { formData = formData.copy(value = it) },
                    label = stringResource(Res.string.label_value),
                    enabled = !isSubmitting
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description field (optional)
                SettingFormTextField(
                    value = formData.description,
                    onValueChange = { formData = formData.copy(description = it) },
                    label = descriptionLabel,
                    enabled = !isSubmitting,
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status switch
                SettingStatusSwitch(
                    isActive = formData.isActive,
                    onActiveChanged = { formData = formData.copy(isActive = it) },
                    enabled = !isSubmitting,
                    label = stringResource(Res.string.label_status)
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
                                    formData.parameterId!!,
                                    formData.actuatorStateId!!,
                                    formData.value,
                                    formData.description.ifBlank { null },
                                    formData.isActive
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
 * Dropdown field component for setting forms.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingFormDropdown(
    label: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedText: String,
    isLoading: Boolean,
    loadingText: String,
    enabled: Boolean,
    isError: Boolean = false,
    errorMessage: String? = null,
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
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
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
 * Text field component for value input.
 */
@Composable
private fun SettingFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
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
            singleLine = minLines == 1 && maxLines == 1,
            minLines = minLines,
            maxLines = maxLines,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )
    }
}

/**
 * Status switch component.
 */
@Composable
private fun SettingStatusSwitch(
    isActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    enabled: Boolean,
    label: String,
    modifier: Modifier = Modifier
) {
    val activeText = stringResource(Res.string.status_active)
    val inactiveText = stringResource(Res.string.status_inactive)

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isActive) activeText else inactiveText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Switch(
                checked = isActive,
                onCheckedChange = onActiveChanged,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Preview
@Composable
private fun SettingFormDialogCreatePreview() {
    GreenhouseAdminTheme {
        SettingFormDialog(
            mode = SettingFormMode.Create,
            sectors = listOf(
                Sector(id = 1L, code = "SEC-00001", tenantId = 1L, greenhouseId = 1L, name = "Tomato"),
                Sector(id = 2L, code = "SEC-00002", tenantId = 1L, greenhouseId = 1L, name = "Pepper")
            ),
            parameters = listOf(
                DeviceCatalogType(
                    id = 1,
                    name = "Temperature",
                    description = "Temperature sensor",
                    categoryId = 1,
                    defaultUnitId = 1,
                    defaultUnitSymbol = "°C",
                    controlType = null
                ),
                DeviceCatalogType(
                    id = 2,
                    name = "Humidity",
                    description = "Humidity sensor",
                    categoryId = 1,
                    defaultUnitId = 2,
                    defaultUnitSymbol = "%",
                    controlType = null
                )
            ),
            actuatorStates = listOf(
                ActuatorState(
                    id = 1,
                    name = "OFF",
                    description = "Device off",
                    isOperational = false,
                    displayOrder = 1,
                    color = null
                ),
                ActuatorState(
                    id = 2,
                    name = "ON",
                    description = "Device on",
                    isOperational = true,
                    displayOrder = 2,
                    color = null
                ),
                ActuatorState(
                    id = 3,
                    name = "AUTO",
                    description = "Automatic mode",
                    isOperational = true,
                    displayOrder = 3,
                    color = null
                )
            )
        )
    }
}

@Preview
@Composable
private fun SettingFormDialogEditPreview() {
    GreenhouseAdminTheme {
        SettingFormDialog(
            mode = SettingFormMode.Edit(
                Setting(
                    id = 1L,
                    code = "SET-00001",
                    sectorId = 1L,
                    sectorCode = "SEC-00001",
                    tenantId = 1L,
                    parameterId = 1,
                    parameterName = "Temperature",
                    actuatorStateId = 2,
                    actuatorStateName = "ON",
                    value = "25",
                    description = null,
                    isActive = true,
                    createdAt = "2024-01-15T10:30:00Z"
                )
            ),
            sectors = listOf(
                Sector(id = 1L, code = "SEC-00001", tenantId = 1L, greenhouseId = 1L, name = "Tomato"),
                Sector(id = 2L, code = "SEC-00002", tenantId = 1L, greenhouseId = 1L, name = "Pepper")
            ),
            parameters = listOf(
                DeviceCatalogType(
                    id = 1,
                    name = "Temperature",
                    description = "Temperature sensor",
                    categoryId = 1,
                    defaultUnitId = 1,
                    defaultUnitSymbol = "°C",
                    controlType = null
                ),
                DeviceCatalogType(
                    id = 2,
                    name = "Humidity",
                    description = "Humidity sensor",
                    categoryId = 1,
                    defaultUnitId = 2,
                    defaultUnitSymbol = "%",
                    controlType = null
                )
            ),
            actuatorStates = listOf(
                ActuatorState(
                    id = 1,
                    name = "OFF",
                    description = "Device off",
                    isOperational = false,
                    displayOrder = 1,
                    color = null
                ),
                ActuatorState(
                    id = 2,
                    name = "ON",
                    description = "Device on",
                    isOperational = true,
                    displayOrder = 2,
                    color = null
                ),
                ActuatorState(
                    id = 3,
                    name = "AUTO",
                    description = "Automatic mode",
                    isOperational = true,
                    displayOrder = 3,
                    color = null
                )
            )
        )
    }
}
