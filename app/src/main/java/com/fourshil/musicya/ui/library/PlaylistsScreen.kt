package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.db.Playlist
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.components.CreatePlaylistDialog
import com.fourshil.musicya.ui.components.PlaylistArtGrid
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.NeoAmber
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.Slate50
import com.fourshil.musicya.ui.theme.Slate900

import com.fourshil.musicya.ui.components.TopNavItem
import com.fourshil.musicya.ui.components.TopNavigationChips
import com.fourshil.musicya.ui.navigation.Screen
import com.fourshil.musicya.ui.navigation.NavigationUtils

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModel = hiltViewModel(),
    onPlaylistClick: (Long) -> Unit = {},
    currentRoute: String? = null,
    onNavigate: (String) -> Unit = {}
) {
    val playlists by viewModel.playlists.collectAsState()
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Playlist?>(null) }
    var showRenameDialog by remember { mutableStateOf<Playlist?>(null) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = NeoDimens.ListBottomPadding)
    ) {
        item {
             Column(modifier = Modifier.padding(horizontal = NeoDimens.ScreenPadding)) {
                Spacer(modifier = Modifier.height(NeoDimens.HeaderHeight))
             }
        }

        if (playlists.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "No playlists yet", 
                        style = MaterialTheme.typography.bodyLarge, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(
                items = playlists,
                key = { it.id },
                contentType = { "playlist_item" }
            ) { playlist ->
                Box(modifier = Modifier.padding(horizontal = NeoDimens.ScreenPadding, vertical = 6.dp)) {
                    val playlistSongs by viewModel.getPlaylistSongs(playlist.id)
                        .collectAsState(initial = emptyList())
                    
                    PlaylistArtisticItem(
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
    
    // Dialogs (reuse existing or style them? For now reuse standard ones but wrapped)
    // Ideally we should create "ArtisticDialog" but standard M3 dialogs are acceptable for now given time.
    
    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name -> viewModel.createPlaylist(name) }
        )
    }
    
    showDeleteDialog?.let { playlist ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("DELETE ASSET?", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("This will permanently remove '${playlist.name}' from the archive.", color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePlaylist(playlist.id)
                        showDeleteDialog = null
                    }
                ) { Text("Delete", color = NeoCoral, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                 TextButton(onClick = { showDeleteDialog = null }) { Text("CANCEL", color = MaterialTheme.colorScheme.onSurface) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.small
        )
    }

    // Rename Dialog (similar to delete)
     showRenameDialog?.let { playlist ->
        var newName by remember { mutableStateOf(playlist.name) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text("RENAME ASSET", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = NeoCoral,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            confirmButton = {
                 TextButton(
                    onClick = {
                        viewModel.renamePlaylist(playlist.id, newName)
                        showRenameDialog = null
                    }
                ) { Text("SAVE", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) }
            },
             dismissButton = {
                  TextButton(onClick = { showRenameDialog = null }) { Text("CANCEL", color = MaterialTheme.colorScheme.onSurface) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.small
        )
    }
}

@Composable
fun PlaylistArtisticItem(
     playlist: Playlist,
    songCount: Int,
    artUris: List<android.net.Uri>,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRename: () -> Unit
) {
    ArtisticCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        showHalftone = false // Performance optimization
    ) {
         Row(
            modifier = Modifier.padding(NeoDimens.SpacingL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .border(NeoDimens.BorderThin, MaterialTheme.colorScheme.outline)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (artUris.isNotEmpty()) {
                    PlaylistArtGrid(uris = artUris, size = 56.dp)
                } else {
                    Icon(Icons.Default.Folder, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.width(NeoDimens.SpacingL))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface // Added explicit color
                )
                Text(
                    text = "$songCount songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onRename) {
                Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
