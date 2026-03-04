package com.apptolast.greenhouse.admin.presentation.ui.components.clients.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.apptolast.greenhouse.admin.data.model.User
import com.apptolast.greenhouse.admin.data.model.UserRole
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.ProvideAppWindowInfo
import com.apptolast.greenhouse.admin.presentation.ui.components.common.TabContentWrapper
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.new_user
import greenhouseadmin.composeapp.generated.resources.users_empty
import greenhouseadmin.composeapp.generated.resources.users_subtitle
import greenhouseadmin.composeapp.generated.resources.users_title
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
    TabContentWrapper(
        title = Res.string.users_title,
        subtitle = Res.string.users_subtitle,
        addButtonText = Res.string.new_user,
        items = users,
        isLoading = isLoading,
        error = error,
        emptyMessage = Res.string.users_empty,
        onAdd = onAddUser,
        onRetry = onRetry,
        modifier = modifier
    ) { userList ->
        UsersTableOrCards(
            users = userList,
            onEditUser = onEditUser,
            onDeleteUser = onDeleteUser
        )
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
