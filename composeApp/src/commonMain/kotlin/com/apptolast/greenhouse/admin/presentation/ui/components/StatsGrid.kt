package com.apptolast.greenhouse.admin.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.greenhouse.admin.data.model.StatCard
import com.apptolast.greenhouse.admin.data.model.StatCardIcon
import com.apptolast.greenhouse.admin.data.model.StatCardSubtitleColor
import com.apptolast.greenhouse.admin.presentation.ui.theme.GreenhouseAdminTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Grid layout for displaying stats cards.
 * Responsive: adapts columns based on available width.
 */
@Composable
fun StatsGrid(
    statCards: List<StatCard>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 220.dp),
        modifier = modifier,
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = statCards,
            key = { it.id }
        ) { card ->
            StatsCard(
                statCard = card,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private object StatsGridPreviewData {
    val sampleCards = listOf(
        StatCard("1", "Total Clients", "156", "+12%", StatCardSubtitleColor.SUCCESS, StatCardIcon.PEOPLE),
        StatCard("2", "Greenhouses", "48", "Active", StatCardSubtitleColor.DEFAULT, StatCardIcon.GREENHOUSE),
        StatCard("3", "Devices", "234", "Online", StatCardSubtitleColor.DEFAULT, StatCardIcon.DEVICES),
        StatCard("4", "Alerts", "8", "3 critical", StatCardSubtitleColor.WARNING, StatCardIcon.ALERT)
    )
}

@Preview
@Composable
private fun StatsGridPreview() {
    GreenhouseAdminTheme {
        StatsGrid(
            statCards = StatsGridPreviewData.sampleCards,
            modifier = Modifier.height(300.dp)
        )
    }
}
