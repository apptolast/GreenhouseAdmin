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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
import greenhouseadmin.composeapp.generated.resources.error_key_invalid
import greenhouseadmin.composeapp.generated.resources.error_key_min_length
import greenhouseadmin.composeapp.generated.resources.error_key_required
import greenhouseadmin.composeapp.generated.resources.error_value_required
import greenhouseadmin.composeapp.generated.resources.field_description
import greenhouseadmin.composeapp.generated.resources.field_key
import greenhouseadmin.composeapp.generated.resources.field_value
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dialog for creating or editing a setting.
 */
@Composable
fun SettingFormDialog(
    mode: SettingFormMode,
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (key: String, value: String, description: String) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is SettingFormMode.Create -> SettingFormData()
            is SettingFormMode.Edit -> SettingFormData(
                key = mode.setting.key,
                value = mode.setting.value,
                description = mode.setting.description
            )
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(SettingFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val keyRequiredMsg = stringResource(Res.string.error_key_required)
    val keyMinLengthMsg = stringResource(Res.string.error_key_min_length)
    val keyInvalidMsg = stringResource(Res.string.error_key_invalid)
    val valueRequiredMsg = stringResource(Res.string.error_value_required)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_key_required" -> keyRequiredMsg
            "error_key_min_length" -> keyMinLengthMsg
            "error_key_invalid" -> keyInvalidMsg
            "error_value_required" -> valueRequiredMsg
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

                // Key field
                SettingFormTextField(
                    value = formData.key,
                    onValueChange = {
                        formData = formData.copy(key = it)
                        if (hasAttemptedSubmit) validationErrors = formData.validate()
                    },
                    label = stringResource(Res.string.field_key),
                    error = getErrorMessage(validationErrors.key),
                    enabled = !isSubmitting && !isEditMode // Key is not editable in edit mode
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Value field
                SettingFormTextField(
                    value = formData.value,
                    onValueChange = {
                        formData = formData.copy(value = it)
                        if (hasAttemptedSubmit) validationErrors = formData.validate()
                    },
                    label = stringResource(Res.string.field_value),
                    error = getErrorMessage(validationErrors.value),
                    enabled = !isSubmitting
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description field (optional, multi-line)
                SettingFormTextField(
                    value = formData.description,
                    onValueChange = {
                        formData = formData.copy(description = it)
                    },
                    label = stringResource(Res.string.field_description),
                    error = null,
                    enabled = !isSubmitting,
                    singleLine = false,
                    maxLines = 3
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
                                onSubmit(formData.key, formData.value, formData.description)
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
private fun SettingFormTextField(
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
                errorBorderColor = MaterialTheme.colorScheme.error,
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                disabledTextColor = MaterialTheme.colorScheme.onSurface
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

private object SettingFormDialogPreviewData {
    val sampleSetting = Setting(
        id = "1",
        key = "notification_email",
        value = "test@example.com",
        description = "Email address for receiving notifications",
        clientId = "client1"
    )
}

@Preview
@Composable
private fun SettingFormDialogCreatePreview() {
    GreenhouseAdminTheme {
        SettingFormDialog(mode = SettingFormMode.Create)
    }
}

@Preview
@Composable
private fun SettingFormDialogEditPreview() {
    GreenhouseAdminTheme {
        SettingFormDialog(mode = SettingFormMode.Edit(SettingFormDialogPreviewData.sampleSetting))
    }
}
