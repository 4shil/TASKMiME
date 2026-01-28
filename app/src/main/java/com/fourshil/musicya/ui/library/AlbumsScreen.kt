package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.ui.components.NeoEmptyState
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.components.NeoCard
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.ui.unit.sp

/**
 * Clean Minimalistic Albums Screen
 * With grid layout and clean album cards
 */
@Composable
fun AlbumsScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onAlbumClick: (Long) -> Unit = {},
    currentRoute: String? = null,
    onNavigate: (String) -> Unit = {}
) {
    val albums by viewModel.albums.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val gridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    val isScrolling by remember { derivedStateOf { gridState.isScrollInProgress } }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = NeoDimens.ScreenPadding),
        state = gridState,
        contentPadding = PaddingValues(
            top = 0.dp,
            bottom = NeoDimens.ListBottomPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(NeoDimens.SpacingM),
        verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingM)
    ) {
        when {
            isLoading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            albums.isEmpty() -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp), contentAlignment = Alignment.Center) {
                         NeoEmptyState(
                            message = "NO ALBUMS FOUND",
                            icon = Icons.Default.Album
                        )
                    }
                }
            }
            
            else -> {
                items(albums, key = { it.id }) { album ->
                    AlbumCard(
                        album = album,
                        isScrolling = isScrolling,
                        onClick = { onAlbumClick(album.id) }
                    )
                }
            }
        }
    }
}

/**
 * Neo-Brutalist Album Card
 */
@Composable
private fun AlbumCard(
    album: Album,
    isScrolling: Boolean = false,
    onClick: () -> Unit
) {
    NeoCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        shadowSize = 4.dp,
        backgroundColor = Color.White,
        borderWidth = 2.dp
    ) {
        Column {
            // Album Art
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.LightGray)
                    .border(
                        width = 0.dp, // No border for image inside card if unnecessary, or bottom border
                        color = Color.Black
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Defer loading while scrolling
                val model = if (isScrolling) null else ImageRequest.Builder(LocalContext.current)
                    .data(album.artUri)
                    .crossfade(true)
                    .build()
                
                if (model != null) {
                    AsyncImage(
                        model = model,
                        contentDescription = album.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // Separator border
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.Black))

            // Album Info
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${album.songCount} SONGS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

