package com.fourshil.musicya.ui.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
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
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val songs by viewModel.favoriteSongs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Selection state
    val selectionState = rememberSelectionState()
    
    // Dialog states
    var showActionsSheet by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    
    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        songs.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No favorites yet", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Songs you favorite will appear here",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                    FavoriteSongItem(
                        song = song,
                        isSelected = selectionState.isSelected(song.id),
                        isSelectionMode = selectionState.isSelectionMode,
                        onClick = {
                            if (selectionState.isSelectionMode) {
                                selectionState.toggleSelection(song.id)
                            } else {
                                viewModel.playSongAt(index)
                            }
                        },
                        onLongClick = {
                            if (!selectionState.isSelectionMode) {
                                selectionState.startSelection(song.id)
                            }
                        },
                        onMoreClick = {
                            selectedSong = song
                            showActionsSheet = true
                        }
                    )
                }
            }
        }
    }
    
    // Song actions bottom sheet
    if (showActionsSheet && selectedSong != null) {
        SongActionsBottomSheet(
            song = selectedSong!!,
            isFavorite = true,
            onDismiss = { showActionsSheet = false },
            onPlayNext = { viewModel.playNext(selectedSong!!) },
            onAddToQueue = { viewModel.addToQueue(selectedSong!!) },
            onToggleFavorite = { viewModel.toggleFavorite(selectedSong!!.id) },
            onAddToPlaylist = { /* TODO */ },
            onViewDetails = { showDetailsDialog = true },
            onDelete = { /* TODO */ }
        )
    }
    
    // Song details dialog
    if (showDetailsDialog && selectedSong != null) {
        SongDetailsDialog(
            song = selectedSong!!,
            onDismiss = { showDetailsDialog = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FavoriteSongItem(
    song: Song,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),
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
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null
                )
            } else {
                Card(
                    modifier = Modifier.size(48.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    AsyncImage(
                        model = song.albumArtUri,
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        },
        trailingContent = {
            IconButton(onClick = onMoreClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }
        }
    )
}
