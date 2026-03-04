package com.apptolast.greenhouse.admin.presentation.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.ActuatorState
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.viewmodel.ActuatorStateFormMode
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.button_add
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.button_save
import greenhouseadmin.composeapp.generated.resources.catalog_empty_message
import greenhouseadmin.composeapp.generated.resources.dialog_create_actuator_state
import greenhouseadmin.composeapp.generated.resources.dialog_edit_actuator_state
import greenhouseadmin.composeapp.generated.resources.field_color
import greenhouseadmin.composeapp.generated.resources.field_description
import greenhouseadmin.composeapp.generated.resources.field_display_order
import greenhouseadmin.composeapp.generated.resources.field_is_operational
import greenhouseadmin.composeapp.generated.resources.field_name
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_color
import greenhouseadmin.composeapp.generated.resources.header_description
import greenhouseadmin.composeapp.generated.resources.header_id
import greenhouseadmin.composeapp.generated.resources.header_name
import greenhouseadmin.composeapp.generated.resources.header_operational
import greenhouseadmin.composeapp.generated.resources.header_order
import greenhouseadmin.composeapp.generated.resources.label_no
import greenhouseadmin.composeapp.generated.resources.label_yes
import greenhouseadmin.composeapp.generated.resources.settings_tab_actuator_states
import org.jetbrains.compose.resources.stringResource

/**
 * Tab content for Actuator States management with full CRUD support.
 */
@Composable
fun SettingsActuatorStatesTab(
    actuatorStates: List<ActuatorState>,
    showDialog: Boolean,
    formMode: ActuatorStateFormMode,
    isSubmitting: Boolean,
    submitError: String?,
    onAddClicked: () -> Unit,
    onEditClicked: (ActuatorState) -> Unit,
    onDeleteClicked: (ActuatorState) -> Unit,
    onSubmit: (name: String, description: String?, isOperational: Boolean, displayOrder: Short, color: String?) -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current
    val scrollState = rememberScrollState()

    if (showDialog) {
        ActuatorStateFormDialog(
            formMode = formMode,
            isSubmitting = isSubmitting,
            submitError = submitError,
            onSubmit = onSubmit,
            onDismiss = onDismissDialog
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(
                horizontal = if (windowInfo.isCompact) 16.dp else 24.dp,
                vertical = 24.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 1000.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.settings_tab_actuator_states),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Button(onClick = onAddClicked) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.button_add))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (actuatorStates.isEmpty()) {
                Text(
                    text = stringResource(Res.string.catalog_empty_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                ActuatorStatesTable(
                    actuatorStates = actuatorStates,
                    onEdit = onEditClicked,
                    onDelete = onDeleteClicked
                )
            }
        }
    }
}

@Composable
private fun ActuatorStatesTable(
    actuatorStates: List<ActuatorState>,
    onEdit: (ActuatorState) -> Unit,
    onDelete: (ActuatorState) -> Unit,
    modifier: Modifier = Modifier
) {
    val yesText = stringResource(Res.string.label_yes)
    val noText = stringResource(Res.string.label_no)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    stringResource(Res.string.header_id),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.4f)
                )
                Text(
                    stringResource(Res.string.header_name),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    stringResource(Res.string.header_description),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    stringResource(Res.string.header_operational),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.7f)
                )
                Text(
                    stringResource(Res.string.header_order),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.5f)
                )
                Text(
                    stringResource(Res.string.header_color),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.6f)
                )
                Text(
                    stringResource(Res.string.header_actions),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(80.dp),
                    textAlign = TextAlign.Center
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Data rows
            actuatorStates.forEach { state ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        state.id.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(0.4f)
                    )
                    Text(
                        state.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        state.description ?: "-",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        if (state.isOperational) yesText else noText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (state.isOperational) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (state.isOperational) FontWeight.Medium else FontWeight.Normal,
                        modifier = Modifier.weight(0.7f)
                    )
                    Text(
                        state.displayOrder.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.5f)
                    )
                    // Color indicator
                    Row(
                        modifier = Modifier.weight(0.6f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.color != null) {
                            val colorValue = parseHexColor(state.color)
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(colorValue)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                state.color,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                "-",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row(modifier = Modifier.width(80.dp), horizontalArrangement = Arrangement.Center) {
                        IconButton(onClick = { onEdit(state) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(Res.string.action_edit),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDelete(state) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(Res.string.action_delete),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}

/**
 * Parse hex color string to Compose Color.
 */
private fun parseHexColor(hex: String): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorLong = cleanHex.toLong(16)
        when (cleanHex.length) {
            6 -> Color(0xFF000000 or colorLong)
            8 -> Color(colorLong)
            else -> Color.Gray
        }
    } catch (e: Exception) {
        Color.Gray
    }
}

@Composable
private fun ActuatorStateFormDialog(
    formMode: ActuatorStateFormMode,
    isSubmitting: Boolean,
    submitError: String?,
    onSubmit: (name: String, description: String?, isOperational: Boolean, displayOrder: Short, color: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val isEditMode = formMode is ActuatorStateFormMode.Edit
    val existing = (formMode as? ActuatorStateFormMode.Edit)?.actuatorState

    var name by remember(formMode) { mutableStateOf(existing?.name ?: "") }
    var description by remember(formMode) { mutableStateOf(existing?.description ?: "") }
    var isOperational by remember(formMode) { mutableStateOf(existing?.isOperational ?: false) }
    var displayOrder by remember(formMode) { mutableStateOf(existing?.displayOrder?.toString() ?: "0") }
    var color by remember(formMode) { mutableStateOf(existing?.color ?: "") }

    AlertDialog(
        onDismissRequest = {},
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = stringResource(if (isEditMode) Res.string.dialog_edit_actuator_state else Res.string.dialog_create_actuator_state),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(Res.string.field_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(Res.string.field_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isOperational, onCheckedChange = { isOperational = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.field_is_operational))
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = displayOrder,
                    onValueChange = { newValue ->
                        // Only allow numeric input
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            displayOrder = newValue
                        }
                    },
                    label = { Text(stringResource(Res.string.field_display_order)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text(stringResource(Res.string.field_color)) },
                    placeholder = { Text("#FF0000") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (color.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(color))
                            )
                        }
                    }
                )

                if (submitError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = submitError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val order = displayOrder.toShortOrNull() ?: 0
                    onSubmit(
                        name,
                        description.ifBlank { null },
                        isOperational,
                        order,
                        color.ifBlank { null }
                    )
                },
                enabled = !isSubmitting && name.isNotBlank()
            ) {
                if (isSubmitting) CircularProgressIndicator(
                    modifier = Modifier.height(16.dp).width(16.dp),
                    strokeWidth = 2.dp
                )
                else Text(stringResource(Res.string.button_save))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.button_cancel)) } }
    )
}
