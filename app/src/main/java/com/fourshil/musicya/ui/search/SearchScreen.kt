package com.fourshil.musicya.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.components.PlaylistArtGrid
import com.fourshil.musicya.ui.components.SongListItem
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header: SEARCH
        Row(verticalAlignment = Alignment.CenterVertically) {
             ArtisticButton(
                onClick = onBack,
                icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = PureBlack) },
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "LIBRARY",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PureBlack.copy(alpha=0.6f)
                )
                Text(
                    text = "SEARCH",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    ),
                    color = PureBlack
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, PureBlack)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, null, tint = MangaRed, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = PureBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    cursorBrush = SolidColor(MangaRed),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                "ENTER QUERY...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = PureBlack.copy(alpha = 0.4f),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        innerTextField()
                    }
                )
                if (query.isNotEmpty()) {
                    Icon(
                        Icons.Default.Close, 
                        null, 
                        tint = PureBlack, 
                        modifier = Modifier.clickable { viewModel.onQueryChange("") }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (isSearching) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PureBlack)
            }
        } else if (query.isEmpty()) {
             // Empty State
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 160.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                 // Songs
                if (songs.isNotEmpty()) {
                    item { SectionHeader("TRACKS") }
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
                             // Minimal interactions for search results
                            onLongClick = {},
                            onMoreClick = {} 
                        )
                    }
                }

                // Albums
                if (albums.isNotEmpty()) {
                    item { SectionHeader("ALBUMS") }
                    items(albums, key = { "album_${it.id}" }) { album ->
                         SearchArtisticCard(
                            title = album.name,
                            subtitle = album.artist,
                            artUri = listOfNotNull(album.artUri),
                            onClick = { onAlbumClick(album.id) }
                         )
                    }
                }

                // Artists
                if (artists.isNotEmpty()) {
                    item { SectionHeader("ARTISTS") }
                    items(artists, key = { "artist_${it.id}" }) { artist ->
                         // Need art uris for artists? Assuming empty for now or would need to fetch
                         SearchArtisticCard(
                            title = artist.name,
                            subtitle = "${artist.songCount} SONGS",
                            artUri = emptyList(), // or pass if available
                            onClick = { onArtistClick(artist.name) }
                         )
                    }
                }

                if (songs.isEmpty() && albums.isEmpty() && artists.isEmpty()) {
                     item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("NO DATA FOUND", style = MaterialTheme.typography.headlineSmall, color = PureBlack.copy(0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
        color = MangaRed,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SearchArtisticCard(
    title: String,
    subtitle: String,
    artUri: List<android.net.Uri>,
    onClick: () -> Unit
) {
    ArtisticCard(
        onClick = onClick, 
        modifier = Modifier.fillMaxWidth(),
        showHalftone = false // Performance optimization
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).border(2.dp, PureBlack).background(Color.White)
            ) {
                 if (artUri.isNotEmpty()) {
                     PlaylistArtGrid(uris = artUri, size = 48.dp)
                 } else {
                     Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
                 }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title.uppercase(), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1)
                Text(subtitle.uppercase(), style = MaterialTheme.typography.labelSmall, color = PureBlack.copy(0.6f))
            }
        }
    }
}
