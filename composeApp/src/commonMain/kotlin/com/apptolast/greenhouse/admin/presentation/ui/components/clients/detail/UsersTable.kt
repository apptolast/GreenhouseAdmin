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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.User
import com.apptolast.greenhouse.admin.data.model.UserRole
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.InitialAvatar
import com.apptolast.greenhouse.admin.presentation.ui.components.common.StatusChip
import com.apptolast.greenhouse.admin.presentation.ui.components.common.avatarColorForInitial
import com.apptolast.greenhouse.admin.presentation.ui.components.common.table.SortDirection
import com.apptolast.greenhouse.admin.presentation.ui.components.common.table.SortableColumnHeader
import com.apptolast.greenhouse.admin.presentation.ui.components.common.table.TableRowActions
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
 * Enum representing sortable columns in the Users table.
 */
private enum class UserSortColumn {
    USERNAME, EMAIL, ROLE
}

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
 * Sortable columns: USERNAME, EMAIL, ROLE
 */
@Composable
fun UsersTable(
    users: List<User>,
    onEditUser: (User) -> Unit = {},
    onDeleteUser: (User) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var sortColumn by remember { mutableStateOf<UserSortColumn?>(null) }
    var sortDirection by remember { mutableStateOf(SortDirection.ASCENDING) }

    // Sort users based on selected column and direction
    val sortedUsers = remember(users, sortColumn, sortDirection) {
        when (sortColumn) {
            UserSortColumn.USERNAME -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    users.sortedBy { it.username.lowercase() }
                } else {
                    users.sortedByDescending { it.username.lowercase() }
                }
            }

            UserSortColumn.EMAIL -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    users.sortedBy { it.email.lowercase() }
                } else {
                    users.sortedByDescending { it.email.lowercase() }
                }
            }

            UserSortColumn.ROLE -> {
                if (sortDirection == SortDirection.ASCENDING) {
                    users.sortedBy { it.role.displayName.lowercase() }
                } else {
                    users.sortedByDescending { it.role.displayName.lowercase() }
                }
            }

            null -> users
        }
    }

    fun onHeaderClick(column: UserSortColumn) {
        if (sortColumn == column) {
            sortDirection = sortDirection.toggle()
        } else {
            sortColumn = column
            sortDirection = SortDirection.ASCENDING
        }
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Header row with sorting
            UsersTableHeader(
                sortColumn = sortColumn,
                sortDirection = sortDirection,
                onSortClick = ::onHeaderClick
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // User rows - using Column instead of LazyColumn to work inside scrollable parent
            sortedUsers.forEach { user ->
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
private fun UsersTableHeader(
    sortColumn: UserSortColumn?,
    sortDirection: SortDirection,
    onSortClick: (UserSortColumn) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // USERNAME column - Sortable
        SortableColumnHeader(
            text = stringResource(Res.string.header_username),
            column = UserSortColumn.USERNAME,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(UserSortColumn.USERNAME) },
            modifier = Modifier.weight(1.2f)
        )
        // EMAIL column - Sortable
        SortableColumnHeader(
            text = stringResource(Res.string.header_email),
            column = UserSortColumn.EMAIL,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(UserSortColumn.EMAIL) },
            modifier = Modifier.weight(1.5f)
        )
        // ROLE column - Sortable
        SortableColumnHeader(
            text = stringResource(Res.string.header_role),
            column = UserSortColumn.ROLE,
            currentSortColumn = sortColumn,
            sortDirection = sortDirection,
            onClick = { onSortClick(UserSortColumn.ROLE) },
            modifier = Modifier.weight(0.8f)
        )
        // STATUS column - Not sortable
        Text(
            text = stringResource(Res.string.header_status),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.7f)
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
            InitialAvatar(
                initials = user.initial,
                color = avatarColorForInitial(user.initial),
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
        TableRowActions(onEdit = onEdit, onDelete = onDelete)
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
                InitialAvatar(
                    initials = user.initial,
                    color = avatarColorForInitial(user.initial),
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

private object UsersTablePreviewData {
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
        ),
        User(
            id = 3L,
            code = "USR-00003",
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

