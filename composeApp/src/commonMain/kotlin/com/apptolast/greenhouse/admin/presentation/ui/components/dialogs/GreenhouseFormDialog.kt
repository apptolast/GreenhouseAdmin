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
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.GreenhouseFormData
import com.apptolast.greenhouse.admin.data.model.Location
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.GreenhouseFormMode
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.button_create_greenhouse
import greenhouseadmin.composeapp.generated.resources.button_save
import greenhouseadmin.composeapp.generated.resources.dialog_edit_greenhouse_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_edit_greenhouse_title
import greenhouseadmin.composeapp.generated.resources.dialog_new_greenhouse_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_new_greenhouse_title
import greenhouseadmin.composeapp.generated.resources.error_area_invalid
import greenhouseadmin.composeapp.generated.resources.error_area_positive
import greenhouseadmin.composeapp.generated.resources.error_name_min_length
import greenhouseadmin.composeapp.generated.resources.field_area
import greenhouseadmin.composeapp.generated.resources.label_latitude
import greenhouseadmin.composeapp.generated.resources.label_longitude
import greenhouseadmin.composeapp.generated.resources.label_name
import greenhouseadmin.composeapp.generated.resources.label_status
import greenhouseadmin.composeapp.generated.resources.status_active
import greenhouseadmin.composeapp.generated.resources.status_inactive
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dialog for creating or editing a greenhouse.
 */
@Composable
fun GreenhouseFormDialog(
    mode: GreenhouseFormMode,
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (name: String, location: Location?, areaM2: Double?, timezone: String?, isActive: Boolean) -> Unit = { _, _, _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is GreenhouseFormMode.Create -> GreenhouseFormData()
            is GreenhouseFormMode.Edit -> GreenhouseFormData.fromGreenhouse(mode.greenhouse)
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(GreenhouseFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val nameErrorMsg = stringResource(Res.string.error_name_min_length)
    val areaInvalidMsg = stringResource(Res.string.error_area_invalid)
    val areaPositiveMsg = stringResource(Res.string.error_area_positive)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_name_min_length" -> nameErrorMsg
            "error_area_invalid" -> areaInvalidMsg
            "error_area_positive" -> areaPositiveMsg
            "error_coordinate_invalid" -> "Invalid coordinate"
            "error_latitude_range" -> "Latitude must be between -90 and 90"
            "error_longitude_range" -> "Longitude must be between -180 and 180"
            else -> null
        }
    }

    val isEditMode = mode is GreenhouseFormMode.Edit
    val dialogTitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_greenhouse_title)
    } else {
        stringResource(Res.string.dialog_new_greenhouse_title)
    }
    val dialogSubtitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_greenhouse_subtitle)
    } else {
        stringResource(Res.string.dialog_new_greenhouse_subtitle)
    }
    val submitButtonText = if (isEditMode) {
        stringResource(Res.string.button_save)
    } else {
        stringResource(Res.string.button_create_greenhouse)
    }

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
                GreenhouseFormTextField(
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

                // Location fields (Latitude & Longitude)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GreenhouseFormTextField(
                        value = formData.latitude,
                        onValueChange = {
                            formData = formData.copy(latitude = it)
                            if (hasAttemptedSubmit) validationErrors = formData.validate()
                        },
                        label = stringResource(Res.string.label_latitude),
                        error = getErrorMessage(validationErrors.latitude),
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )

                    GreenhouseFormTextField(
                        value = formData.longitude,
                        onValueChange = {
                            formData = formData.copy(longitude = it)
                            if (hasAttemptedSubmit) validationErrors = formData.validate()
                        },
                        label = stringResource(Res.string.label_longitude),
                        error = getErrorMessage(validationErrors.longitude),
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Area field
                GreenhouseFormTextField(
                    value = formData.areaM2,
                    onValueChange = {
                        formData = formData.copy(areaM2 = it)
                        if (hasAttemptedSubmit) validationErrors = formData.validate()
                    },
                    label = stringResource(Res.string.field_area),
                    error = getErrorMessage(validationErrors.areaM2),
                    enabled = !isSubmitting
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status switch
                StatusSwitch(
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
                                    formData.name,
                                    formData.location,
                                    formData.areaM2AsDouble,
                                    formData.timezone.takeIf { it.isNotBlank() },
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

@Composable
private fun GreenhouseFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    maxLines: Int = 1
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
            singleLine = singleLine,
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

@Composable
private fun StatusSwitch(
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

        Spacer(modifier = Modifier.height(8.dp))

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

private object GreenhouseFormDialogPreviewData {
    val sampleGreenhouse = Greenhouse(
        id = "1",
        name = "Invernadero Principal",
        tenantId = "client1",
        location = Location(lat = 36.8381, lon = -2.4597),
        areaM2 = 1500.0,
        timezone = "Europe/Madrid",
        isActive = true,
        createdAt = "2024-01-01T00:00:00Z",
        updatedAt = "2024-01-01T00:00:00Z"
    )
}

@Preview
@Composable
private fun GreenhouseFormDialogCreatePreview() {
    GreenhouseAdminTheme {
        GreenhouseFormDialog(mode = GreenhouseFormMode.Create)
    }
}

@Preview
@Composable
private fun GreenhouseFormDialogEditPreview() {
    GreenhouseAdminTheme {
        GreenhouseFormDialog(mode = GreenhouseFormMode.Edit(GreenhouseFormDialogPreviewData.sampleGreenhouse))
    }
}
