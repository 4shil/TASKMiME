package com.fourshil.musicya.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.SongActionsBottomSheet
import com.fourshil.musicya.ui.components.SongDetailsDialog
import com.fourshil.musicya.ui.components.SongListItem
import com.fourshil.musicya.ui.components.TopNavItem
import com.fourshil.musicya.ui.components.TopNavigationChips
import com.fourshil.musicya.ui.navigation.Screen
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.navigation.NavigationUtils

@Composable
fun RecentlyPlayedScreen(
    viewModel: RecentlyPlayedViewModel = hiltViewModel(),
    currentRoute: String? = null,
    onNavigate: (String) -> Unit = {}
) {
    val songs by viewModel.recentSongs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    
    // Dialog states
    var showActionsSheet by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
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
                        "No recently played songs", 
                        style = MaterialTheme.typography.bodyLarge, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            itemsIndexed(
                items = songs,
                key = { index, song -> "${song.id}_$index" },
                contentType = { _, _ -> "recent_item" }
            ) { index, song ->
                val isFavorite = song.id in favoriteIds
                Box(modifier = Modifier.padding(horizontal = NeoDimens.ScreenPadding, vertical = 6.dp)) {
                    SongListItem(
                        song = song,
                        isFavorite = isFavorite,
                        isSelected = false,
                        isSelectionMode = false,
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
        val isFavorite = selectedSong!!.id in favoriteIds
        SongActionsBottomSheet(
            song = selectedSong!!,
            isFavorite = isFavorite,
            onDismiss = { showActionsSheet = false },
            onPlayNext = { viewModel.playNext(selectedSong!!) },
            onAddToQueue = { viewModel.addToQueue(selectedSong!!) },
            onToggleFavorite = { viewModel.toggleFavorite(selectedSong!!.id) }, 
            onAddToPlaylist = { /* Todo */ },
            onViewDetails = { showDetailsDialog = true },
            onDelete = { }
        )
    }
    
    if (showDetailsDialog && selectedSong != null) {
        SongDetailsDialog(
            song = selectedSong!!,
            onDismiss = { showDetailsDialog = false }
        )
    }
}
