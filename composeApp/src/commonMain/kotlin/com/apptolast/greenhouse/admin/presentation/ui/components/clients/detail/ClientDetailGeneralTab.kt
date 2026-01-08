package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.apptolast.greenhouse.admin.data.model.Client
import com.apptolast.greenhouse.admin.data.model.ClientStatus
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * General tab content for the client detail screen.
 * Displays contact details and other general information.
 */
@Composable
fun ClientDetailGeneralTab(
    client: Client,
    modifier: Modifier = Modifier
) {
    ContactDetailsCard(
        client = client,
        modifier = modifier.fillMaxWidth()
    )
}

private object GeneralTabPreviewData {
    val sampleClient = Client(
        id = 1L,
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
private fun ClientDetailGeneralTabPreview() {
    GreenhouseAdminTheme {
        ClientDetailGeneralTab(client = GeneralTabPreviewData.sampleClient)
    }
}
