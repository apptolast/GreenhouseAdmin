package com.apptolast.greenhouse.admin.presentation.ui.components.common.search

/**
 * Category of a search result, used for grouping and icon display.
 */
enum class SearchResultCategory {
    CLIENT,
    GREENHOUSE,
    SECTOR,
    DEVICE,
    ALERT,
    SETTING,
    USER
}

/**
 * A single search result item displayed in the autocomplete dropdown.
 */
data class SearchResult(
    val id: String,
    val category: SearchResultCategory,
    val title: String,
    val subtitle: String?,
    val code: String,
    val navigationTarget: SearchNavigationTarget
)

/**
 * Encodes where clicking a search result should navigate.
 */
sealed interface SearchNavigationTarget {
    data class ToClient(val clientId: Long) : SearchNavigationTarget

    data class ToGreenhouseTab(
        val clientId: Long,
        val greenhouseId: Long
    ) : SearchNavigationTarget

    data class ToSector(
        val clientId: Long,
        val greenhouseId: Long,
        val sectorId: Long,
        val subTab: String? = null
    ) : SearchNavigationTarget

    data class ToUsersTab(val clientId: Long) : SearchNavigationTarget
}
