package com.apptolast.greenhouse.admin.presentation.ui.screens

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import com.apptolast.greenhouse.admin.presentation.viewmodel.SettingsEvent
import com.apptolast.greenhouse.admin.presentation.viewmodel.SettingsUiState
import com.apptolast.greenhouse.admin.presentation.viewmodel.SettingsViewModel
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.button_cancel
import greenhouseadmin.composeapp.generated.resources.logout_button
import greenhouseadmin.composeapp.generated.resources.logout_confirm_button
import greenhouseadmin.composeapp.generated.resources.logout_confirmation_message
import greenhouseadmin.composeapp.generated.resources.logout_confirmation_title
import greenhouseadmin.composeapp.generated.resources.settings_account_section
import greenhouseadmin.composeapp.generated.resources.settings_logged_in_as
import greenhouseadmin.composeapp.generated.resources.settings_role
import greenhouseadmin.composeapp.generated.resources.settings_screen_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

/**
 * Settings screen with logout functionality.
 * Responsive design for all screen sizes.
 */
@Composable
fun SettingsScreen(
    onLogoutSuccess: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle logout navigation
    LaunchedEffect(uiState.isLogoutSuccessful) {
        if (uiState.isLogoutSuccessful) {
            onLogoutSuccess()
            viewModel.onEvent(SettingsEvent.OnLogoutComplete)
        }
    }

    SettingsScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit = {}
) {
    val windowInfo = LocalAppWindowInfo.current
    val scrollState = rememberScrollState()

    // Logout confirmation dialog
    if (uiState.showLogoutConfirmation) {
        LogoutConfirmationDialog(
            onConfirm = { onEvent(SettingsEvent.OnConfirmLogout) },
            onDismiss = { onEvent(SettingsEvent.OnCancelLogout) }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
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
                // Header
                Text(
                    text = stringResource(Res.string.settings_screen_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Account Section Card
                AccountSectionCard(
                    username = uiState.username,
                    roles = uiState.roles
                )

                Spacer(modifier = Modifier.weight(1f))

                // Logout Button at the bottom
                LogoutButton(
                    isLoading = uiState.isLoggingOut,
                    onClick = { onEvent(SettingsEvent.OnLogoutClicked) },
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
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
                        Spacer(modifier = Modifier.height(8.dp))

                        // Role label
                        Text(
                            text = stringResource(Res.string.settings_role),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

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

@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = stringResource(Res.string.logout_confirmation_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.logout_confirmation_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(Res.string.logout_confirm_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(Res.string.button_cancel))
            }
        }
    )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            SettingsScreenContent(
                uiState = SettingsUiState(
                    username = "admin@greenhouse.com",
                    roles = listOf("ROLE_ADMIN")
                )
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenLoadingPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            SettingsScreenContent(
                uiState = SettingsUiState(
                    username = "admin@greenhouse.com",
                    roles = listOf("ROLE_ADMIN"),
                    isLoggingOut = true
                )
            )
        }
    }
}

@Preview
@Composable
private fun LogoutConfirmationDialogPreview() {
    GreenhouseAdminTheme {
        LogoutConfirmationDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}
