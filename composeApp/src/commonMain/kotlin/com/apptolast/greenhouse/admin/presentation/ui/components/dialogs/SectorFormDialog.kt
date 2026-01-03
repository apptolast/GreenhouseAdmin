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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.GreenhouseStatus
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
import greenhouseadmin.composeapp.generated.resources.error_area_invalid
import greenhouseadmin.composeapp.generated.resources.error_area_positive
import greenhouseadmin.composeapp.generated.resources.error_area_required
import greenhouseadmin.composeapp.generated.resources.error_greenhouse_required
import greenhouseadmin.composeapp.generated.resources.error_name_min_length
import greenhouseadmin.composeapp.generated.resources.field_area
import greenhouseadmin.composeapp.generated.resources.field_greenhouse
import greenhouseadmin.composeapp.generated.resources.label_name
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
    onSubmit: (name: String, greenhouseId: String, greenhouseName: String, area: Double) -> Unit = { _, _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is SectorFormMode.Create -> SectorFormData()
            is SectorFormMode.Edit -> SectorFormData(
                name = mode.sector.name,
                greenhouseId = mode.sector.greenhouseId,
                area = mode.sector.area.toString()
            )
        }
    }

    var formData by remember(mode) { mutableStateOf(initialFormData) }
    var validationErrors by remember { mutableStateOf(SectorFormData.ValidationErrors()) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var greenhouseExpanded by remember { mutableStateOf(false) }

    // Get error message strings
    val nameErrorMsg = stringResource(Res.string.error_name_min_length)
    val greenhouseRequiredMsg = stringResource(Res.string.error_greenhouse_required)
    val areaRequiredMsg = stringResource(Res.string.error_area_required)
    val areaInvalidMsg = stringResource(Res.string.error_area_invalid)
    val areaPositiveMsg = stringResource(Res.string.error_area_positive)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_name_min_length" -> nameErrorMsg
            "error_greenhouse_required" -> greenhouseRequiredMsg
            "error_area_required" -> areaRequiredMsg
            "error_area_invalid" -> areaInvalidMsg
            "error_area_positive" -> areaPositiveMsg
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

                // Name field
                SectorFormTextField(
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

                // Greenhouse dropdown
                Column {
                    Text(
                        text = stringResource(Res.string.field_greenhouse),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = greenhouseExpanded,
                        onExpandedChange = { if (!isSubmitting) greenhouseExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedGreenhouse?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isSubmitting,
                            isError = hasAttemptedSubmit && validationErrors.greenhouseId != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = greenhouseExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                errorBorderColor = MaterialTheme.colorScheme.error
                            )
                        )

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

                // Area field
                SectorFormTextField(
                    value = formData.area,
                    onValueChange = {
                        formData = formData.copy(area = it)
                        if (hasAttemptedSubmit) validationErrors = formData.validate()
                    },
                    label = stringResource(Res.string.field_area),
                    error = getErrorMessage(validationErrors.area),
                    enabled = !isSubmitting,
                    keyboardType = KeyboardType.Decimal
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
                                val greenhouseName = selectedGreenhouse?.name ?: ""
                                onSubmit(formData.name, formData.greenhouseId, greenhouseName, formData.areaValue)
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
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
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
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
        name = "Sector Norte A",
        greenhouseId = "gh1",
        greenhouseName = "Invernadero Principal",
        area = 150.0,
        clientId = "client1"
    )

    val sampleGreenhouses = listOf(
        Greenhouse(
            id = "gh1",
            name = "Invernadero Principal",
            description = "Produccion de tomates",
            status = GreenhouseStatus.ACTIVE,
            clientId = "client1"
        ),
        Greenhouse(
            id = "gh2",
            name = "Invernadero Norte",
            description = "Cultivo de lechugas",
            status = GreenhouseStatus.ACTIVE,
            clientId = "client1"
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
