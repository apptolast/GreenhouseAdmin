package com.apptolast.greenhouse.admin.presentation.ui.components.clients.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Card-based list of clients for compact (mobile) screens.
 * Shows essential client info in a vertically scrolling list of cards.
 */
@Composable
fun ClientsCards(
    clients: List<Client>,
    onClientClicked: (Client) -> Unit = {},
    onEditClient: (Client) -> Unit = {},
    onDeleteClient: (Client) -> Unit = {},
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
 * Single client card displaying essential information.
 */
@Composable
private fun ClientCard(
    client: Client,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row: Avatar, Name, Status, Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                ClientAvatar(
                    initials = client.initials,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Name and Status
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = client.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ClientStatusBadge(status = client.status)
                }

                // Actions
                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(Res.string.action_edit),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.action_delete),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contact info rows
            ClientInfoRow(
                icon = Icons.Default.Email,
                text = client.email
            )

            Spacer(modifier = Modifier.height(8.dp))

            ClientInfoRow(
                icon = Icons.Default.Phone,
                text = client.phone
            )

            Spacer(modifier = Modifier.height(8.dp))

            ClientInfoRow(
                icon = Icons.Default.LocationOn,
                text = client.province
            )
        }
    }
}

@Composable
private fun ClientInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private object ClientsCardsPreviewData {
    val sampleClients = listOf(
        Client(
            id = "1",
            name = "Elena Rodriguez",
            email = "elena@freshveg.com",
            phone = "+34 612 345 678",
            province = "Almeria",
            country = "Spain",
            status = ClientStatus.ACTIVE
        ),
        Client(
            id = "2",
            name = "Juan Garcia",
            email = "juan@greenfields.es",
            phone = "+34 623 456 789",
            province = "Murcia",
            country = "Spain",
            status = ClientStatus.PENDING
        ),
        Client(
            id = "3",
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
private fun ClientsCardsPreview() {
    GreenhouseAdminTheme {
        ClientsCards(clients = ClientsCardsPreviewData.sampleClients)
    }
}

@Preview
@Composable
private fun ClientCardPreview() {
    GreenhouseAdminTheme {
        ClientCard(
            client = ClientsCardsPreviewData.sampleClients.first(),
            onClick = {},
            onEdit = {},
            onDelete = {}
        )
    }
}
