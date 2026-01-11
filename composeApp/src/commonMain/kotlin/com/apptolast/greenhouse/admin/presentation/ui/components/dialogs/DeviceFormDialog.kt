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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apptolast.greenhouse.admin.data.model.Device
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogCategory
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogType
import com.apptolast.greenhouse.admin.data.model.DeviceCatalogUnit
import com.apptolast.greenhouse.admin.data.model.DeviceFormData
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.DeviceFormMode
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.button_create_device
import greenhouseadmin.composeapp.generated.resources.button_save
import greenhouseadmin.composeapp.generated.resources.dialog_edit_device_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_edit_device_title
import greenhouseadmin.composeapp.generated.resources.dialog_new_device_subtitle
import greenhouseadmin.composeapp.generated.resources.dialog_new_device_title
import greenhouseadmin.composeapp.generated.resources.error_greenhouse_required
import greenhouseadmin.composeapp.generated.resources.error_name_max_length
import greenhouseadmin.composeapp.generated.resources.error_type_required
import greenhouseadmin.composeapp.generated.resources.label_category
import greenhouseadmin.composeapp.generated.resources.label_device_name
import greenhouseadmin.composeapp.generated.resources.label_device_type
import greenhouseadmin.composeapp.generated.resources.label_greenhouse
import greenhouseadmin.composeapp.generated.resources.label_is_active
import greenhouseadmin.composeapp.generated.resources.label_unit
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dialog for creating or editing a device.
 * Uses catalog data for categories, types, and units.
 * Types are filtered locally based on the selected category.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceFormDialog(
    mode: DeviceFormMode,
    greenhouses: List<Greenhouse> = emptyList(),
    categories: List<DeviceCatalogCategory> = emptyList(),
    allTypes: List<DeviceCatalogType> = emptyList(),
    units: List<DeviceCatalogUnit> = emptyList(),
    isLoadingCatalog: Boolean = false,
    isSubmitting: Boolean = false,
    error: String? = null,
    onSubmit: (greenhouseId: Long?, name: String, categoryId: Short?, typeId: Short?, unitId: Short?, isActive: Boolean) -> Unit = { _, _, _, _, _, _ -> },
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialFormData = remember(mode) {
        when (mode) {
            is DeviceFormMode.Create -> DeviceFormData()
            is DeviceFormMode.Edit -> DeviceFormData(
                greenhouseId = mode.device.greenhouseId,
                name = mode.device.name ?: "",
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
    var typeExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    val greenhouseErrorMsg = stringResource(Res.string.error_greenhouse_required)
    val nameMaxLengthErrorMsg = stringResource(Res.string.error_name_max_length)
    val typeErrorMsg = stringResource(Res.string.error_type_required)

    fun getErrorMessage(errorKey: String?): String? {
        return when (errorKey) {
            "error_greenhouse_required" -> greenhouseErrorMsg
            "error_name_max_length" -> nameMaxLengthErrorMsg
            "error_type_required" -> typeErrorMsg
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

    // Filter types by selected category (local filtering)
    val filteredTypes = remember(formData.categoryId, allTypes) {
        if (formData.categoryId != null) {
            allTypes.filter { it.categoryId == formData.categoryId }
        } else {
            emptyList()
        }
    }

    // Find selected items
    val selectedGreenhouse = greenhouses.find { it.id == formData.greenhouseId }
    val selectedCategory = categories.find { it.id == formData.categoryId }
    val selectedType = filteredTypes.find { it.id == formData.typeId }
    val selectedUnit = units.find { it.id == formData.unitId }

    // Auto-select unit when type changes (if type has a default unit)
    LaunchedEffect(formData.typeId) {
        val type = filteredTypes.find { it.id == formData.typeId }
        if (type?.defaultUnitId != null && formData.unitId == null) {
            formData = formData.copy(unitId = type.defaultUnitId)
        }
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

                // Loading indicator for catalog
                if (isLoadingCatalog) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Loading catalog...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

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

                // Name text field (optional)
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
                            if (it.length <= DeviceFormData.MAX_NAME_LENGTH) {
                                formData = formData.copy(name = it)
                                if (hasAttemptedSubmit) validationErrors = formData.validate()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSubmitting,
                        isError = hasAttemptedSubmit && validationErrors.name != null,
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "e.g., Sensor Temperatura Norte",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        supportingText = {
                            Text(
                                text = "${formData.name.length}/${DeviceFormData.MAX_NAME_LENGTH}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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

                // Category dropdown
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
                            value = selectedCategory?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            placeholder = {
                                Text(
                                    text = "Select category...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            isError = hasAttemptedSubmit && validationErrors.categoryId != null,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                errorBorderColor = MaterialTheme.colorScheme.error
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        formData = formData.copy(
                                            categoryId = category.id,
                                            typeId = null, // Reset type when category changes
                                            unitId = null  // Reset unit when category changes
                                        )
                                        categoryExpanded = false
                                        // Types are filtered locally based on categoryId
                                        if (hasAttemptedSubmit) validationErrors = formData.validate()
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Type dropdown (filtered by category)
                Column {
                    Text(
                        text = stringResource(Res.string.label_device_type),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { if (!isSubmitting && formData.categoryId != null) typeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedType?.let { "${it.name}${it.description?.let { d -> " - $d" } ?: ""}" }
                                ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isSubmitting && formData.categoryId != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            placeholder = {
                                Text(
                                    text = if (formData.categoryId == null) "Select category first..." else "Select type...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            },
                            trailingIcon = {
                                if (formData.categoryId != null) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                                }
                            },
                            isError = hasAttemptedSubmit && validationErrors.typeId != null,
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
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            filteredTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(type.name)
                                            type.description?.let { desc ->
                                                Text(
                                                    text = desc,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        formData = formData.copy(
                                            typeId = type.id,
                                            unitId = type.defaultUnitId // Auto-select default unit
                                        )
                                        typeExpanded = false
                                        if (hasAttemptedSubmit) validationErrors = formData.validate()
                                    }
                                )
                            }
                        }
                    }

                    if (hasAttemptedSubmit && validationErrors.typeId != null) {
                        Text(
                            text = getErrorMessage(validationErrors.typeId) ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Unit dropdown (optional, auto-selected from type default)
                Column {
                    Text(
                        text = stringResource(Res.string.label_unit),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = unitExpanded,
                        onExpandedChange = { if (!isSubmitting) unitExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedUnit?.let { "${it.symbol} - ${it.name}" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            placeholder = {
                                Text(
                                    text = "Select unit (optional)...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = unitExpanded,
                            onDismissRequest = { unitExpanded = false }
                        ) {
                            units.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text("${unit.symbol} - ${unit.name}") },
                                    onClick = {
                                        formData = formData.copy(unitId = unit.id)
                                        unitExpanded = false
                                    }
                                )
                            }
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
            id = 1L,
            code = "GRH-00001",
            name = "Main Greenhouse",
            tenantId = 1L,
            location = null,
            areaM2 = 1500.0,
            timezone = "Europe/Madrid",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        ),
        Greenhouse(
            id = 2L,
            code = "GRH-00002",
            name = "North Greenhouse",
            tenantId = 1L,
            location = null,
            areaM2 = 800.0,
            timezone = "Europe/Madrid",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )
    )

    val sampleCategories = listOf(
        DeviceCatalogCategory(1, "CAT-00001", "SENSOR"),
        DeviceCatalogCategory(2, "CAT-00002", "ACTUATOR")
    )

    val sampleTypes = listOf(
        DeviceCatalogType(1, "TYP-00001", "TEMPERATURE", "Temperature sensor", 1, 1, "°C", null),
        DeviceCatalogType(2, "TYP-00002", "HUMIDITY", "Humidity sensor", 1, 3, "%", null)
    )

    val sampleUnits = listOf(
        DeviceCatalogUnit(1, "UNT-00001", "°C", "Celsius"),
        DeviceCatalogUnit(2, "UNT-00002", "°F", "Fahrenheit"),
        DeviceCatalogUnit(3, "UNT-00003", "%", "Percentage")
    )

    val sampleDevice = Device(
        id = 1L,
        code = "DEV-00001",
        tenantId = 1L,
        greenhouseId = 1L,
        name = "Sensor Temperatura Norte",
        categoryId = Device.CATEGORY_SENSOR,
        categoryName = "SENSOR",
        typeId = 1,
        typeName = "TEMPERATURE",
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
            greenhouses = DeviceFormDialogPreviewData.sampleGreenhouses,
            categories = DeviceFormDialogPreviewData.sampleCategories,
            allTypes = DeviceFormDialogPreviewData.sampleTypes,
            units = DeviceFormDialogPreviewData.sampleUnits
        )
    }
}

@Preview
@Composable
private fun DeviceFormDialogEditPreview() {
    GreenhouseAdminTheme {
        DeviceFormDialog(
            mode = DeviceFormMode.Edit(DeviceFormDialogPreviewData.sampleDevice),
            greenhouses = DeviceFormDialogPreviewData.sampleGreenhouses,
            categories = DeviceFormDialogPreviewData.sampleCategories,
            allTypes = DeviceFormDialogPreviewData.sampleTypes,
            units = DeviceFormDialogPreviewData.sampleUnits
        )
    }
}
