package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fourshil.musicya.ui.navigation.NavigationUtils
import com.fourshil.musicya.ui.theme.NeoBackground

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
            // Removed horizontal padding here to allow full-width scroll
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header: Title + Menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp), // Added padding here
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            NeoButton(
                onClick = onMenuClick,
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                shadowSize = 0.dp
            ) {
                Icon(Icons.Default.Menu, null, tint = MaterialTheme.colorScheme.onSurface)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Search Bar
        NeoCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 24.dp), // Added padding here
            onClick = onSearchClick,
            // backgroundColor = Color.White,
            shadowSize = 0.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "SEARCH YOUR MUSIC...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        TopNavigationChips(
            items = NavigationUtils.LibraryTabs,
            currentRoute = currentRoute,
            onItemClick = onNavigate,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}
