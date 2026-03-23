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
import com.apptolast.greenhouse.admin.data.model.DataType
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogType
import com.apptolast.greenhouse.admin.data.model.Setting
import com.apptolast.greenhouse.admin.data.model.SettingFormData
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.FormMode
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
import greenhouseadmin.composeapp.generated.resources.label_actuator_state
import greenhouseadmin.composeapp.generated.resources.label_client_name
import greenhouseadmin.composeapp.generated.resources.label_data_type
import greenhouseadmin.composeapp.generated.resources.label_description
import greenhouseadmin.composeapp.generated.resources.label_loading
import greenhouseadmin.composeapp.generated.resources.label_parameter
import greenhouseadmin.composeapp.generated.resources.label_select
import greenhouseadmin.composeapp.generated.resources.label_status
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
    mode: FormMode<Setting>,
    parameters: List<DeviceCatalogType> = emptyList(),
    actuatorStates: List<ActuatorState> = emptyList(),
    dataTypes: List<DataType> = emptyList(),
    isLoadingCatalog: Boolean = false,
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (clientName: String?, parameterId: Short, actuatorStateId: Short, dataTypeId: Short?, description: String?, isActive: Boolean) -> Unit = { _, _, _, _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is FormMode.Create -> SettingFormData()
            is FormMode.Edit -> SettingFormData(
                clientName = mode.entity.clientName ?: "",
                parameterId = mode.entity.parameterId,
                actuatorStateId = mode.entity.actuatorStateId,
                dataTypeId = mode.entity.dataTypeId,
                description = mode.entity.description ?: "",
                isActive = mode.entity.isActive
            )
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(SettingFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var parameterExpanded by remember { mutableStateOf(false) }
    var parameterSearchQuery by remember { mutableStateOf("") }
    var actuatorStateExpanded by remember { mutableStateOf(false) }
    var actuatorStateSearchQuery by remember { mutableStateOf("") }
    var dataTypeExpanded by remember { mutableStateOf(false) }
    var dataTypeSearchQuery by remember { mutableStateOf("") }

    val parameterRequiredMsg = stringResource(Res.string.error_parameter_required)
    val actuatorStateRequiredMsg = stringResource(Res.string.error_actuator_state_required)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_parameter_required" -> parameterRequiredMsg
            "error_actuator_state_required" -> actuatorStateRequiredMsg
            else -> null
        }
    }

    val isEditMode = mode is FormMode.Edit
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
    val selectedParameter = parameters.find { it.id == formData.parameterId }
    val selectedActuatorState = actuatorStates.find { it.id == formData.actuatorStateId }
    val selectedDataType = dataTypes.find { it.id == formData.dataTypeId }

    Dialog(onDismissRequest = {}) {
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

                // Client Name text field (optional - display name for end users)
                SettingFormTextField(
                    value = formData.clientName,
                    onValueChange = { formData = formData.copy(clientName = it) },
                    label = stringResource(Res.string.label_client_name),
                    enabled = !isSubmitting
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Parameter searchable dropdown (required) - uses DeviceCatalogType
                SearchableDropdown(
                    label = stringResource(Res.string.label_parameter),
                    expanded = parameterExpanded,
                    onExpandedChange = { if (!isSubmitting && !isLoadingCatalog) parameterExpanded = it },
                    selectedText = selectedParameter?.name ?: selectText,
                    searchQuery = parameterSearchQuery,
                    onSearchQueryChange = {
                        parameterSearchQuery = it
                        if (!parameterExpanded) parameterExpanded = true
                    },
                    isLoading = isLoadingCatalog,
                    loadingText = loadingText,
                    enabled = !isSubmitting,
                    isError = validationErrors.parameterId != null,
                    errorMessage = getErrorMessage(validationErrors.parameterId)
                ) {
                    val filteredParameters = parameters.filter {
                        it.name.contains(parameterSearchQuery, ignoreCase = true)
                    }
                    filteredParameters.forEach { parameter ->
                        DropdownMenuItem(
                            text = { Text(parameter.name) },
                            onClick = {
                                formData = formData.copy(parameterId = parameter.id)
                                parameterSearchQuery = ""
                                parameterExpanded = false
                                if (hasAttemptedSubmit) validationErrors = formData.validate()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actuator State searchable dropdown (required)
                SearchableDropdown(
                    label = stringResource(Res.string.label_actuator_state),
                    expanded = actuatorStateExpanded,
                    onExpandedChange = { if (!isSubmitting && !isLoadingCatalog) actuatorStateExpanded = it },
                    selectedText = selectedActuatorState?.name ?: selectText,
                    searchQuery = actuatorStateSearchQuery,
                    onSearchQueryChange = {
                        actuatorStateSearchQuery = it
                        if (!actuatorStateExpanded) actuatorStateExpanded = true
                    },
                    isLoading = isLoadingCatalog,
                    loadingText = loadingText,
                    enabled = !isSubmitting,
                    isError = validationErrors.actuatorStateId != null,
                    errorMessage = getErrorMessage(validationErrors.actuatorStateId)
                ) {
                    val filteredActuatorStates = actuatorStates.filter {
                        it.name.contains(actuatorStateSearchQuery, ignoreCase = true)
                    }
                    filteredActuatorStates.forEach { actuatorState ->
                        DropdownMenuItem(
                            text = { Text(actuatorState.name) },
                            onClick = {
                                formData = formData.copy(actuatorStateId = actuatorState.id)
                                actuatorStateSearchQuery = ""
                                actuatorStateExpanded = false
                                if (hasAttemptedSubmit) validationErrors = formData.validate()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Data Type searchable dropdown (optional)
                SearchableDropdown(
                    label = stringResource(Res.string.label_data_type),
                    expanded = dataTypeExpanded,
                    onExpandedChange = { if (!isSubmitting && !isLoadingCatalog) dataTypeExpanded = it },
                    selectedText = selectedDataType?.name ?: selectText,
                    searchQuery = dataTypeSearchQuery,
                    onSearchQueryChange = {
                        dataTypeSearchQuery = it
                        if (!dataTypeExpanded) dataTypeExpanded = true
                    },
                    isLoading = isLoadingCatalog,
                    loadingText = loadingText,
                    enabled = !isSubmitting
                ) {
                    val filteredDataTypes = dataTypes.filter {
                        it.name.contains(dataTypeSearchQuery, ignoreCase = true)
                    }
                    filteredDataTypes.forEach { dataType ->
                        DropdownMenuItem(
                            text = { Text(dataType.name) },
                            onClick = {
                                formData = formData.copy(dataTypeId = dataType.id)
                                dataTypeSearchQuery = ""
                                dataTypeExpanded = false
                            }
                        )
                    }
                }

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
                                    formData.clientName.ifBlank { null },
                                    formData.parameterId!!,
                                    formData.actuatorStateId!!,
                                    formData.dataTypeId,
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
 * Searchable dropdown field component for setting forms.
 * Allows typing to filter items in the dropdown list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchableDropdown(
    label: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedText: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
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
                value = if (isLoading) loadingText else if (expanded) searchQuery else selectedText,
                onValueChange = { onSearchQueryChange(it) },
                readOnly = isLoading,
                enabled = enabled && !isLoading,
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
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
                placeholder = {
                    Text(
                        text = selectedText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
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
                onDismissRequest = {
                    onSearchQueryChange("")
                    onExpandedChange(false)
                }
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
            mode = FormMode.Create,
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
            mode = FormMode.Edit(
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
                    dataTypeId = 1,
                    dataTypeName = "INTEGER",
                    value = "25",
                    description = null,
                    isActive = true,
                    createdAt = "2024-01-15T10:30:00Z"
                )
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
