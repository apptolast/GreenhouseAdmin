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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.AlertSeverityCatalog
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.viewmodel.AlertSeverityFormMode
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.button_add
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.button_save
import greenhouseadmin.composeapp.generated.resources.catalog_empty_message
import greenhouseadmin.composeapp.generated.resources.dialog_create_alert_severity
import greenhouseadmin.composeapp.generated.resources.dialog_edit_alert_severity
import greenhouseadmin.composeapp.generated.resources.error_level_invalid
import greenhouseadmin.composeapp.generated.resources.field_color
import greenhouseadmin.composeapp.generated.resources.field_level
import greenhouseadmin.composeapp.generated.resources.field_name
import greenhouseadmin.composeapp.generated.resources.field_requires_action
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_color
import greenhouseadmin.composeapp.generated.resources.header_id
import greenhouseadmin.composeapp.generated.resources.header_level
import greenhouseadmin.composeapp.generated.resources.header_name
import greenhouseadmin.composeapp.generated.resources.settings_tab_alert_severities
import org.jetbrains.compose.resources.stringResource

/**
 * Tab content for Alert Severities management.
 */
@Composable
fun SettingsAlertSeveritiesTab(
    severities: List<AlertSeverityCatalog>,
    showDialog: Boolean,
    formMode: AlertSeverityFormMode,
    isSubmitting: Boolean,
    submitError: String?,
    onAddClicked: () -> Unit,
    onEditClicked: (AlertSeverityCatalog) -> Unit,
    onDeleteClicked: (AlertSeverityCatalog) -> Unit,
    onSubmit: (name: String, level: Short, description: String?, color: String?, requiresAction: Boolean, notificationDelayMinutes: Int) -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current
    val scrollState = rememberScrollState()

    if (showDialog) {
        AlertSeverityFormDialog(
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
        Column(modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.settings_tab_alert_severities),
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

            if (severities.isEmpty()) {
                Text(
                    text = stringResource(Res.string.catalog_empty_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                AlertSeveritiesTable(severities = severities, onEdit = onEditClicked, onDelete = onDeleteClicked)
            }
        }
    }
}

@Composable
private fun AlertSeveritiesTable(
    severities: List<AlertSeverityCatalog>,
    onEdit: (AlertSeverityCatalog) -> Unit,
    onDelete: (AlertSeverityCatalog) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    stringResource(Res.string.header_level),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.6f)
                )
                Text(
                    stringResource(Res.string.header_color),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.8f)
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

            severities.forEach { severity ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        severity.id.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(0.4f)
                    )
                    Text(
                        severity.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1.5f)
                    )
                    Text(
                        severity.level.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(0.6f)
                    )
                    Row(modifier = Modifier.weight(0.8f), verticalAlignment = Alignment.CenterVertically) {
                        if (severity.color != null) {
                            val parsedColor = parseHexColor(severity.color) ?: MaterialTheme.colorScheme.primary
                            Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(parsedColor))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                severity.color,
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
                        IconButton(onClick = { onEdit(severity) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(Res.string.action_edit),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDelete(severity) }) {
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

@Composable
private fun AlertSeverityFormDialog(
    formMode: AlertSeverityFormMode,
    isSubmitting: Boolean,
    submitError: String?,
    onSubmit: (name: String, level: Short, description: String?, color: String?, requiresAction: Boolean, notificationDelayMinutes: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val isEditMode = formMode is AlertSeverityFormMode.Edit
    val existing = (formMode as? AlertSeverityFormMode.Edit)?.severity

    var name by remember(formMode) { mutableStateOf(existing?.name ?: "") }
    var level by remember(formMode) { mutableStateOf(existing?.level?.toString() ?: "") }
    var color by remember(formMode) { mutableStateOf(existing?.color ?: "") }
    var requiresAction by remember(formMode) { mutableStateOf(existing?.requiresAction ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = stringResource(if (isEditMode) Res.string.dialog_edit_alert_severity else Res.string.dialog_create_alert_severity),
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
                    value = level,
                    onValueChange = { newValue ->
                        // Only allow digits
                        level = newValue.filter { it.isDigit() }
                    },
                    label = { Text(stringResource(Res.string.field_level)) },
                    placeholder = { Text("1, 2, 3...") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = level.isNotBlank() && level.toShortOrNull() == null,
                    supportingText = if (level.isNotBlank() && level.toShortOrNull() == null) {
                        { Text(stringResource(Res.string.error_level_invalid)) }
                    } else null
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text(stringResource(Res.string.field_color)) },
                    placeholder = { Text("#FF0000") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = requiresAction, onCheckedChange = { requiresAction = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.field_requires_action))
                }
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
                    val levelValue = level.toShortOrNull() ?: 0
                    onSubmit(
                        name,
                        levelValue,
                        null,
                        color.ifBlank { null },
                        requiresAction,
                        0
                    )
                },
                enabled = !isSubmitting && name.isNotBlank() && level.toShortOrNull() != null
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

/**
 * Parses a hex color string to a Compose Color.
 * Supports formats: #RGB, #RRGGBB, #AARRGGBB
 */
private fun parseHexColor(hexColor: String): Color? {
    return try {
        val hex = hexColor.trimStart('#')
        when (hex.length) {
            3 -> {
                // #RGB -> #RRGGBB
                val r = hex[0].toString().repeat(2).toInt(16)
                val g = hex[1].toString().repeat(2).toInt(16)
                val b = hex[2].toString().repeat(2).toInt(16)
                Color(r, g, b)
            }

            6 -> {
                // #RRGGBB
                val r = hex.substring(0, 2).toInt(16)
                val g = hex.substring(2, 4).toInt(16)
                val b = hex.substring(4, 6).toInt(16)
                Color(r, g, b)
            }

            8 -> {
                // #AARRGGBB
                val a = hex.substring(0, 2).toInt(16)
                val r = hex.substring(2, 4).toInt(16)
                val g = hex.substring(4, 6).toInt(16)
                val b = hex.substring(6, 8).toInt(16)
                Color(r, g, b, a)
            }

            else -> null
        }
    } catch (e: Exception) {
        null
    }
}
