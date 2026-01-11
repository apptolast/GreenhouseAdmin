package com.apptolast.greenhouse.admin.presentation.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.data.model.Location
import com.apptolast.greenhouse.admin.data.model.NewClientFormData
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.button_create
import greenhouseadmin.composeapp.generated.resources.button_save
import greenhouseadmin.composeapp.generated.resources.dialog_edit_client_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_edit_client_title
import greenhouseadmin.composeapp.generated.resources.dialog_new_client_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_new_client_title
import greenhouseadmin.composeapp.generated.resources.error_country_required
import greenhouseadmin.composeapp.generated.resources.error_email_invalid
import greenhouseadmin.composeapp.generated.resources.error_name_min_length
import greenhouseadmin.composeapp.generated.resources.error_phone_required
import greenhouseadmin.composeapp.generated.resources.error_province_required
import greenhouseadmin.composeapp.generated.resources.label_country
import greenhouseadmin.composeapp.generated.resources.label_email
import greenhouseadmin.composeapp.generated.resources.label_latitude
import greenhouseadmin.composeapp.generated.resources.label_longitude
import greenhouseadmin.composeapp.generated.resources.label_name
import greenhouseadmin.composeapp.generated.resources.label_phone
import greenhouseadmin.composeapp.generated.resources.label_province
import greenhouseadmin.composeapp.generated.resources.label_status
import greenhouseadmin.composeapp.generated.resources.status_active
import greenhouseadmin.composeapp.generated.resources.status_inactive
import greenhouseadmin.composeapp.generated.resources.status_pending
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Represents the mode for the client form dialog.
 */
sealed interface ClientFormMode {
    data object Create : ClientFormMode
    data class Edit(val client: Client) : ClientFormMode
}

/**
 * Generic dialog for creating or editing a client.
 */
@Composable
fun ClientFormDialog(
    mode: ClientFormMode,
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (
        id: Long?,
        name: String,
        email: String,
        phone: String,
        province: String,
        country: String,
        location: Location?,
        status: ClientStatus
    ) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is ClientFormMode.Create -> NewClientFormData()
            is ClientFormMode.Edit -> NewClientFormData(
                name = mode.client.name,
                email = mode.client.email,
                phone = mode.client.phone,
                province = mode.client.province,
                country = mode.client.country,
                latitude = mode.client.location?.lat?.toString() ?: "",
                longitude = mode.client.location?.lon?.toString() ?: "",
                status = mode.client.status
            )
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(NewClientFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val nameErrorMsg = stringResource(Res.string.error_name_min_length)
    val emailErrorMsg = stringResource(Res.string.error_email_invalid)
    val phoneErrorMsg = stringResource(Res.string.error_phone_required)
    val provinceErrorMsg = stringResource(Res.string.error_province_required)
    val countryErrorMsg = stringResource(Res.string.error_country_required)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_name_min_length" -> nameErrorMsg
            "error_email_invalid" -> emailErrorMsg
            "error_phone_required" -> phoneErrorMsg
            "error_province_required" -> provinceErrorMsg
            "error_country_required" -> countryErrorMsg
            else -> null
        }
    }

    val isEditMode = mode is ClientFormMode.Edit
    val dialogTitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_client_title)
    } else {
        stringResource(Res.string.dialog_new_client_title)
    }
    val dialogSubtitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_client_subtitle)
    } else {
        stringResource(Res.string.dialog_new_client_subtitle)
    }
    val submitButtonText = if (isEditMode) {
        stringResource(Res.string.button_save)
    } else {
        stringResource(Res.string.button_create)
    }

    Dialog(onDismissRequest = onDismiss) {
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
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dialogSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                FormTextField(
                    value = formData.name,
                    onValueChange = {
                        formData = formData.copy(name = it)
                        if (hasAttemptedSubmit) {
                            validationErrors = formData.validate()
                        }
                    },
                    label = stringResource(Res.string.label_name),
                    error = getErrorMessage(validationErrors.name),
                    enabled = !isSubmitting
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FormTextField(
                        value = formData.email,
                        onValueChange = {
                            formData = formData.copy(email = it)
                            if (hasAttemptedSubmit) {
                                validationErrors = formData.validate()
                            }
                        },
                        label = stringResource(Res.string.label_email),
                        error = getErrorMessage(validationErrors.email),
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                    FormTextField(
                        value = formData.phone,
                        onValueChange = {
                            formData = formData.copy(phone = it)
                            if (hasAttemptedSubmit) {
                                validationErrors = formData.validate()
                            }
                        },
                        label = stringResource(Res.string.label_phone),
                        error = getErrorMessage(validationErrors.phone),
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FormTextField(
                        value = formData.province,
                        onValueChange = {
                            formData = formData.copy(province = it)
                            if (hasAttemptedSubmit) {
                                validationErrors = formData.validate()
                            }
                        },
                        label = stringResource(Res.string.label_province),
                        error = getErrorMessage(validationErrors.province),
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                    FormTextField(
                        value = formData.country,
                        onValueChange = {
                            formData = formData.copy(country = it)
                            if (hasAttemptedSubmit) {
                                validationErrors = formData.validate()
                            }
                        },
                        label = stringResource(Res.string.label_country),
                        error = getErrorMessage(validationErrors.country),
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Location (Latitude and Longitude)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FormTextField(
                        value = formData.latitude,
                        onValueChange = { formData = formData.copy(latitude = it) },
                        label = stringResource(Res.string.label_latitude),
                        error = null,
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                    FormTextField(
                        value = formData.longitude,
                        onValueChange = { formData = formData.copy(longitude = it) },
                        label = stringResource(Res.string.label_longitude),
                        error = null,
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                StatusDropdown(
                    value = formData.status,
                    onValueChange = { formData = formData.copy(status = it) },
                    label = stringResource(Res.string.label_status),
                    enabled = !isSubmitting
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                                val clientId = when (mode) {
                                    is ClientFormMode.Create -> null
                                    is ClientFormMode.Edit -> mode.client.id
                                }
                                onSubmit(
                                    clientId,
                                    formData.name,
                                    formData.email,
                                    formData.phone,
                                    formData.province,
                                    formData.country,
                                    formData.toLocation(),
                                    formData.status
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
                                modifier = Modifier.height(18.dp).width(18.dp),
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
private fun FormTextField(
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

@Composable
private fun FormDropdown(
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = { if (enabled) expanded = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = enabled,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (value.isEmpty()) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        ) {
            Text(
                text = value.ifEmpty { label },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }

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
private fun StatusDropdown(
    value: ClientStatus,
    onValueChange: (ClientStatus) -> Unit,
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val statusText = when (value) {
        ClientStatus.ACTIVE -> stringResource(Res.string.status_active)
        ClientStatus.PENDING -> stringResource(Res.string.status_pending)
        ClientStatus.INACTIVE -> stringResource(Res.string.status_inactive)
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = { if (enabled) expanded = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = enabled,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ClientStatus.entries.forEach { status ->
                val text = when (status) {
                    ClientStatus.ACTIVE -> stringResource(Res.string.status_active)
                    ClientStatus.PENDING -> stringResource(Res.string.status_pending)
                    ClientStatus.INACTIVE -> stringResource(Res.string.status_inactive)
                }
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onValueChange(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

private object ClientFormDialogPreviewData {
    val sampleClient = Client(
        id = 1L,
        code = "TNT-00001",
        name = "Elena Rodriguez",
        email = "elena@freshveg.com",
        phone = "+34 612 345 678",
        province = "Almeria",
        country = "España",
        status = ClientStatus.ACTIVE
    )
}

@Preview
@Composable
private fun ClientFormDialogCreatePreview() {
    GreenhouseAdminTheme {
        ClientFormDialog(
            mode = ClientFormMode.Create
        )
    }
}

@Preview
@Composable
private fun ClientFormDialogEditPreview() {
    GreenhouseAdminTheme {
        ClientFormDialog(
            mode = ClientFormMode.Edit(ClientFormDialogPreviewData.sampleClient)
        )
    }
}
