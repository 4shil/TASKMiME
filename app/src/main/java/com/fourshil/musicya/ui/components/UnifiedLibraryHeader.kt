package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fourshil.musicya.ui.navigation.NavigationUtils
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.Slate900

@Composable
fun UnifiedLibraryHeader(
    title: String,
    currentRoute: String?,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = NeoDimens.ScreenPadding)
    ) {
        Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))
        
        // Header: Title + Menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            ArtisticButton(
                onClick = onMenuClick,
                icon = { Icon(Icons.Default.Menu, null, tint = MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier.size(NeoDimens.ButtonHeightMedium)
            )
        }
        
        Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))
        
        // Search Bar (Visual only for now, can be clickable to go to search screen)
        ArtisticCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSearchClick,
            backgroundColor = MaterialTheme.colorScheme.surface,
            borderColor = MaterialTheme.colorScheme.outline
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NeoDimens.SpacingL, vertical = NeoDimens.SpacingM),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = NeoCoral,
                    modifier = Modifier.size(NeoDimens.IconMedium) // Was 22.dp close to 24.dp
                )
                Spacer(modifier = Modifier.width(NeoDimens.SpacingM))
                Text(
                    text = "Search...",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))

        TopNavigationChips(
            items = NavigationUtils.LibraryTabs,
            currentRoute = currentRoute,
            onItemClick = onNavigate,
            modifier = Modifier.padding(bottom = NeoDimens.SpacingXL)
        )
    }
}
