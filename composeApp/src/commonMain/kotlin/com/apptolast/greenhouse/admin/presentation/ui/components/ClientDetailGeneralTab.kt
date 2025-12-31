package com.apptolast.greenhouse.admin.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.apptolast.greenhouse.admin.data.model.Client

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
