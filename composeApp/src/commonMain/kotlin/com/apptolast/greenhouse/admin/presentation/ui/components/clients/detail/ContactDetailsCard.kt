package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.card_contact_details
import greenhouseadmin.composeapp.generated.resources.label_coordinates
import greenhouseadmin.composeapp.generated.resources.label_location
import greenhouseadmin.composeapp.generated.resources.label_phone_number
import greenhouseadmin.composeapp.generated.resources.label_primary_email
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Card component displaying client contact details.
 */
@Composable
fun ContactDetailsCard(
    client: Client,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.card_contact_details),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Email and Phone row
            Row(modifier = Modifier.fillMaxWidth()) {
                // Email
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.label_primary_email),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = client.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Phone
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.label_phone_number),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = client.phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Location and Coordinates row
            Row(modifier = Modifier.fillMaxWidth()) {
                // Location (Province, Country)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.label_location),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = client.fullLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Coordinates (if available)
                if (client.location?.isValid == true) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.label_coordinates),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = client.location.displayString,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

private object ContactDetailsCardPreviewData {
    val sampleClient = Client(
        id = "1",
        name = "Elena Rodriguez",
        email = "elena@freshveg.com",
        phone = "+34 612 345 678",
        province = "Almeria",
        country = "Spain",
        status = ClientStatus.ACTIVE
    )
}

@Preview
@Composable
private fun ContactDetailsCardPreview() {
    GreenhouseAdminTheme {
        ContactDetailsCard(client = ContactDetailsCardPreviewData.sampleClient)
    }
}
