package com.fourshil.musicya.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.ui.components.PlaylistArtGrid

@Composable
fun ArtistsScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onArtistClick: (String) -> Unit = {}
) {
    val artists by viewModel.artists.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (artists.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No artists found", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(artists, key = { it.id }) { artist ->
                // Find songs by this artist for the artwork grid
                val artistSongs = songs.filter { it.artist == artist.name }
                
                ArtistListItem(
                    artist = artist,
                    artUris = artistSongs.map { it.albumArtUri },
                    onClick = { onArtistClick(artist.name) }
                )
            }
        }
    }
}

@Composable
fun ArtistListItem(
    artist: Artist,
    artUris: List<android.net.Uri>,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(text = artist.name)
        },
        supportingContent = {
            Text(
                text = "${artist.songCount} songs • ${artist.albumCount} albums",
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            PlaylistArtGrid(
                uris = artUris,
                size = 48.dp
            )
        }
    )
}
