package com.fourshil.musicya.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.data.model.Song

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = { Text("Search songs, albums, artists...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (query.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Search your library", color = MaterialTheme.colorScheme.outline)
                }
            }
        } else if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Songs Section
                if (songs.isNotEmpty()) {
                    item {
                        Text(
                            "Songs",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                        )
                    }
                    items(songs, key = { it.id }) { song ->
                        SearchSongItem(song) {
                            viewModel.playSong(song)
                            onSongClick(song)
                        }
                    }
                }

                // Albums Section
                if (albums.isNotEmpty()) {
                    item {
                        Text(
                            "Albums",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                        )
                    }
                    items(albums, key = { it.id }) { album ->
                        SearchAlbumItem(album) { onAlbumClick(album.id) }
                    }
                }

                // Artists Section
                if (artists.isNotEmpty()) {
                    item {
                        Text(
                            "Artists",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                        )
                    }
                    items(artists, key = { it.id }) { artist ->
                        SearchArtistItem(artist) { onArtistClick(artist.name) }
                    }
                }

                // No results
                if (songs.isEmpty() && albums.isEmpty() && artists.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No results found", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchSongItem(song: Song, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(song.artist, maxLines = 1, style = MaterialTheme.typography.bodySmall) },
        leadingContent = {
            Card(modifier = Modifier.size(40.dp), shape = MaterialTheme.shapes.small) {
                AsyncImage(song.albumArtUri, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
        }
    )
}

@Composable
fun SearchAlbumItem(album: Album, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(album.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(album.artist, maxLines = 1, style = MaterialTheme.typography.bodySmall) },
        leadingContent = {
            Card(modifier = Modifier.size(40.dp), shape = MaterialTheme.shapes.small) {
                AsyncImage(album.artUri, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
        }
    )
}

@Composable
fun SearchArtistItem(artist: Artist, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(artist.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text("${artist.songCount} songs", style = MaterialTheme.typography.bodySmall) },
        leadingContent = {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.padding(8.dp))
            }
        }
    )
}
