package com.fourshil.musicya.ui.library

import android.Manifest
import android.os.Build
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@Composable
fun SongsScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onSongClick: (Int) -> Unit = {}
) {
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    
    // Selection state
    val selectionState = rememberSelectionState()
    
    // Dialog states
    var showActionsSheet by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showAddToPlaylistSheet by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBulkActionsSheet by remember { mutableStateOf(false) }

    // Permission handling
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    
    val permissionsState = rememberMultiplePermissionsState(permissions)
    
    // Request permission on first launch
    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
    
    // Reload library when permission is granted
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            viewModel.loadLibrary()
        }
    }
    
    // Selection mode top bar
    if (selectionState.isSelectionMode) {
        SelectionTopBar(
            selectedCount = selectionState.selectedCount,
            onClose = { selectionState.clearSelection() },
            onSelectAll = { selectionState.selectAll(songs.map { it.id }) },
            onActions = { showBulkActionsSheet = true }
        )
    }

    when {
        !permissionsState.allPermissionsGranted -> {
            // Permission not granted
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Permission Required", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Grant audio permission to see your music")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        songs.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No songs found", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.loadLibrary() }) {
                        Text("Refresh")
                    }
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                    val isFavorite = song.id in favoriteIds
                    
                    SongListItem(
                        song = song,
                        isFavorite = isFavorite,
                        isSelected = selectionState.isSelected(song.id),
                        isSelectionMode = selectionState.isSelectionMode,
                        onClick = {
                            if (selectionState.isSelectionMode) {
                                selectionState.toggleSelection(song.id)
                            } else {
                                viewModel.playSongAt(index)
                                onSongClick(index)
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
        val isFavorite = selectedSong!!.id in favoriteIds
        SongActionsBottomSheet(
            song = selectedSong!!,
            isFavorite = isFavorite,
            onDismiss = { showActionsSheet = false },
            onPlayNext = { viewModel.playNext(selectedSong!!) },
            onAddToQueue = { viewModel.addToQueue(selectedSong!!) },
            onToggleFavorite = { viewModel.toggleFavorite(selectedSong!!.id) },
            onAddToPlaylist = { 
                showActionsSheet = false
                showAddToPlaylistSheet = true 
            },
            onViewDetails = { 
                showActionsSheet = false
                showDetailsDialog = true 
            },
            onDelete = { 
                showActionsSheet = false
                showDeleteDialog = true 
            }
        )
    }
    
    // Bulk actions bottom sheet
    if (showBulkActionsSheet) {
        BulkActionsBottomSheet(
            selectedCount = selectionState.selectedCount,
            onDismiss = { showBulkActionsSheet = false },
            onAddToQueue = {
                val selectedSongs = songs.filter { it.id in selectionState.selectedIds }
                viewModel.addToQueue(selectedSongs)
                selectionState.clearSelection()
            },
            onAddToFavorites = {
                viewModel.addToFavorites(selectionState.selectedIds.toList())
                selectionState.clearSelection()
            },
            onAddToPlaylist = {
                showBulkActionsSheet = false
                showAddToPlaylistSheet = true
            },
            onDelete = {
                showBulkActionsSheet = false
                showDeleteDialog = true
            }
        )
    }
    
    // Add to playlist bottom sheet
    if (showAddToPlaylistSheet) {
        AddToPlaylistBottomSheet(
            playlists = playlists,
            onDismiss = { showAddToPlaylistSheet = false },
            onPlaylistSelected = { playlistId ->
                if (selectionState.isSelectionMode) {
                    viewModel.addToPlaylist(playlistId, selectionState.selectedIds.toList())
                    selectionState.clearSelection()
                } else {
                    selectedSong?.let { viewModel.addToPlaylist(playlistId, it.id) }
                }
            },
            onCreateNew = { showCreatePlaylistDialog = true }
        )
    }
    
    // Create playlist dialog
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onCreate = { name ->
                viewModel.createPlaylist(name)
            }
        )
    }
    
    // Song details dialog
    if (showDetailsDialog && selectedSong != null) {
        SongDetailsDialog(
            song = selectedSong!!,
            onDismiss = { showDetailsDialog = false }
        )
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        val count = if (selectionState.isSelectionMode) selectionState.selectedCount else 1
        DeleteConfirmDialog(
            songCount = count,
            onConfirm = {
                // TODO: Implement actual file deletion
                selectionState.clearSelection()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun SelectionTopBar(
    selectedCount: Int,
    onClose: () -> Unit,
    onSelectAll: () -> Unit,
    onActions: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close selection")
            }
            Text(
                text = "$selectedCount selected",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onSelectAll) {
                Icon(Icons.Default.SelectAll, contentDescription = "Select all")
            }
            IconButton(onClick = onActions) {
                Icon(Icons.Default.MoreVert, contentDescription = "Actions")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongListItem(
    song: Song,
    isFavorite: Boolean = false,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier.combinedClickable(
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
                AlbumArtImage(
                    uri = song.albumArtUri,
                    size = 48.dp
                )
            }
        },
        trailingContent = {
            Row {
                if (isFavorite && !isSelectionMode) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                if (!isSelectionMode) {
                    IconButton(onClick = onMoreClick) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                }
            }
        }
    )
}

