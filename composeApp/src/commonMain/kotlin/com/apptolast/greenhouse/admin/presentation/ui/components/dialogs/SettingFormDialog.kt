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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogType
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Period
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
import greenhouseadmin.composeapp.generated.resources.error_greenhouse_required
import greenhouseadmin.composeapp.generated.resources.error_min_greater_than_max
import greenhouseadmin.composeapp.generated.resources.error_parameter_required
import greenhouseadmin.composeapp.generated.resources.error_period_required
import greenhouseadmin.composeapp.generated.resources.field_greenhouse
import greenhouseadmin.composeapp.generated.resources.label_loading
import greenhouseadmin.composeapp.generated.resources.label_max_value
import greenhouseadmin.composeapp.generated.resources.label_min_value
import greenhouseadmin.composeapp.generated.resources.label_parameter
import greenhouseadmin.composeapp.generated.resources.label_period
import greenhouseadmin.composeapp.generated.resources.label_select
import greenhouseadmin.composeapp.generated.resources.label_status
import greenhouseadmin.composeapp.generated.resources.status_active
import greenhouseadmin.composeapp.generated.resources.status_inactive
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dialog for creating or editing a setting (parameter threshold configuration).
 * Uses API catalogs for Parameter (DeviceCatalogType) and Period dropdowns.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingFormDialog(
    mode: SettingFormMode,
    greenhouses: List<Greenhouse> = emptyList(),
    parameters: List<DeviceCatalogType> = emptyList(),
    periods: List<Period> = emptyList(),
    isLoadingCatalog: Boolean = false,
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (greenhouseId: Long?, parameterId: Short, periodId: Short, minValue: Double?, maxValue: Double?, isActive: Boolean) -> Unit = { _, _, _, _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is SettingFormMode.Create -> SettingFormData()
            is SettingFormMode.Edit -> SettingFormData(
                greenhouseId = mode.setting.greenhouseId,
                parameterId = mode.setting.parameterId,
                periodId = mode.setting.periodId,
                minValue = mode.setting.minValue?.toString() ?: "",
                maxValue = mode.setting.maxValue?.toString() ?: "",
                isActive = mode.setting.isActive
            )
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(SettingFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var greenhouseExpanded by remember { mutableStateOf(false) }
    var parameterExpanded by remember { mutableStateOf(false) }
    var periodExpanded by remember { mutableStateOf(false) }

    val greenhouseRequiredMsg = stringResource(Res.string.error_greenhouse_required)
    val parameterRequiredMsg = stringResource(Res.string.error_parameter_required)
    val periodRequiredMsg = stringResource(Res.string.error_period_required)
    val minGreaterThanMaxMsg = stringResource(Res.string.error_min_greater_than_max)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_greenhouse_required" -> greenhouseRequiredMsg
            "error_parameter_required" -> parameterRequiredMsg
            "error_period_required" -> periodRequiredMsg
            "error_min_greater_than_max" -> minGreaterThanMaxMsg
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

    // Find selected items for display
    val selectedGreenhouse = greenhouses.find { it.id == formData.greenhouseId }
    val selectedParameter = parameters.find { it.id == formData.parameterId }
    val selectedPeriod = periods.find { it.id == formData.periodId }

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

                // Greenhouse dropdown (required)
                SettingFormDropdown(
                    label = stringResource(Res.string.field_greenhouse),
                    expanded = greenhouseExpanded,
                    onExpandedChange = { if (!isSubmitting && !isLoadingCatalog) greenhouseExpanded = it },
                    selectedText = selectedGreenhouse?.name ?: selectText,
                    isLoading = isLoadingCatalog,
                    loadingText = loadingText,
                    enabled = !isSubmitting && !isEditMode, // Greenhouse not editable in edit mode
                    isError = validationErrors.greenhouseId != null,
                    errorMessage = getErrorMessage(validationErrors.greenhouseId)
                ) {
                    greenhouses.forEach { greenhouse ->
                        DropdownMenuItem(
                            text = { Text(greenhouse.name) },
                            onClick = {
                                formData = formData.copy(greenhouseId = greenhouse.id)
                                greenhouseExpanded = false
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

                // Period dropdown (required)
                SettingFormDropdown(
                    label = stringResource(Res.string.label_period),
                    expanded = periodExpanded,
                    onExpandedChange = { if (!isSubmitting && !isLoadingCatalog) periodExpanded = it },
                    selectedText = selectedPeriod?.displayName ?: selectText,
                    isLoading = isLoadingCatalog,
                    loadingText = loadingText,
                    enabled = !isSubmitting,
                    isError = validationErrors.periodId != null,
                    errorMessage = getErrorMessage(validationErrors.periodId)
                ) {
                    periods.forEach { period ->
                        DropdownMenuItem(
                            text = { Text(period.displayName) },
                            onClick = {
                                formData = formData.copy(periodId = period.id)
                                periodExpanded = false
                                if (hasAttemptedSubmit) validationErrors = formData.validate()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Min/Max values in a row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Min Value field
                    SettingFormNumberField(
                        value = formData.minValue,
                        onValueChange = {
                            formData = formData.copy(minValue = it)
                            if (hasAttemptedSubmit) validationErrors = formData.validate()
                        },
                        label = stringResource(Res.string.label_min_value),
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )

                    // Max Value field
                    SettingFormNumberField(
                        value = formData.maxValue,
                        onValueChange = {
                            formData = formData.copy(maxValue = it)
                            if (hasAttemptedSubmit) validationErrors = formData.validate()
                        },
                        label = stringResource(Res.string.label_max_value),
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Min/Max validation error
                if (validationErrors.minMax != null) {
                    Text(
                        text = getErrorMessage(validationErrors.minMax) ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

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
                                    formData.greenhouseId,
                                    formData.parameterId!!,
                                    formData.periodId!!,
                                    formData.minValueDouble,
                                    formData.maxValueDouble,
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
 * Number field component for min/max values.
 */
@Composable
private fun SettingFormNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
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
            onValueChange = { newValue ->
                // Only allow numeric input with optional decimal point
                if (newValue.isEmpty() || newValue.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
            greenhouses = listOf(
                Greenhouse(id = 1L, tenantId = 1L, name = "Greenhouse A"),
                Greenhouse(id = 2L, tenantId = 1L, name = "Greenhouse B")
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
            periods = listOf(
                Period(id = 1, name = "DAY"),
                Period(id = 2, name = "NIGHT"),
                Period(id = 3, name = "ALL")
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
                    greenhouseId = 1L,
                    greenhouseName = "Greenhouse A",
                    tenantId = 1L,
                    parameterId = 1,
                    parameterName = "Temperature",
                    periodId = 1,
                    periodName = "DAY",
                    minValue = 18.0,
                    maxValue = 25.0,
                    isActive = true,
                    createdAt = "2024-01-15T10:30:00Z"
                )
            ),
            greenhouses = listOf(
                Greenhouse(id = 1L, tenantId = 1L, name = "Greenhouse A"),
                Greenhouse(id = 2L, tenantId = 1L, name = "Greenhouse B")
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
            periods = listOf(
                Period(id = 1, name = "DAY"),
                Period(id = 2, name = "NIGHT"),
                Period(id = 3, name = "ALL")
            )
        )
    }
}
