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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.DeviceFormData
import com.apptolast.greenhouse.admin.data.model.DeviceStatus
import com.apptolast.greenhouse.admin.data.model.DeviceType
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.DeviceFormMode
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.button_create_device
import greenhouseadmin.composeapp.generated.resources.button_save
import greenhouseadmin.composeapp.generated.resources.device_status_offline
import greenhouseadmin.composeapp.generated.resources.device_status_online
import greenhouseadmin.composeapp.generated.resources.device_type_actuator
import greenhouseadmin.composeapp.generated.resources.device_type_sensor
import greenhouseadmin.composeapp.generated.resources.dialog_edit_device_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_edit_device_title
import greenhouseadmin.composeapp.generated.resources.dialog_new_device_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_new_device_title
import greenhouseadmin.composeapp.generated.resources.error_name_min_length
import greenhouseadmin.composeapp.generated.resources.field_status
import greenhouseadmin.composeapp.generated.resources.field_type
import greenhouseadmin.composeapp.generated.resources.label_name
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dialog for creating or editing a device.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceFormDialog(
    mode: DeviceFormMode,
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (name: String, type: DeviceType, status: DeviceStatus) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is DeviceFormMode.Create -> DeviceFormData()
            is DeviceFormMode.Edit -> DeviceFormData(
                name = mode.device.name,
                type = mode.device.type,
                status = mode.device.status
            )
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(DeviceFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    val nameErrorMsg = stringResource(Res.string.error_name_min_length)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_name_min_length" -> nameErrorMsg
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

    val typeSensorText = stringResource(Res.string.device_type_sensor)
    val typeActuatorText = stringResource(Res.string.device_type_actuator)
    val statusOnlineText = stringResource(Res.string.device_status_online)
    val statusOfflineText = stringResource(Res.string.device_status_offline)

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

                // Name field
                DeviceFormTextField(
                    value = formData.name,
                    onValueChange = {
                        formData = formData.copy(name = it)
                        if (hasAttemptedSubmit) validationErrors = formData.validate()
                    },
                    label = stringResource(Res.string.label_name),
                    error = getErrorMessage(validationErrors.name),
                    enabled = !isSubmitting
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Type dropdown
                Column {
                    Text(
                        text = stringResource(Res.string.field_type),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { if (!isSubmitting) typeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = when (formData.type) {
                                DeviceType.SENSOR -> typeSensorText
                                DeviceType.ACTUATOR -> typeActuatorText
                            },
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(typeSensorText) },
                                onClick = {
                                    formData = formData.copy(type = DeviceType.SENSOR)
                                    typeExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(typeActuatorText) },
                                onClick = {
                                    formData = formData.copy(type = DeviceType.ACTUATOR)
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status dropdown
                Column {
                    Text(
                        text = stringResource(Res.string.field_status),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { if (!isSubmitting) statusExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = when (formData.status) {
                                DeviceStatus.ONLINE -> statusOnlineText
                                DeviceStatus.OFFLINE -> statusOfflineText
                            },
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(statusOnlineText) },
                                onClick = {
                                    formData = formData.copy(status = DeviceStatus.ONLINE)
                                    statusExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(statusOfflineText) },
                                onClick = {
                                    formData = formData.copy(status = DeviceStatus.OFFLINE)
                                    statusExpanded = false
                                }
                            )
                        }
                    }
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
                                onSubmit(formData.name, formData.type, formData.status)
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

@Composable
private fun DeviceFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
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
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = error != null,
            singleLine = true,
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

private object DeviceFormDialogPreviewData {
    val sampleDevice = Device(
        id = "1",
        name = "Sensor Temperatura A1",
        type = DeviceType.SENSOR,
        status = DeviceStatus.ONLINE,
        clientId = "client1"
    )
}

@Preview
@Composable
private fun DeviceFormDialogCreatePreview() {
    GreenhouseAdminTheme {
        DeviceFormDialog(mode = DeviceFormMode.Create)
    }
}

@Preview
@Composable
private fun DeviceFormDialogEditPreview() {
    GreenhouseAdminTheme {
        DeviceFormDialog(mode = DeviceFormMode.Edit(DeviceFormDialogPreviewData.sampleDevice))
    }
}
