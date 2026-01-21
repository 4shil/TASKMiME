package com.fourshil.musicya.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Navigation item data class
 */
data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

// Legacy alias for backward compatibility
typealias ArtisticNavItem = NavItem

/**
 * Clean Minimalistic Bottom Navigation
 * Material3 styled with pill-shaped indicator
 */
@Composable
fun BottomNavigation(
    items: List<NavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(NeoDimens.BottomNavHeight),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = NeoDimens.ElevationHigh
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NeoDimens.SpacingL),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                NavItemButton(
                    item = item,
                    selected = selected,
                    onClick = { onItemClick(item.route) }
                )
            }
        }
    }
}

@Composable
private fun NavItemButton(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    val indicatorWidth by animateDpAsState(
        targetValue = if (selected) 56.dp else 0.dp,
        animationSpec = spring(),
        label = "indicatorWidth"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = NeoDimens.SpacingXS)
    ) {
        // Icon with pill indicator
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 32.dp)
                .clip(RoundedCornerShape(NeoDimens.CornerFull))
                .background(
                    if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(NeoDimens.IconMedium),
                tint = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        // Label
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(top = NeoDimens.SpacingXXS)
        )
    }
}

/**
 * Legacy function for backward compatibility
 */
@Composable
fun ArtisticBottomNavigation(
    items: List<ArtisticNavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    BottomNavigation(
        items = items,
        currentRoute = currentRoute,
        onItemClick = onItemClick,
        modifier = modifier
    )
}
