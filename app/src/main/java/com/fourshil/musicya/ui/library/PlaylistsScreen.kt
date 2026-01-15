package com.fourshil.musicya.ui.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.db.Playlist
import com.fourshil.musicya.ui.components.CreatePlaylistDialog
import com.fourshil.musicya.ui.components.PlaylistArtGrid

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModel = hiltViewModel(),
    onPlaylistClick: (Long) -> Unit = {}
) {
    val playlists by viewModel.playlists.collectAsState()
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Playlist?>(null) }
    var showRenameDialog by remember { mutableStateOf<Playlist?>(null) }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Playlist")
            }
        }
    ) { padding ->
        if (playlists.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.QueueMusic,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No playlists yet", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tap + to create a playlist",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(playlists, key = { it.id }) { playlist ->
                    val playlistSongs by viewModel.getPlaylistSongs(playlist.id)
                        .collectAsState(initial = emptyList())
                    
                    PlaylistItem(
                        playlist = playlist,
                        songCount = playlistSongs.size,
                        artUris = playlistSongs.map { it.albumArtUri },
                        onClick = { onPlaylistClick(playlist.id) },
                        onLongClick = { showDeleteDialog = playlist },
                        onRename = { showRenameDialog = playlist }
                    )
                }
            }
        }
    }
    
    // Create playlist dialog
    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                viewModel.createPlaylist(name)
            }
        )
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { playlist ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Playlist") },
            text = { Text("Delete \"${playlist.name}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePlaylist(playlist.id)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Rename dialog
    showRenameDialog?.let { playlist ->
        var newName by remember { mutableStateOf(playlist.name) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text("Rename Playlist") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Playlist Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.renamePlaylist(playlist.id, newName.trim())
                            showRenameDialog = null
                        }
                    },
                    enabled = newName.isNotBlank()
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlaylistItem(
    playlist: Playlist,
    songCount: Int,
    artUris: List<android.net.Uri>,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRename: () -> Unit
) {
    ListItem(
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        ),
        headlineContent = { Text(playlist.name) },
        supportingContent = {
            Text(
                text = "$songCount songs",
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            PlaylistArtGrid(
                uris = artUris,
                size = 48.dp
            )
        },
        trailingContent = {
            IconButton(onClick = onRename) {
                Icon(Icons.Default.Edit, contentDescription = "Rename")
            }
        }
    )
}
