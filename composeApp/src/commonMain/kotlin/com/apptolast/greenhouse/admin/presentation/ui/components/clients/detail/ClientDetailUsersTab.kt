package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.User
import com.apptolast.greenhouse.admin.data.model.UserRole
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.ErrorContent
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.new_user
import greenhouseadmin.composeapp.generated.resources.users_empty
import greenhouseadmin.composeapp.generated.resources.users_subtitle
import greenhouseadmin.composeapp.generated.resources.users_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Users tab content for the client detail screen.
 * Displays a table of users with add/edit/delete functionality.
 * On compact screens, the add button is hidden (FAB is shown by parent).
 */
@Composable
fun ClientDetailUsersTab(
    users: List<User>,
    isLoading: Boolean = false,
    error: String? = null,
    onAddUser: () -> Unit = {},
    onEditUser: (User) -> Unit = {},
    onDeleteUser: (User) -> Unit = {},
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Header with title and Add button (button hidden on compact - FAB shown by parent)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(Res.string.users_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(Res.string.users_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Show button only on expanded screens
            if (!windowInfo.isCompact) {
                Button(
                    onClick = onAddUser,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.new_user))
                }
            }
        }

        // Content
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                ErrorContent(
                    message = error,
                    onRetry = onRetry
                )
            }

            users.isEmpty() -> {
                EmptyUsersContent()
            }

            else -> {
                UsersTableOrCards(
                    users = users,
                    onEditUser = onEditUser,
                    onDeleteUser = onDeleteUser
                )
            }
        }
    }
}

@Composable
private fun EmptyUsersContent(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.users_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private object ClientDetailUsersTabPreviewData {
    val sampleUsers = listOf(
        User(
            id = 1L,
            code = "USR-00001",
            username = "anamartinez",
            email = "ana@freshveg.com",
            role = UserRole.ADMIN,
            tenantId = 1L,
            isActive = true
        ),
        User(
            id = 2L,
            code = "USR-00002",
            username = "carlosruiz",
            email = "carlos@freshveg.com",
            role = UserRole.OPERATOR,
            tenantId = 1L,
            isActive = true
        )
    )
}

@Preview
@Composable
private fun ClientDetailUsersTabPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailUsersTab(users = ClientDetailUsersTabPreviewData.sampleUsers)
        }
    }
}

@Preview
@Composable
private fun ClientDetailUsersTabEmptyPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailUsersTab(users = emptyList())
        }
    }
}

@Preview
@Composable
private fun ClientDetailUsersTabLoadingPreview() {
    GreenhouseAdminTheme {
        ProvideAppWindowInfo {
            ClientDetailUsersTab(users = emptyList(), isLoading = true)
        }
    }
}
