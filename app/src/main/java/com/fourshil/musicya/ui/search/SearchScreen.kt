package com.fourshil.musicya.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.AlbumArtImage
import com.fourshil.musicya.ui.components.MinimalIconButton
import com.fourshil.musicya.ui.components.SongListItem
import com.fourshil.musicya.ui.components.NeoScaffold
import com.fourshil.musicya.ui.components.NeoCard
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Clean Minimalistic Search Screen
 */
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onSongClick: (Song) -> Unit = {},
    onAlbumClick: (Long) -> Unit = {},
    onArtistClick: (String) -> Unit = {}
) {
    val query by viewModel.query.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    NeoScaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Clean header with search bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = NeoDimens.ScreenPadding)
            ) {
                // Back button and title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = NeoDimens.SpacingL),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MinimalIconButton(
                        onClick = onBack,
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                    Spacer(modifier = Modifier.width(NeoDimens.SpacingL))
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(NeoDimens.SpacingL))

                // Search Bar
                NeoCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(NeoDimens.CornerFull),
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    shadowSize = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = NeoDimens.SpacingL,
                            vertical = NeoDimens.SpacingM
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(NeoDimens.IconMedium)
                        )
                        Spacer(modifier = Modifier.width(NeoDimens.SpacingM))
                        BasicTextField(
                            value = query,
                            onValueChange = { viewModel.onQueryChange(it) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                if (query.isEmpty()) {
                                    Text(
                                        "Search songs, albums, artists...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (query.isNotEmpty()) {
                            MinimalIconButton(
                                onClick = { viewModel.onQueryChange("") },
                                icon = Icons.Default.Close,
                                contentDescription = "Clear search",
                                size = 32.dp,
                                iconSize = 18.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(NeoDimens.SpacingM))
            }
        }
    ) { padding ->
        when {
            isSearching -> {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            
            query.isEmpty() -> {
                // Empty state - prompt to search
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingM)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            "Search your music library",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            songs.isEmpty() && albums.isEmpty() && artists.isEmpty() -> {
                // No results
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingM)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            "No results found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            else -> {
                // Results
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(bottom = NeoDimens.ListBottomPadding),
                    verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingXS)
                ) {
                    // Songs Section
                    if (songs.isNotEmpty()) {
                        item { 
                            SectionHeader("Songs", songs.size) 
                        }
                        items(songs, key = { "song_${it.id}" }) { song ->
                            SongListItem(
                                song = song,
                                isFavorite = false,
                                isSelected = false,
                                isSelectionMode = false,
                                onClick = {
                                    viewModel.playSong(song)
                                    onSongClick(song)
                                },
                                onLongClick = {},
                                onMoreClick = {}
                            )
                        }
                    }

                    // Albums Section
                    if (albums.isNotEmpty()) {
                        item { 
                            Spacer(modifier = Modifier.height(NeoDimens.SpacingM))
                            SectionHeader("Albums", albums.size) 
                        }
                        items(albums, key = { "album_${it.id}" }) { album ->
                            SearchResultItem(
                                title = album.name,
                                subtitle = album.artist,
                                artUri = album.artUri,
                                onClick = { onAlbumClick(album.id) }
                            )
                        }
                    }

                    // Artists Section
                    if (artists.isNotEmpty()) {
                        item { 
                            Spacer(modifier = Modifier.height(NeoDimens.SpacingM))
                            SectionHeader("Artists", artists.size) 
                        }
                        items(artists, key = { "artist_${it.id}" }) { artist ->
                            SearchResultItem(
                                title = artist.name,
                                subtitle = "${artist.songCount} songs",
                                artUri = null,
                                isArtist = true,
                                onClick = { onArtistClick(artist.name) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = NeoDimens.ScreenPadding, vertical = NeoDimens.SpacingS),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SearchResultItem(
    title: String,
    subtitle: String,
    artUri: android.net.Uri?,
    isArtist: Boolean = false,
    onClick: () -> Unit
) {
    NeoCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(NeoDimens.CornerMedium),
        backgroundColor = MaterialTheme.colorScheme.surface,
        shadowSize = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(NeoDimens.SpacingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art or Artist icon
            Box(
                modifier = Modifier
                    .size(NeoDimens.AlbumArtSmall)
                    .clip(RoundedCornerShape(if (isArtist) NeoDimens.CornerFull else NeoDimens.CornerSmall))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(if (isArtist) NeoDimens.CornerFull else NeoDimens.CornerSmall)),
                contentAlignment = Alignment.Center
            ) {
                if (artUri != null) {
                    AlbumArtImage(uri = artUri, size = NeoDimens.AlbumArtSmall)
                } else {
                    Icon(
                        imageVector = if (isArtist) Icons.Default.Person else Icons.Default.Album,
                        contentDescription = null,
                        modifier = Modifier.size(NeoDimens.IconMedium),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(NeoDimens.SpacingM))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(NeoDimens.IconMedium)
            )
        }
    }
}
