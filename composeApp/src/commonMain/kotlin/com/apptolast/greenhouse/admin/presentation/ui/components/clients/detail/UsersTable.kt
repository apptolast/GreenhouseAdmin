package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
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
import com.apptolast.greenhouse.admin.data.model.User
import com.apptolast.greenhouse.admin.data.model.UserRole
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.StatusChip
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.action_delete
import greenhouseadmin.composeapp.generated.resources.action_edit
import greenhouseadmin.composeapp.generated.resources.header_actions
import greenhouseadmin.composeapp.generated.resources.header_email
import greenhouseadmin.composeapp.generated.resources.header_role
import greenhouseadmin.composeapp.generated.resources.header_status
import greenhouseadmin.composeapp.generated.resources.header_username
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Adaptive component that shows a table on larger screens and cards on compact screens.
 */
@Composable
fun UsersTableOrCards(
    users: List<User>,
    onEditUser: (User) -> Unit = {},
    onDeleteUser: (User) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current

    if (windowInfo.isCompact) {
        UsersCardList(
            users = users,
            onEditUser = onEditUser,
            onDeleteUser = onDeleteUser,
            modifier = modifier
        )
    } else {
        UsersTable(
            users = users,
            onEditUser = onEditUser,
            onDeleteUser = onDeleteUser,
            modifier = modifier
        )
    }
}

/**
 * Table displaying list of users with headers and rows.
 */
@Composable
fun UsersTable(
    users: List<User>,
    onEditUser: (User) -> Unit = {},
    onDeleteUser: (User) -> Unit = {},
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
            UsersTableHeader()
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // User rows - using Column instead of LazyColumn to work inside scrollable parent
            users.forEach { user ->
                UserTableRow(
                    user = user,
                    onEdit = { onEditUser(user) },
                    onDelete = { onDeleteUser(user) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun UsersTableHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(Res.string.header_username),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = stringResource(Res.string.header_email),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = stringResource(Res.string.header_role),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = stringResource(Res.string.header_status),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.7f)
        )
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
private fun UserTableRow(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // USERNAME with avatar
        Row(
            modifier = Modifier.weight(1.2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(
                initial = user.initial,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.username,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // EMAIL
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // ROLE
        Text(
            text = user.role.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // STATUS - Using StatusChip with wrapContentWidth
        StatusChip(
            isActive = user.isActive,
            modifier = Modifier.weight(0.7f).wrapContentWidth(align = Alignment.Start)
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
 * Card list for displaying users on compact screens.
 */
@Composable
private fun UsersCardList(
    users: List<User>,
    onEditUser: (User) -> Unit,
    onDeleteUser: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        users.forEach { user ->
            UserCard(
                user = user,
                onEdit = { onEditUser(user) },
                onDelete = { onDeleteUser(user) }
            )
        }
    }
}

/**
 * Individual user card for compact screens with dropdown menu for actions.
 */
@Composable
private fun UserCard(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
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
            // Header row: Avatar, Name, Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    initial = user.initial,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = user.role.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
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
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status chip
            StatusChip(isActive = user.isActive)
        }
    }
}

/**
 * Avatar component for users with colored background based on initial.
 */
@Composable
fun UserAvatar(
    initial: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (initial.firstOrNull()?.uppercaseChar()) {
        'A' -> Color(0xFFE91E63)
        'B' -> Color(0xFF9C27B0)
        'C' -> Color(0xFF795548)
        'D' -> Color(0xFF009688)
        'E' -> Color(0xFF3F51B5)
        'F' -> Color(0xFF4CAF50)
        'G' -> Color(0xFFCDDC39)
        'H' -> Color(0xFF2196F3)
        'I' -> Color(0xFF00BCD4)
        'J' -> Color(0xFFFF5722)
        'K' -> Color(0xFF673AB7)
        'L' -> Color(0xFF9C27B0)
        'M' -> Color(0xFFFF9800)
        'N' -> Color(0xFF8BC34A)
        'O' -> Color(0xFFFFEB3B)
        'P' -> Color(0xFF4CAF50)
        'Q' -> Color(0xFF607D8B)
        'R' -> Color(0xFFF44336)
        'S' -> Color(0xFF03A9F4)
        'T' -> Color(0xFF00BCD4)
        'U' -> Color(0xFF9E9E9E)
        'V' -> Color(0xFFFF5722)
        'W' -> Color(0xFF795548)
        'X' -> Color(0xFF607D8B)
        'Y' -> Color(0xFFFFC107)
        'Z' -> Color(0xFF9E9E9E)
        else -> Color(0xFF607D8B)
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

private object UsersTablePreviewData {
    val sampleUsers = listOf(
        User(
            id = 1L,
            username = "anamartinez",
            email = "ana@freshveg.com",
            role = UserRole.ADMIN,
            tenantId = 1L,
            isActive = true
        ),
        User(
            id = 2L,
            username = "carlosruiz",
            email = "carlos@freshveg.com",
            role = UserRole.OPERATOR,
            tenantId = 1L,
            isActive = true
        ),
        User(
            id = 3L,
            username = "sofiafernandez",
            email = "sofia@freshveg.com",
            role = UserRole.VIEWER,
            tenantId = 1L,
            isActive = false
        )
    )
}

@Preview
@Composable
private fun UsersTablePreview() {
    GreenhouseAdminTheme {
        UsersTable(users = UsersTablePreviewData.sampleUsers)
    }
}

@Preview
@Composable
private fun UserAvatarPreview() {
    GreenhouseAdminTheme {
        UserAvatar(initial = "A")
    }
}
