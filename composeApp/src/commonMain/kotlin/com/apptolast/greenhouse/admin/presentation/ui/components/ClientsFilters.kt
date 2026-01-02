package com.apptolast.greenhouse.admin.presentation.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.SmallFloatingActionButton
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
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.filter_all
import greenhouseadmin.composeapp.generated.resources.filter_province
import greenhouseadmin.composeapp.generated.resources.filter_status
import greenhouseadmin.composeapp.generated.resources.new_client
import greenhouseadmin.composeapp.generated.resources.search_clients_placeholder
import greenhouseadmin.composeapp.generated.resources.status_active
import greenhouseadmin.composeapp.generated.resources.status_inactive
import greenhouseadmin.composeapp.generated.resources.status_pending
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Filter bar for the clients list with search, status filter, province filter, and new client button.
 * Adapts layout based on screen size:
 * - Compact: Vertical layout with search on top, horizontally scrollable filters below
 * - Expanded: Horizontal layout with all elements in one row
 */
@Composable
fun ClientsFilters(
    searchQuery: String,
    statusFilter: ClientStatusFilter,
    provinceFilter: String?,
    provinces: List<String>,
    onSearchQueryChanged: (String) -> Unit = {},
    onStatusFilterChanged: (ClientStatusFilter) -> Unit = {},
    onProvinceFilterChanged: (String?) -> Unit = {},
    onNewClientClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    if (windowInfo.isCompact) {
        CompactFilters(
            searchQuery = searchQuery,
            statusFilter = statusFilter,
            provinceFilter = provinceFilter,
            provinces = provinces,
            onSearchQueryChanged = onSearchQueryChanged,
            onStatusFilterChanged = onStatusFilterChanged,
            onProvinceFilterChanged = onProvinceFilterChanged,
            onNewClientClicked = onNewClientClicked,
            modifier = modifier
        )
    } else {
        ExpandedFilters(
            searchQuery = searchQuery,
            statusFilter = statusFilter,
            provinceFilter = provinceFilter,
            provinces = provinces,
            onSearchQueryChanged = onSearchQueryChanged,
            onStatusFilterChanged = onStatusFilterChanged,
            onProvinceFilterChanged = onProvinceFilterChanged,
            onNewClientClicked = onNewClientClicked,
            modifier = modifier
        )
    }
}

/**
 * Compact layout for mobile: vertical with search on top, scrollable filters row below.
 */
@Composable
private fun CompactFilters(
    searchQuery: String,
    statusFilter: ClientStatusFilter,
    provinceFilter: String?,
    provinces: List<String>,
    onSearchQueryChanged: (String) -> Unit,
    onStatusFilterChanged: (ClientStatusFilter) -> Unit,
    onProvinceFilterChanged: (String?) -> Unit,
    onNewClientClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search field with add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Spacer(modifier = Modifier.width(12.dp))

            // Compact add button (FAB style)
            SmallFloatingActionButton(
                onClick = onNewClientClicked,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.new_client)
                )
            }
        }

        // Horizontally scrollable filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusFilterDropdown(
                selectedFilter = statusFilter,
                onFilterSelected = onStatusFilterChanged,
                compact = true
            )

            ProvinceFilterDropdown(
                selectedProvince = provinceFilter,
                provinces = provinces,
                onProvinceSelected = onProvinceFilterChanged,
                compact = true
            )
        }
    }
}

/**
 * Expanded layout for desktop: all elements in one horizontal row.
 */
@Composable
private fun ExpandedFilters(
    searchQuery: String,
    statusFilter: ClientStatusFilter,
    provinceFilter: String?,
    provinces: List<String>,
    onSearchQueryChanged: (String) -> Unit,
    onStatusFilterChanged: (ClientStatusFilter) -> Unit,
    onProvinceFilterChanged: (String?) -> Unit,
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

        // Province filter dropdown
        ProvinceFilterDropdown(
            selectedProvince = provinceFilter,
            provinces = provinces,
            onProvinceSelected = onProvinceFilterChanged
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
    modifier: Modifier = Modifier,
    compact: Boolean = false
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
private fun ProvinceFilterDropdown(
    selectedProvince: String?,
    provinces: List<String>,
    onProvinceSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val displayText = selectedProvince
        ?: stringResource(Res.string.filter_province)

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
                onProvinceSelected(null)
                expanded = false
            }
        )

        // Province options
        provinces.forEach { province ->
            DropdownMenuItem(
                text = { Text(province) },
                onClick = {
                    onProvinceSelected(province)
                    expanded = false
                }
            )
        }
    }
}

@Preview
@Composable
private fun ClientsFiltersPreview() {
    GreenhouseAdminTheme {
        ClientsFilters(
            searchQuery = "",
            statusFilter = ClientStatusFilter.ALL,
            provinceFilter = null,
            provinces = listOf("Almeria", "Murcia", "Valencia", "Granada")
        )
    }
}

@Preview
@Composable
private fun ClientsFiltersWithSearchPreview() {
    GreenhouseAdminTheme {
        ClientsFilters(
            searchQuery = "Elena",
            statusFilter = ClientStatusFilter.ACTIVE,
            provinceFilter = "Almeria",
            provinces = listOf("Almeria", "Murcia", "Valencia", "Granada")
        )
    }
}
