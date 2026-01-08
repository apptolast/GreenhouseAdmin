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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.logout_button
import greenhouseadmin.composeapp.generated.resources.settings_account_section
import greenhouseadmin.composeapp.generated.resources.settings_logged_in_as
import greenhouseadmin.composeapp.generated.resources.settings_role
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Account tab content for the Settings screen.
 * Displays user info and logout functionality.
 */
@Composable
fun SettingsAccountTab(
    username: String,
    roles: List<String>,
    isLoggingOut: Boolean,
    onLogoutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current
    val scrollState = rememberScrollState()

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
        // Content container with max width for large screens
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Account Section Card
            AccountSectionCard(
                username = username,
                roles = roles
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button at the bottom
            LogoutButton(
                isLoading = isLoggingOut,
                onClick = onLogoutClicked,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }
    }
}

@Composable
private fun AccountSectionCard(
    username: String,
    roles: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Section title
            Text(
                text = stringResource(Res.string.settings_account_section),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(16.dp))

            // User info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Username label
                    Text(
                        text = stringResource(Res.string.settings_logged_in_as),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Username value
                    Text(
                        text = username.ifEmpty { "-" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )

                    if (roles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Role label
                            Text(
                                text = stringResource(Res.string.settings_role),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            // Roles
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                roles.forEach { role ->
                                    RoleChip(role = role)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleChip(role: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = role.removePrefix("ROLE_"),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LogoutButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    OutlinedButton(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier
            .then(
                if (windowInfo.isCompact) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier.widthIn(min = 280.dp)
                }
            )
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFE53935) // Red color for logout
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE53935).copy(alpha = 0.5f))
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color(0xFFE53935),
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(Res.string.logout_button),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
private fun SettingsAccountTabPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            SettingsAccountTab(
                username = "admin@greenhouse.com",
                roles = listOf("ROLE_ADMIN"),
                isLoggingOut = false,
                onLogoutClicked = {}
            )
        }
    }
}

@Preview
@Composable
private fun SettingsAccountTabLoadingPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            SettingsAccountTab(
                username = "admin@greenhouse.com",
                roles = listOf("ROLE_ADMIN"),
                isLoggingOut = true,
                onLogoutClicked = {}
            )
        }
    }
}
