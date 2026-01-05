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
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.DeviceFormData
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.DeviceFormMode
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.button_create_device
import greenhouseadmin.composeapp.generated.resources.button_save
import greenhouseadmin.composeapp.generated.resources.device_category_actuator
import greenhouseadmin.composeapp.generated.resources.device_category_sensor
import greenhouseadmin.composeapp.generated.resources.dialog_edit_device_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_edit_device_title
import greenhouseadmin.composeapp.generated.resources.dialog_new_device_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_new_device_title
import greenhouseadmin.composeapp.generated.resources.error_greenhouse_required
import greenhouseadmin.composeapp.generated.resources.error_name_required
import greenhouseadmin.composeapp.generated.resources.label_category
import greenhouseadmin.composeapp.generated.resources.label_device_name
import greenhouseadmin.composeapp.generated.resources.label_greenhouse
import greenhouseadmin.composeapp.generated.resources.label_is_active
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dialog for creating or editing a device.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceFormDialog(
    mode: DeviceFormMode,
    greenhouses: List<Greenhouse> = emptyList(),
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (greenhouseId: String, name: String, categoryId: Short?, typeId: Short?, unitId: Short?, isActive: Boolean) -> Unit = { _, _, _, _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is DeviceFormMode.Create -> DeviceFormData()
            is DeviceFormMode.Edit -> DeviceFormData(
                greenhouseId = mode.device.greenhouseId,
                name = mode.device.categoryName ?: "",
                categoryId = mode.device.categoryId,
                typeId = mode.device.typeId,
                unitId = mode.device.unitId,
                isActive = mode.device.isActive
            )
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(DeviceFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var greenhouseExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val greenhouseErrorMsg = stringResource(Res.string.error_greenhouse_required)
    val nameErrorMsg = stringResource(Res.string.error_name_required)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_greenhouse_required" -> greenhouseErrorMsg
            "error_name_required" -> nameErrorMsg
            else -> null
        }
    }

    val isEditMode = mode is DeviceFormMode.Edit
    val dialogTitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_device_title)
    } else {
        stringResource(Res.string.dialog_new_device_title)
    }
    val dialogSubtitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_device_subtitle)
    } else {
        stringResource(Res.string.dialog_new_device_subtitle)
    }
    val submitButtonText = if (isEditMode) {
        stringResource(Res.string.button_save)
    } else {
        stringResource(Res.string.button_create_device)
    }

    val categorySensorText = stringResource(Res.string.device_category_sensor)
    val categoryActuatorText = stringResource(Res.string.device_category_actuator)

    // Find selected greenhouse name
    val selectedGreenhouse = greenhouses.find { it.id == formData.greenhouseId }

    Dialog(onDismissRequest = { if (!isSubmitting) onDismiss() }) {
        Card(
            modifier = modifier.width(420.dp),
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

                // Greenhouse dropdown (required, not editable in edit mode)
                Column {
                    Text(
                        text = stringResource(Res.string.label_greenhouse),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = greenhouseExpanded,
                        onExpandedChange = { if (!isSubmitting && !isEditMode) greenhouseExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedGreenhouse?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isSubmitting && !isEditMode,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            trailingIcon = {
                                if (!isEditMode) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = greenhouseExpanded)
                                }
                            },
                            isError = hasAttemptedSubmit && validationErrors.greenhouseId != null,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                errorBorderColor = MaterialTheme.colorScheme.error,
                                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                disabledTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        if (!isEditMode) {
                            ExposedDropdownMenu(
                                expanded = greenhouseExpanded,
                                onDismissRequest = { greenhouseExpanded = false }
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
                        }
                    }

                    if (hasAttemptedSubmit && validationErrors.greenhouseId != null) {
                        Text(
                            text = getErrorMessage(validationErrors.greenhouseId) ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Device Name text field
                Column {
                    Text(
                        text = stringResource(Res.string.label_device_name),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = formData.name,
                        onValueChange = {
                            formData = formData.copy(name = it)
                            if (hasAttemptedSubmit) validationErrors = formData.validate()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSubmitting,
                        isError = hasAttemptedSubmit && validationErrors.name != null,
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Temperature Sensor A1...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            errorBorderColor = MaterialTheme.colorScheme.error
                        )
                    )

                    if (hasAttemptedSubmit && validationErrors.name != null) {
                        Text(
                            text = getErrorMessage(validationErrors.name) ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Type dropdown
                Column {
                    Text(
                        text = stringResource(Res.string.label_category),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { if (!isSubmitting) categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = when (formData.categoryId) {
                                Device.CATEGORY_SENSOR -> categorySensorText
                                Device.CATEGORY_ACTUATOR -> categoryActuatorText
                                else -> categorySensorText
                            },
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(categorySensorText) },
                                onClick = {
                                    formData = formData.copy(categoryId = Device.CATEGORY_SENSOR)
                                    categoryExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(categoryActuatorText) },
                                onClick = {
                                    formData = formData.copy(categoryId = Device.CATEGORY_ACTUATOR)
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.label_is_active),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Switch(
                        checked = formData.isActive,
                        onCheckedChange = { formData = formData.copy(isActive = it) },
                        enabled = !isSubmitting,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    )
                }

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
                                    formData.name,
                                    formData.categoryId,
                                    formData.typeId,
                                    formData.unitId,
                                    formData.isActive
                                )
                            }
                        },
                        enabled = !isSubmitting,
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

private object DeviceFormDialogPreviewData {
    val sampleGreenhouses = listOf(
        Greenhouse(
            id = "gh1",
            name = "Main Greenhouse",
            tenantId = "tenant1",
            location = null,
            areaM2 = 1500.0,
            timezone = "Europe/Madrid",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Greenhouse(
            id = "gh2",
            name = "North Greenhouse",
            tenantId = "tenant1",
            location = null,
            areaM2 = 800.0,
            timezone = "Europe/Madrid",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )
    )

    val sampleDevice = Device(
        id = "1",
        tenantId = "tenant1",
        greenhouseId = "gh1",
        categoryId = Device.CATEGORY_SENSOR,
        categoryName = "Temperature Sensor A1",
        typeId = 1,
        typeName = "Temperature",
        unitId = 1,
        unitSymbol = "°C",
        isActive = true
    )
}

@Preview
@Composable
private fun DeviceFormDialogCreatePreview() {
    GreenhouseAdminTheme {
        DeviceFormDialog(
            mode = DeviceFormMode.Create,
            greenhouses = DeviceFormDialogPreviewData.sampleGreenhouses
        )
    }
}

@Preview
@Composable
private fun DeviceFormDialogEditPreview() {
    GreenhouseAdminTheme {
        DeviceFormDialog(
            mode = DeviceFormMode.Edit(DeviceFormDialogPreviewData.sampleDevice),
            greenhouses = DeviceFormDialogPreviewData.sampleGreenhouses
        )
    }
}
