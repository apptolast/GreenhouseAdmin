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
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.data.model.SectorFormData
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.SectorFormMode
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.button_create_sector
import greenhouseadmin.composeapp.generated.resources.button_save
import greenhouseadmin.composeapp.generated.resources.dialog_edit_sector_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_edit_sector_title
import greenhouseadmin.composeapp.generated.resources.dialog_new_sector_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_new_sector_title
import greenhouseadmin.composeapp.generated.resources.error_greenhouse_required
import greenhouseadmin.composeapp.generated.resources.error_variety_min_length
import greenhouseadmin.composeapp.generated.resources.field_greenhouse
import greenhouseadmin.composeapp.generated.resources.label_variety
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dialog for creating or editing a sector.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectorFormDialog(
    mode: SectorFormMode,
    greenhouses: List<Greenhouse>,
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (greenhouseId: String, variety: String) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is SectorFormMode.Create -> SectorFormData()
            is SectorFormMode.Edit -> SectorFormData(
                variety = mode.sector.variety ?: "",
                greenhouseId = mode.sector.greenhouseId
            )
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(SectorFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var greenhouseExpanded by remember { mutableStateOf(false) }

    // Get error message strings
    val varietyErrorMsg = stringResource(Res.string.error_variety_min_length)
    val greenhouseRequiredMsg = stringResource(Res.string.error_greenhouse_required)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_variety_min_length" -> varietyErrorMsg
            "error_greenhouse_required" -> greenhouseRequiredMsg
            else -> null
        }
    }

    val isEditMode = mode is SectorFormMode.Edit
    val dialogTitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_sector_title)
    } else {
        stringResource(Res.string.dialog_new_sector_title)
    }
    val dialogSubtitle = if (isEditMode) {
        stringResource(Res.string.dialog_edit_sector_subtitle)
    } else {
        stringResource(Res.string.dialog_new_sector_subtitle)
    }
    val submitButtonText = if (isEditMode) {
        stringResource(Res.string.button_save)
    } else {
        stringResource(Res.string.button_create_sector)
    }

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

                // Greenhouse dropdown (only for Create mode, or show as read-only in Edit)
                Column {
                    Text(
                        text = stringResource(Res.string.field_greenhouse),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = greenhouseExpanded && !isEditMode,
                        onExpandedChange = { if (!isSubmitting && !isEditMode) greenhouseExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedGreenhouse?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isSubmitting && !isEditMode,
                            isError = hasAttemptedSubmit && validationErrors.greenhouseId != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            trailingIcon = {
                                if (!isEditMode) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = greenhouseExpanded)
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

                // Variety field
                SectorFormTextField(
                    value = formData.variety,
                    onValueChange = {
                        formData = formData.copy(variety = it)
                        if (hasAttemptedSubmit) validationErrors = formData.validate()
                    },
                    label = stringResource(Res.string.label_variety),
                    error = getErrorMessage(validationErrors.variety),
                    enabled = !isSubmitting
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
                                onSubmit(formData.greenhouseId, formData.variety)
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
private fun SectorFormTextField(
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

private object SectorFormDialogPreviewData {
    val sampleSector = Sector(
        id = "1",
        greenhouseId = "gh1",
        variety = "Tomate Cherry"
    )

    val sampleGreenhouses = listOf(
        Greenhouse(
            id = "gh1",
            name = "Invernadero Principal",
            tenantId = "client1",
            location = null,
            areaM2 = 1500.0,
            timezone = "Europe/Madrid",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Greenhouse(
            id = "gh2",
            name = "Invernadero Norte",
            tenantId = "client1",
            location = null,
            areaM2 = 800.0,
            timezone = "Europe/Madrid",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )
    )
}

@Preview
@Composable
private fun SectorFormDialogCreatePreview() {
    GreenhouseAdminTheme {
        SectorFormDialog(
            mode = SectorFormMode.Create,
            greenhouses = SectorFormDialogPreviewData.sampleGreenhouses
        )
    }
}

@Preview
@Composable
private fun SectorFormDialogEditPreview() {
    GreenhouseAdminTheme {
        SectorFormDialog(
            mode = SectorFormMode.Edit(SectorFormDialogPreviewData.sampleSector),
            greenhouses = SectorFormDialogPreviewData.sampleGreenhouses
        )
    }
}
