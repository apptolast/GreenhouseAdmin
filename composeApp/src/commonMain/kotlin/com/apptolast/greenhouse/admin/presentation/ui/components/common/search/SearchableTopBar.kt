package com.apptolast.greenhouse.admin.presentation.ui.components.common.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.AdaptiveDimens
import com.apptolast.greenhouse.admin.presentation.ui.adaptive.LocalAppWindowInfo
import greenhouseadmin.composeapp.generated.resources.Res
import greenhouseadmin.composeapp.generated.resources.search
import greenhouseadmin.composeapp.generated.resources.search_category_alerts
import greenhouseadmin.composeapp.generated.resources.search_category_clients
import greenhouseadmin.composeapp.generated.resources.search_category_devices
import greenhouseadmin.composeapp.generated.resources.search_category_greenhouses
import greenhouseadmin.composeapp.generated.resources.search_category_sectors
import greenhouseadmin.composeapp.generated.resources.search_category_settings
import greenhouseadmin.composeapp.generated.resources.search_category_users
import greenhouseadmin.composeapp.generated.resources.search_dashboard_placeholder
import greenhouseadmin.composeapp.generated.resources.search_no_results
import org.jetbrains.compose.resources.stringResource

/**
 * Top bar with search autocomplete functionality.
 * Replaces DashboardTopBar with an autocomplete dropdown that shows
 * matching entities grouped by category.
 */
@Composable
fun SearchableTopBar(
    title: String,
    subtitle: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<SearchResult> = emptyList(),
    onResultSelected: (SearchNavigationTarget) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowInfo = LocalAppWindowInfo.current
    val horizontalPadding = AdaptiveDimens.horizontalPadding()
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val showDropdown = isFocused && searchQuery.length >= 2

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Title section
        Column(
            modifier = if (windowInfo.isCompact) Modifier.weight(1f) else Modifier
        ) {
            Text(
                text = title,
                style = if (windowInfo.isCompact) {
                    MaterialTheme.typography.titleLarge
                } else {
                    MaterialTheme.typography.headlineSmall
                },
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Search field with autocomplete dropdown
        if (!windowInfo.isCompact) {
            val searchFieldWidth = if (windowInfo.isMedium) 200.dp else 280.dp

            Box {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .width(searchFieldWidth)
                        .height(48.dp)
                        .onFocusChanged { isFocused = it.isFocused },
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.search_dashboard_placeholder),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(Res.string.search),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                if (showDropdown) {
                    Popup(
                        alignment = Alignment.TopStart,
                        onDismissRequest = { focusManager.clearFocus() },
                        properties = PopupProperties(focusable = false)
                    ) {
                        SearchDropdown(
                            results = searchResults,
                            searchQuery = searchQuery,
                            width = searchFieldWidth,
                            onResultClick = { target ->
                                onSearchQueryChange("")
                                focusManager.clearFocus()
                                onResultSelected(target)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchDropdown(
    results: List<SearchResult>,
    searchQuery: String,
    width: androidx.compose.ui.unit.Dp,
    onResultClick: (SearchNavigationTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(width)
            .padding(top = 52.dp) // offset below the text field
            .heightIn(max = 400.dp),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        if (results.isEmpty() && searchQuery.length >= 2) {
            // Empty state
            Text(
                text = stringResource(Res.string.search_no_results),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            val groupedResults = results.groupBy { it.category }

            LazyColumn {
                groupedResults.forEach { (category, categoryResults) ->
                    item(key = "header-$category") {
                        SearchCategoryHeader(categoryName = category.displayName())
                    }
                    items(
                        items = categoryResults,
                        key = { it.id }
                    ) { result ->
                        SearchResultItem(
                            result = result,
                            onClick = { onResultClick(result.navigationTarget) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultCategory.displayName(): String = when (this) {
    SearchResultCategory.CLIENT -> stringResource(Res.string.search_category_clients)
    SearchResultCategory.GREENHOUSE -> stringResource(Res.string.search_category_greenhouses)
    SearchResultCategory.SECTOR -> stringResource(Res.string.search_category_sectors)
    SearchResultCategory.DEVICE -> stringResource(Res.string.search_category_devices)
    SearchResultCategory.ALERT -> stringResource(Res.string.search_category_alerts)
    SearchResultCategory.SETTING -> stringResource(Res.string.search_category_settings)
    SearchResultCategory.USER -> stringResource(Res.string.search_category_users)
}
