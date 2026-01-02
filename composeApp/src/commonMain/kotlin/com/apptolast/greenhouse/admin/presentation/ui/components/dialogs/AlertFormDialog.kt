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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import com.apptolast.greenhouse.admin.data.model.Alert
import com.apptolast.greenhouse.admin.data.model.AlertFormData
import com.apptolast.greenhouse.admin.data.model.AlertSeverity
import com.apptolast.greenhouse.admin.data.model.AlertStatus
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.AlertFormMode
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.alert_status_dismissed
import greenhouseadmin.composeapp.generated.resources.alert_status_read
import greenhouseadmin.composeapp.generated.resources.alert_status_unread
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.button_create_alert
import greenhouseadmin.composeapp.generated.resources.button_save
import greenhouseadmin.composeapp.generated.resources.dialog_edit_alert_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_edit_alert_title
import greenhouseadmin.composeapp.generated.resources.dialog_new_alert_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_new_alert_title
import greenhouseadmin.composeapp.generated.resources.error_name_min_length
import greenhouseadmin.composeapp.generated.resources.error_title_required
import greenhouseadmin.composeapp.generated.resources.field_severity
import greenhouseadmin.composeapp.generated.resources.field_status
import greenhouseadmin.composeapp.generated.resources.field_title
import greenhouseadmin.composeapp.generated.resources.severity_critical
import greenhouseadmin.composeapp.generated.resources.severity_high
import greenhouseadmin.composeapp.generated.resources.severity_low
import greenhouseadmin.composeapp.generated.resources.severity_medium
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

/**
 * Dialog for creating or editing an alert.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertFormDialog(
    mode: AlertFormMode,
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (title: String, severity: AlertSeverity, status: AlertStatus) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is AlertFormMode.Create -> AlertFormData()
            is AlertFormMode.Edit -> AlertFormData(
                title = mode.alert.title,
                severity = mode.alert.severity,
                status = mode.alert.status
            )
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(AlertFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var severityExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    val titleRequiredMsg = stringResource(Res.string.error_title_required)
    val nameMinLengthMsg = stringResource(Res.string.error_name_min_length)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_title_required" -> titleRequiredMsg
            "error_name_min_length" -> nameMinLengthMsg
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

    // Severity options
    val severityLowText = stringResource(Res.string.severity_low)
    val severityMediumText = stringResource(Res.string.severity_medium)
    val severityHighText = stringResource(Res.string.severity_high)
    val severityCriticalText = stringResource(Res.string.severity_critical)

    // Status options
    val statusUnreadText = stringResource(Res.string.alert_status_unread)
    val statusReadText = stringResource(Res.string.alert_status_read)
    val statusDismissedText = stringResource(Res.string.alert_status_dismissed)

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

                // Title field
                AlertFormTextField(
                    value = formData.title,
                    onValueChange = {
                        formData = formData.copy(title = it)
                        if (hasAttemptedSubmit) validationErrors = formData.validate()
                    },
                    label = stringResource(Res.string.field_title),
                    error = getErrorMessage(validationErrors.title),
                    enabled = !isSubmitting
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Severity dropdown
                Column {
                    Text(
                        text = stringResource(Res.string.field_severity),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = severityExpanded,
                        onExpandedChange = { if (!isSubmitting) severityExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = when (formData.severity) {
                                AlertSeverity.LOW -> severityLowText
                                AlertSeverity.MEDIUM -> severityMediumText
                                AlertSeverity.HIGH -> severityHighText
                                AlertSeverity.CRITICAL -> severityCriticalText
                            },
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = severityExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = severityExpanded,
                            onDismissRequest = { severityExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(severityLowText) },
                                onClick = {
                                    formData = formData.copy(severity = AlertSeverity.LOW)
                                    severityExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(severityMediumText) },
                                onClick = {
                                    formData = formData.copy(severity = AlertSeverity.MEDIUM)
                                    severityExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(severityHighText) },
                                onClick = {
                                    formData = formData.copy(severity = AlertSeverity.HIGH)
                                    severityExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(severityCriticalText) },
                                onClick = {
                                    formData = formData.copy(severity = AlertSeverity.CRITICAL)
                                    severityExpanded = false
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
                                AlertStatus.UNREAD -> statusUnreadText
                                AlertStatus.READ -> statusReadText
                                AlertStatus.DISMISSED -> statusDismissedText
                            },
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
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
                                text = { Text(statusUnreadText) },
                                onClick = {
                                    formData = formData.copy(status = AlertStatus.UNREAD)
                                    statusExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(statusReadText) },
                                onClick = {
                                    formData = formData.copy(status = AlertStatus.READ)
                                    statusExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(statusDismissedText) },
                                onClick = {
                                    formData = formData.copy(status = AlertStatus.DISMISSED)
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
                                onSubmit(formData.title, formData.severity, formData.status)
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
private fun AlertFormTextField(
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

private object AlertFormDialogPreviewData {
    val sampleAlert = Alert(
        id = "1",
        title = "Temperatura alta en Sector A",
        severity = AlertSeverity.HIGH,
        status = AlertStatus.UNREAD,
        createdAt = Clock.System.now().toEpochMilliseconds(),
        clientId = "client1"
    )
}

@Preview
@Composable
private fun AlertFormDialogCreatePreview() {
    GreenhouseAdminTheme {
        AlertFormDialog(mode = AlertFormMode.Create)
    }
}

@Preview
@Composable
private fun AlertFormDialogEditPreview() {
    GreenhouseAdminTheme {
        AlertFormDialog(mode = AlertFormMode.Edit(AlertFormDialogPreviewData.sampleAlert))
    }
}
