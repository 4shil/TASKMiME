package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.ui.components.PlaylistArtGrid
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.components.NeoCard
import com.fourshil.musicya.ui.components.NeoButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border

/**
 * Clean Minimalistic Artists Screen
 */
@Composable
fun ArtistsScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onArtistClick: (String) -> Unit = {},
    currentRoute: String? = null,
    onNavigate: (String) -> Unit = {}
) {
    val artists by viewModel.artists.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            top = 0.dp,
            bottom = NeoDimens.ListBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingXS)
    ) {
        when {
            isLoading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            artists.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingM)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                "No artists found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            else -> {
                items(
                    items = artists,
                    key = { it.id },
                    contentType = { "artist_item" }
                ) { artist ->
                    val artistSongs = songs.filter { it.artist == artist.name }
                    ArtistListItem(
                        artist = artist,
                        artUris = artistSongs.map { it.albumArtUri },
                        isScrolling = isScrolling,
                        onClick = { onArtistClick(artist.name) }
                    )
                }
            }
        }
    }
}

/**
 * Neo-Brutalist Artist List Item
 */
@Composable
private fun ArtistListItem(
    artist: Artist,
    artUris: List<android.net.Uri>,
    isScrolling: Boolean = false,
    onClick: () -> Unit
) {
    NeoCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        shadowSize = 4.dp,
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderWidth = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular artist art
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (artUris.isNotEmpty()) {
                    PlaylistArtGrid(uris = artUris.take(4), size = 56.dp, isScrolling = isScrolling)
                } else {
                     com.fourshil.musicya.ui.components.ArtistImage(size = 56.dp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Artist info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${artist.songCount} SONGS • ${artist.albumCount} ALBUMS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

