package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.apptolast.greenhouse.admin.data.model.Greenhouse
import com.apptolast.greenhouse.admin.data.model.Sector
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.CopyableIdCell
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_greenhouse
import greenhouseadmin.composeapp.generated.resources.header_id
import greenhouseadmin.composeapp.generated.resources.header_sector_name
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Enum representing sortable columns in the Sectors table.
 */
private enum class SectorSortColumn {
    ID, NAME
}

/**
 * Adaptive component that shows a table on larger screens and cards on compact screens.
 */
@Composable
fun SectorsTableOrCards(
    sectors: List<Sector>,
    greenhouses: List<Greenhouse> = emptyList(),
    onEditSector: (Sector) -> Unit = {},
    onDeleteSector: (Sector) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    if (windowInfo.isCompact) {
        SectorsCardList(
            sectors = sectors,
            greenhouses = greenhouses,
            onEditSector = onEditSector,
            onDeleteSector = onDeleteSector,
            onCopyId = onCopyId,
            modifier = modifier
        )
    } else {
        SectorsTable(
            sectors = sectors,
            greenhouses = greenhouses,
            onEditSector = onEditSector,
            onDeleteSector = onDeleteSector,
            onCopyId = onCopyId,
            modifier = modifier
        )
    }
}

/**
 * Table displaying list of sectors with headers and rows.
 * Columns: ID | NAME | GREENHOUSE | ACTIONS
 * Sortable columns: ID, NAME
 */
@Composable
fun SectorsTable(
    sectors: List<Sector>,
    greenhouses: List<Greenhouse> = emptyList(),
    onEditSector: (Sector) -> Unit = {},
    onDeleteSector: (Sector) -> Unit = {},
    onCopyId: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var sortColumn by remember { mutableStateOf<SectorSortColumn?>(null) }
    var sortDirection by remember { mutableStateOf(SortDirection.ASCENDING) }

    // Create a map for quick greenhouse name lookup
    val greenhouseNameMap = greenhouses.associateBy({ it.id }, { it.name })

    // Sort sectors based on selected column and direction
    val sortedSectors = remember(sectors, sortColumn, sortDirection) {
        when (sortColumn) {
            SectorSortColumn.ID -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    sectors.sortedBy { it.code }
                } else {
                    sectors.sortedByDescending { it.code }
                }
            }

            SectorSortColumn.NAME -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    sectors.sortedBy { it.displayName.lowercase() }
                } else {
                    sectors.sortedByDescending { it.displayName.lowercase() }
                }
            }

            null -> sectors
        }
    }

    fun onHeaderClick(column: SectorSortColumn) {
        if (sortColumn == column) {
            sortDirection = sortDirection.toggle()
        } else {
            sortColumn = column
            sortDirection = SortDirection.ASCENDING
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Header row with sorting
            SectorsTableHeader(
                sortColumn = sortColumn,
                sortDirection = sortDirection,
                onSortClick = ::onHeaderClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Sector rows
            sortedSectors.forEach { sector ->
                SectorTableRow(
                    sector = sector,
                    greenhouseName = greenhouseNameMap[sector.greenhouseId],
                    onEdit = { onEditSector(sector) },
                    onDelete = { onDeleteSector(sector) },
                    onCopyId = { onCopyId(sector.code) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun SectorsTableHeader(
    sortColumn: SectorSortColumn?,
    sortDirection: SortDirection,
    onSortClick: (SectorSortColumn) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // ID column - Sortable
        SectorSortableHeader(
            text = stringResource(Res.string.header_id),
            column = SectorSortColumn.ID,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(SectorSortColumn.ID) },
            modifier = Modifier.weight(0.8f)
        )
        // NAME column - Sortable
        SectorSortableHeader(
            text = stringResource(Res.string.header_sector_name),
            column = SectorSortColumn.NAME,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(SectorSortColumn.NAME) },
            modifier = Modifier.weight(1.2f)
        )
        // GREENHOUSE column - Not sortable
        Text(
            text = stringResource(Res.string.header_greenhouse),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.2f)
        )
        // ACTIONS column - Not sortable
        Text(
            text = stringResource(Res.string.header_actions),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Sortable header cell with sort icon for Sectors table.
 */
@Composable
private fun SectorSortableHeader(
    text: String,
    column: SectorSortColumn,
    currentSortColumn: SectorSortColumn?,
    sortDirection: SortDirection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = currentSortColumn == column

    Row(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.width(4.dp))
        if (isActive) {
            Icon(
                imageVector = if (sortDirection == SortDirection.ASCENDING) {
                    Icons.Outlined.ArrowUpward
                } else {
                    Icons.Outlined.ArrowDownward
                },
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            // Show a subtle indicator that this column is sortable
            Icon(
                imageVector = Icons.Outlined.ArrowUpward,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun SectorTableRow(
    sector: Sector,
    greenhouseName: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyId: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // ID - Copyable code
        CopyableIdCell(
            id = sector.code,
            onCopyId = { onCopyId() },
            modifier = Modifier.weight(0.8f)
        )

        // NAME
        Text(
            text = sector.displayName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.2f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // GREENHOUSE
        Text(
            text = greenhouseName ?: sector.greenhouseId.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.2f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // ACTIONS
        Row(
            modifier = Modifier.width(80.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.action_edit),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.action_delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Card list for displaying sectors on compact screens.
 */
@Composable
private fun SectorsCardList(
    sectors: List<Sector>,
    greenhouses: List<Greenhouse>,
    onEditSector: (Sector) -> Unit,
    onDeleteSector: (Sector) -> Unit,
    onCopyId: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val greenhouseNameMap = greenhouses.associateBy({ it.id }, { it.name })

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        sectors.forEach { sector ->
            SectorCard(
                sector = sector,
                greenhouseName = greenhouseNameMap[sector.greenhouseId],
                onEdit = { onEditSector(sector) },
                onDelete = { onDeleteSector(sector) },
                onCopyId = { onCopyId(sector.code) }
            )
        }
    }
}

/**
 * Individual sector card for compact screens with dropdown menu for actions.
 */
@Composable
private fun SectorCard(
    sector: Sector,
    greenhouseName: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyId: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row: Avatar, Name/ID, Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectorAvatar(
                    initials = sector.initial,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sector.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = sector.code,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = greenhouseName ?: sector.greenhouseId.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(Res.string.header_actions),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.action_edit)) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.action_delete)) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Avatar component for sectors with colored background based on initials.
 */
@Composable
fun SectorAvatar(
    initials: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFF2196F3)), // Blue for sectors
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

private object SectorsTablePreviewData {
    val sampleSectors = listOf(
        Sector(
            id = 1L,
            code = "SEC-00001",
            tenantId = 1L,
            greenhouseId = 1L,
            name = "Tomate Cherry"
        ),
        Sector(
            id = 2L,
            code = "SEC-00002",
            tenantId = 1L,
            greenhouseId = 1L,
            name = "Pimiento Rojo"
        ),
        Sector(
            id = 3L,
            code = "SEC-00003",
            tenantId = 1L,
            greenhouseId = 2L,
            name = "Pepino"
        )
    )

    val sampleGreenhouses = listOf(
        Greenhouse(
            id = 1L,
            code = "GRH-00001",
            name = "Invernadero Principal",
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
            name = "Invernadero Norte",
            tenantId = 1L,
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
private fun SectorsTablePreview() {
    GreenhouseAdminTheme {
        SectorsTable(
            sectors = SectorsTablePreviewData.sampleSectors,
            greenhouses = SectorsTablePreviewData.sampleGreenhouses
        )
    }
}

@Preview
@Composable
private fun SectorAvatarPreview() {
    GreenhouseAdminTheme {
        SectorAvatar(initials = "T")
    }
}
