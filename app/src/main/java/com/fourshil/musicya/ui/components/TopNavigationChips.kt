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
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.Slate50
import com.fourshil.musicya.ui.theme.Slate700
import com.fourshil.musicya.ui.theme.Slate900
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
        contentPadding = PaddingValues(horizontal = NeoDimens.SpacingNone), // Or 0.dp
        horizontalArrangement = Arrangement.spacedBy(NeoDimens.SpacingS)
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
    // In our theme, Primary is Slate900 (Light) and Slate50 (Dark), so we use Primary/OnPrimary
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(NeoDimens.BorderThin, borderColor, RoundedCornerShape(50))
            .background(backgroundColor, RoundedCornerShape(50))
            .padding(horizontal = NeoDimens.SpacingL, vertical = NeoDimens.SpacingS),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.5.sp
            ),
            color = contentColor
        )
    }
}

