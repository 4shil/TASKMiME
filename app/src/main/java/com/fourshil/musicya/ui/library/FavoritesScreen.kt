package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.AddToPlaylistBottomSheet
import com.fourshil.musicya.ui.components.CreatePlaylistDialog
import com.fourshil.musicya.ui.components.SongActionsBottomSheet
import com.fourshil.musicya.ui.components.SongDetailsDialog
import com.fourshil.musicya.ui.components.SongListItem
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.NeoDimens

import com.fourshil.musicya.ui.components.TopNavItem
import com.fourshil.musicya.ui.components.TopNavigationChips
import com.fourshil.musicya.ui.navigation.Screen
import com.fourshil.musicya.ui.navigation.NavigationUtils

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    currentRoute: String? = null,
    onNavigate: (String) -> Unit = {}
) {
    val songs by viewModel.favoriteSongs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    
    // Dialog states
    var showActionsSheet by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showAddToPlaylistSheet by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            top = 0.dp,
            bottom = NeoDimens.ListBottomPadding
        )
    ) {

        if (isLoading) {
             item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                }
             }
        } else if (songs.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "No favorites yet", 
                        style = MaterialTheme.typography.bodyLarge, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            itemsIndexed(
                items = songs,
                key = { _, song -> song.id },
                contentType = { _, _ -> "favorite_item" }
            ) { index, song ->
                Box(modifier = Modifier.padding(horizontal = NeoDimens.ScreenPadding, vertical = 6.dp)) {
                    SongListItem(
                        song = song,
                        isFavorite = true,
                        isSelected = false,
                        isSelectionMode = false,
                        isScrolling = isScrolling,
                        onClick = { viewModel.playSongAt(index) },
                        onLongClick = { },
                        onMoreClick = {
                            selectedSong = song
                            showActionsSheet = true
                        }
                    )
                }
            }
        }
    }
    
     // Song actions
    if (showActionsSheet && selectedSong != null) {
        SongActionsBottomSheet(
            song = selectedSong!!,
            isFavorite = true,
            onDismiss = { showActionsSheet = false },
            onPlayNext = { viewModel.playNext(selectedSong!!) },
            onAddToQueue = { viewModel.addToQueue(selectedSong!!) },
            onToggleFavorite = { viewModel.toggleFavorite(selectedSong!!.id) }, 
            onAddToPlaylist = { 
                showActionsSheet = false
                showAddToPlaylistSheet = true 
            },
            onViewDetails = { showDetailsDialog = true },
            onDelete = { /* Favorites doesn't delete file usually */ }
        )
    }
    
    if (showDetailsDialog && selectedSong != null) {
         SongDetailsDialog(
            song = selectedSong!!,
            onDismiss = { showDetailsDialog = false }
        )
    }
    
    // Add to Playlist Sheet
    if (showAddToPlaylistSheet) {
        AddToPlaylistBottomSheet(
            playlists = playlists,
            onDismiss = { showAddToPlaylistSheet = false },
            onPlaylistSelected = { playlistId ->
                selectedSong?.let { viewModel.addToPlaylist(playlistId, it.id) }
                showAddToPlaylistSheet = false
            },
            onCreateNew = { 
                showAddToPlaylistSheet = false
                showCreatePlaylistDialog = true 
            }
        )
    }
    
    // Create Playlist Dialog
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onCreate = { name ->
                viewModel.createPlaylist(name)
                showCreatePlaylistDialog = false
            }
        )
    }
}

