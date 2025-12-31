package com.apptolast.greenhouse.admin.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.ClientStatusFilter
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.filter_all
import greenhouseadmin.composeapp.generated.resources.filter_location
import greenhouseadmin.composeapp.generated.resources.filter_status
import greenhouseadmin.composeapp.generated.resources.new_client
import greenhouseadmin.composeapp.generated.resources.search_clients_placeholder
import greenhouseadmin.composeapp.generated.resources.status_active
import greenhouseadmin.composeapp.generated.resources.status_inactive
import greenhouseadmin.composeapp.generated.resources.status_pending
import org.jetbrains.compose.resources.stringResource

/**
 * Filter bar for the clients list with search, status filter, location filter, and new client button.
 */
@Composable
fun ClientsFilters(
    searchQuery: String,
    statusFilter: ClientStatusFilter,
    locationFilter: String?,
    locations: List<String>,
    onSearchQueryChanged: (String) -> Unit,
    onStatusFilterChanged: (ClientStatusFilter) -> Unit,
    onLocationFilterChanged: (String?) -> Unit,
    onNewClientClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            placeholder = {
                Text(
                    text = stringResource(Res.string.search_clients_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Status filter dropdown
        StatusFilterDropdown(
            selectedFilter = statusFilter,
            onFilterSelected = onStatusFilterChanged
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Location filter dropdown
        LocationFilterDropdown(
            selectedLocation = locationFilter,
            locations = locations,
            onLocationSelected = onLocationFilterChanged
        )

        Spacer(modifier = Modifier.width(16.dp))

        // New Client button
        Button(
            onClick = onNewClientClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.height(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.new_client),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun StatusFilterDropdown(
    selectedFilter: ClientStatusFilter,
    onFilterSelected: (ClientStatusFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val filterText = when (selectedFilter) {
        ClientStatusFilter.ALL -> stringResource(Res.string.filter_status) + ": " + stringResource(Res.string.filter_all)
        ClientStatusFilter.ACTIVE -> stringResource(Res.string.filter_status) + ": " + stringResource(Res.string.status_active)
        ClientStatusFilter.PENDING -> stringResource(Res.string.filter_status) + ": " + stringResource(Res.string.status_pending)
        ClientStatusFilter.INACTIVE -> stringResource(Res.string.filter_status) + ": " + stringResource(Res.string.status_inactive)
    }

    OutlinedButton(
        onClick = { expanded = true },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            text = filterText,
            style = MaterialTheme.typography.bodyMedium
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
        ClientStatusFilter.entries.forEach { filter ->
            val text = when (filter) {
                ClientStatusFilter.ALL -> stringResource(Res.string.filter_all)
                ClientStatusFilter.ACTIVE -> stringResource(Res.string.status_active)
                ClientStatusFilter.PENDING -> stringResource(Res.string.status_pending)
                ClientStatusFilter.INACTIVE -> stringResource(Res.string.status_inactive)
            }
            DropdownMenuItem(
                text = { Text(text) },
                onClick = {
                    onFilterSelected(filter)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun LocationFilterDropdown(
    selectedLocation: String?,
    locations: List<String>,
    onLocationSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val displayText = selectedLocation
        ?: (stringResource(Res.string.filter_location))

    OutlinedButton(
        onClick = { expanded = true },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyMedium
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
        // "All" option
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.filter_all)) },
            onClick = {
                onLocationSelected(null)
                expanded = false
            }
        )

        // Location options
        locations.forEach { location ->
            DropdownMenuItem(
                text = { Text(location) },
                onClick = {
                    onLocationSelected(location)
                    expanded = false
                }
            )
        }
    }
}
