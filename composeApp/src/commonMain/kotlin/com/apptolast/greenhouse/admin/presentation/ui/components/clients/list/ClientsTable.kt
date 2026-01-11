package com.apptolast.greenhouse.admin.presentation.ui.components.clients.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
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
import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_email
import greenhouseadmin.composeapp.generated.resources.header_location
import greenhouseadmin.composeapp.generated.resources.header_name
import greenhouseadmin.composeapp.generated.resources.header_phone
import greenhouseadmin.composeapp.generated.resources.header_province
import greenhouseadmin.composeapp.generated.resources.header_status
import greenhouseadmin.composeapp.generated.resources.status_active
import greenhouseadmin.composeapp.generated.resources.status_inactive
import greenhouseadmin.composeapp.generated.resources.status_pending
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Adaptive component that shows a table on larger screens and cards on compact screens.
 */
@Composable
fun ClientsTableOrCards(
    clients: List<Client>,
    onClientClicked: (Client) -> Unit = {},
    onEditClient: (Client) -> Unit = {},
    onDeleteClient: (Client) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    if (windowInfo.isCompact) {
        ClientsCardList(
            clients = clients,
            onClientClicked = onClientClicked,
            onEditClient = onEditClient,
            onDeleteClient = onDeleteClient,
            modifier = modifier
        )
    } else {
        ClientsTable(
            clients = clients,
            onClientClicked = onClientClicked,
            onEditClient = onEditClient,
            onDeleteClient = onDeleteClient,
            modifier = modifier
        )
    }
}

/**
 * Table displaying list of clients with headers and rows.
 */
@Composable
fun ClientsTable(
    clients: List<Client>,
    onClientClicked: (Client) -> Unit = {},
    onEditClient: (Client) -> Unit = {},
    onDeleteClient: (Client) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Header row
            ClientTableHeader()

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            // Client rows
            LazyColumn {
                items(
                    items = clients,
                    key = { it.id }
                ) { client ->
                    ClientTableRow(
                        client = client,
                        onClick = { onClientClicked(client) },
                        onEdit = { onEditClient(client) },
                        onDelete = { onDeleteClient(client) }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientTableHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // NAME column
        Text(
            text = stringResource(Res.string.header_name),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.2f)
        )

        // EMAIL column
        Text(
            text = stringResource(Res.string.header_email),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.5f)
        )

        // PHONE column
        Text(
            text = stringResource(Res.string.header_phone),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        // PROVINCE column
        Text(
            text = stringResource(Res.string.header_province),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        // LOCATION column
        Text(
            text = stringResource(Res.string.header_location),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        // STATUS column
        Text(
            text = stringResource(Res.string.header_status),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f),
            textAlign = TextAlign.Center
        )

        // ACTIONS column
        Text(
            text = stringResource(Res.string.header_actions),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ClientTableRow(
    client: Client,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // NAME column with avatar
        Row(
            modifier = Modifier.weight(1.2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClientAvatar(
                initials = client.initials,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = client.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // EMAIL column
        Text(
            text = client.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // PHONE column
        Text(
            text = client.phone,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // PROVINCE column
        Text(
            text = client.province,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // LOCATION column
        Text(
            text = "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // STATUS column
        Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.Center
        ) {
            ClientStatusBadge(status = client.status)
        }

        // ACTIONS column
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
 * Avatar component for displaying client initials with color-coded background.
 */
@Composable
fun ClientAvatar(
    initials: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (initials.firstOrNull()) {
        'E' -> Color(0xFF4CAF50)
        'J' -> Color(0xFF2196F3)
        'T' -> Color(0xFF9C27B0)
        'M' -> Color(0xFFFF9800)
        'A' -> Color(0xFFE91E63)
        'S' -> Color(0xFF00BCD4)
        'C' -> Color(0xFF795548)
        'D' -> Color(0xFF607D8B)
        'Y' -> Color(0xFFFFEB3B)
        else -> Color(0xFF9E9E9E)
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
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

/**
 * Badge component for displaying client status with colored indicator.
 */
@Composable
fun ClientStatusBadge(
    status: ClientStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, text) = when (status) {
        ClientStatus.ACTIVE -> Triple(
            Color(0xFF1B5E20).copy(alpha = 0.2f),
            Color(0xFF4CAF50),
            stringResource(Res.string.status_active)
        )

        ClientStatus.PENDING -> Triple(
            Color(0xFFE65100).copy(alpha = 0.2f),
            Color(0xFFFF9800),
            stringResource(Res.string.status_pending)
        )

        ClientStatus.INACTIVE -> Triple(
            Color(0xFF424242).copy(alpha = 0.2f),
            Color(0xFF9E9E9E),
            stringResource(Res.string.status_inactive)
        )
    }

    Row(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(textColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

/**
 * Card list for displaying clients on compact screens.
 */
@Composable
private fun ClientsCardList(
    clients: List<Client>,
    onClientClicked: (Client) -> Unit,
    onEditClient: (Client) -> Unit,
    onDeleteClient: (Client) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = clients,
            key = { it.id }
        ) { client ->
            ClientCard(
                client = client,
                onClick = { onClientClicked(client) },
                onEdit = { onEditClient(client) },
                onDelete = { onDeleteClient(client) }
            )
        }
    }
}

/**
 * Individual client card for compact screens.
 * Entire card is clickable to navigate to detail view.
 * Dropdown menu provides Edit/Delete actions.
 */
@Composable
private fun ClientCard(
    client: Client,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row: Avatar, Name, Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClientAvatar(
                    initials = client.initials,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = client.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = client.province,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

            Spacer(modifier = Modifier.height(12.dp))

            // Email row
            Text(
                text = client.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status badge
            ClientStatusBadge(status = client.status)
        }
    }
}

private object ClientsTablePreviewData {
    val sampleClients = listOf(
        Client(
            id = 1L,
            code = "TNT-00001",
            name = "Elena Rodriguez",
            email = "elena@freshveg.com",
            phone = "+34 612 345 678",
            province = "Almeria",
            country = "Spain",
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = 2L,
            code = "TNT-00002",
            name = "Juan Garcia",
            email = "juan@greenfields.es",
            phone = "+34 623 456 789",
            province = "Murcia",
            country = "Spain",
            status = ClientStatus.PENDING
        ),
        Client(
            id = 3L,
            code = "TNT-00003",
            name = "Maria Lopez",
            email = "maria@organicfarm.com",
            phone = "+34 634 567 890",
            province = "Valencia",
            country = "Spain",
            status = ClientStatus.INACTIVE
        )
    )
}

@Preview
@Composable
private fun ClientsTablePreview() {
    GreenhouseAdminTheme {
        ClientsTable(clients = ClientsTablePreviewData.sampleClients)
    }
}

@Preview
@Composable
private fun ClientAvatarPreview() {
    GreenhouseAdminTheme {
        ClientAvatar(initials = "ER")
    }
}

@Preview
@Composable
private fun ClientStatusBadgeActivePreview() {
    GreenhouseAdminTheme {
        ClientStatusBadge(status = ClientStatus.ACTIVE)
    }
}

@Preview
@Composable
private fun ClientStatusBadgePendingPreview() {
    GreenhouseAdminTheme {
        ClientStatusBadge(status = ClientStatus.PENDING)
    }
}

@Preview
@Composable
private fun ClientStatusBadgeInactivePreview() {
    GreenhouseAdminTheme {
        ClientStatusBadge(status = ClientStatus.INACTIVE)
    }
}
