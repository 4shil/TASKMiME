package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fourshil.musicya.ui.theme.NeoBlue
import com.fourshil.musicya.ui.theme.NeoDimens

data class TopNavItem(
    val route: String,
    val label: String
)

/**
 * Neo-Brutalism Navigation Chips
 * Horizontal scrollable navigation with clean pill design
 */
@Composable
fun TopNavigationChips(
    items: List<TopNavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            val isSelected = currentRoute == item.route
            TopNavChip(
                label = item.label,
                isSelected = isSelected,
                onClick = { onItemClick(item.route) }
            )
        }
    }
}

@Composable
private fun TopNavChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Neo-Brutalist: Selected = NeoBlue + Black Border + Bold
    // Unselected = White + Black Border
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val borderColor = MaterialTheme.colorScheme.outline
    val borderWidth = 2.dp // Consistent thick border
    val fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold

    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(borderWidth, borderColor, RoundedCornerShape(50))
            .background(backgroundColor, RoundedCornerShape(50))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = fontWeight,
                letterSpacing = 1.sp
            ),
            color = contentColor
        )
    }
}

