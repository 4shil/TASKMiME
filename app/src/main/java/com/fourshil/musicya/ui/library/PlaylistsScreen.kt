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
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.MangaYellow
import com.fourshil.musicya.ui.theme.PureBlack

import com.fourshil.musicya.ui.components.TopNavItem
import com.fourshil.musicya.ui.components.TopNavigationChips
import com.fourshil.musicya.ui.navigation.Screen

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
        contentPadding = PaddingValues(bottom = 160.dp)
    ) {
        item {
             Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Header: PLAYLISTS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     Text(
                        text = "PLAYLISTS",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = (-2).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    // Add Button
                    ArtisticButton(
                        onClick = { showCreateDialog = true },
                        icon = { Icon(Icons.Default.Add, null, tint = PureBlack) },
                        modifier = Modifier.size(56.dp),
                        backgroundColor = MangaYellow
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
             }

             TopNavigationChips(
                items = listOf(
                    TopNavItem(Screen.Songs.route, "Songs"),
                    TopNavItem(Screen.Favorites.route, "Favorites"),
                    TopNavItem(Screen.Folders.route, "Folders"),
                    TopNavItem(Screen.Playlists.route, "Playlists"),
                    TopNavItem(Screen.Albums.route, "Albums"),
                    TopNavItem(Screen.Artists.route, "Artists")
                ),
                currentRoute = currentRoute,
                onItemClick = onNavigate,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        if (playlists.isEmpty()) {
             item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                     Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("EMPTY ARCHIVE", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("create new asset +", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                     }
                }
             }
        } else {
             items(
                 items = playlists,
                 key = { it.id },
                 contentType = { "playlist_item" }
             ) { playlist ->
                 Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
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
                ) { Text("CONFIRM", color = MangaRed, fontWeight = FontWeight.Bold) }
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
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f),
                        cursorColor = MangaRed,
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
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Folder Icon or Art
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .border(3.dp, PureBlack)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                 if (artUris.isNotEmpty()) {
                     PlaylistArtGrid(uris = artUris, size = 56.dp)
                 } else {
                     Icon(Icons.Default.Folder, null, tint = PureBlack)
                 }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                 Text(
                    text = playlist.name.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    maxLines = 1
                 )
                 Text(
                    text = "$songCount ITEMS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = PureBlack.copy(alpha=0.6f)
                 )
            }
            
            IconButton(onClick = onRename) {
                Icon(Icons.Default.Edit, null, tint = PureBlack)
            }
        }
    }
}
