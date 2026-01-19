package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import com.fourshil.musicya.ui.components.TopNavItem
import com.fourshil.musicya.ui.components.TopNavigationChips
import com.fourshil.musicya.ui.navigation.Screen
import com.fourshil.musicya.ui.navigation.NavigationUtils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.components.HalftoneBackground
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack
import com.fourshil.musicya.ui.theme.PureWhite
import com.fourshil.musicya.ui.theme.NeoDimens

@Composable
fun AlbumsScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onAlbumClick: (Long) -> Unit = {},
    currentRoute: String? = null,
    onNavigate: (String) -> Unit = {}
) {
    val albums by viewModel.albums.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Screen-level Halftone handled in Scaffold, but we can add specific flair here
    
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = NeoDimens.ScreenPadding),
        contentPadding = PaddingValues(bottom = NeoDimens.ListBottomPadding),
        horizontalArrangement = Arrangement.spacedBy(NeoDimens.SpacingL),
        verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingXL)
    ) {
        // Header & Nav (Span All)
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(NeoDimens.HeaderHeight))
        }

        if (isLoading) {
             item(span = { GridItemSpan(maxLineSpan) }) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                }
             }
        } else if (albums.isEmpty()) {
             item(span = { GridItemSpan(maxLineSpan) }) {
                 Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("NULL", style = MaterialTheme.typography.headlineLarge)
                }
             }
        } else {
            items(albums, key = { it.id }) { album ->
                AlbumArtisticCard(
                    album = album,
                    onClick = { onAlbumClick(album.id) }
                )
            }
        }
    }
}

@Composable
fun AlbumArtisticCard(
    album: Album,
    onClick: () -> Unit
) {
    // "Ink" Card Style
    ArtisticCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().aspectRatio(0.8f), // Taller card for text
        showHalftone = false // Performance optimization
    ) {
        Column {
             // Art Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(NeoDimens.BorderMedium, MaterialTheme.colorScheme.onSurface)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Background Pattern
                HalftoneBackground(modifier = Modifier.alpha(0.2f))
                
                AsyncImage(
                     model = ImageRequest.Builder(LocalContext.current)
                        .data(album.artUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = album.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Code Tag
                Box(
                    modifier = Modifier
                        .padding(NeoDimens.SpacingS)
                        .background(MaterialTheme.colorScheme.onSurface)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "ART-${album.id % 99}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(NeoDimens.SpacingS))
            
            Text(
                text = album.name.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = album.artist.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
