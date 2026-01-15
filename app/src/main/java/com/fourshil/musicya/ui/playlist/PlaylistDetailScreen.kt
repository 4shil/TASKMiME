package com.fourshil.musicya.ui.playlist

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.AlbumArtImage
import com.fourshil.musicya.ui.components.LargeAlbumArt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val songs by viewModel.songs.collectAsState()
    val title by viewModel.title.collectAsState()
    val subtitle by viewModel.subtitle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val artUri by viewModel.artUri.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
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
                // Header with art and play buttons
                item {
                    PlaylistHeader(
                        title = title,
                        subtitle = subtitle,
                        artUri = artUri,
                        onPlayAll = { viewModel.playAll() },
                        onShuffle = { viewModel.shufflePlay() }
                    )
                }

                // Song list
                itemsIndexed(songs, key = { index, song -> "${song.id}_$index" }) { index, song ->
                    PlaylistSongItem(
                        song = song,
                        trackNumber = index + 1,
                        onClick = { viewModel.playSongAt(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistHeader(
    title: String,
    subtitle: String,
    artUri: String?,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Album art with proper fallback
        LargeAlbumArt(
            uri = artUri?.let { Uri.parse(it) },
            contentDescription = "Cover for $title",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Play buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onPlayAll,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Play")
            }

            OutlinedButton(
                onClick = onShuffle,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Shuffle, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Shuffle")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
    }
}

@Composable
fun PlaylistSongItem(
    song: Song,
    trackNumber: Int,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = "${song.artist} • ${song.durationFormatted}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            // Album art for each song
            AlbumArtImage(
                uri = song.albumArtUri,
                size = 48.dp
            )
        },
        trailingContent = {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    )
}
