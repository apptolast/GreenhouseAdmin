package com.apptolast.greenhouse.admin.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.PaginationInfo
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.pagination_next
import greenhouseadmin.composeapp.generated.resources.pagination_previous
import greenhouseadmin.composeapp.generated.resources.rows_per_page
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Pagination controls for client list.
 */
@Composable
fun ClientsPagination(
    pagination: PaginationInfo,
    onPageChanged: (Int) -> Unit = {},
    onPageSizeChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rows per page selector
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.rows_per_page),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(8.dp))

            PageSizeSelector(
                currentSize = pagination.pageSize,
                onSizeSelected = onPageSizeChanged
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Page info and navigation
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${pagination.displayRange} of ${pagination.totalItems}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = { onPageChanged(pagination.currentPage - 1) },
                enabled = pagination.hasPreviousPage,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(Res.string.pagination_previous),
                    tint = if (pagination.hasPreviousPage) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    }
                )
            }

            IconButton(
                onClick = { onPageChanged(pagination.currentPage + 1) },
                enabled = pagination.hasNextPage,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(Res.string.pagination_next),
                    tint = if (pagination.hasNextPage) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    }
                )
            }
        }
    }
}

@Composable
private fun PageSizeSelector(
    currentSize: Int,
    onSizeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val pageSizeOptions = listOf(5, 10, 25, 50)

    TextButton(
        onClick = { expanded = true },
        modifier = modifier
    ) {
        Text(
            text = currentSize.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        pageSizeOptions.forEach { size ->
            DropdownMenuItem(
                text = { Text(size.toString()) },
                onClick = {
                    onSizeSelected(size)
                    expanded = false
                }
            )
        }
    }
}

@Preview
@Composable
private fun ClientsPaginationPreview() {
    GreenhouseAdminTheme {
        ClientsPagination(
            pagination = PaginationInfo(
                currentPage = 1,
                pageSize = 10,
                totalItems = 156
            )
        )
    }
}

@Preview
@Composable
private fun ClientsPaginationFirstPagePreview() {
    GreenhouseAdminTheme {
        ClientsPagination(
            pagination = PaginationInfo(
                currentPage = 0,
                pageSize = 10,
                totalItems = 50
            )
        )
    }
}
