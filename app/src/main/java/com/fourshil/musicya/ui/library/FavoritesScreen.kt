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
import com.fourshil.musicya.ui.components.SongActionsBottomSheet
import com.fourshil.musicya.ui.components.SongDetailsDialog
import com.fourshil.musicya.ui.components.SongListItem
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack

import com.fourshil.musicya.ui.components.TopNavItem
import com.fourshil.musicya.ui.components.TopNavigationChips
import com.fourshil.musicya.ui.navigation.Screen

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    currentRoute: String? = null,
    onNavigate: (String) -> Unit = {}
) {
    val songs by viewModel.favoriteSongs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Dialog states
    var showActionsSheet by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 160.dp)
    ) {
        item {
             Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Header: HEARTS
                Text(
                    text = "YOUR",
                     style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "HEARTS",
                     style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = MangaRed
                    ),
                     modifier = Modifier.padding(bottom = 32.dp)
                )

                TopNavigationChips(
                    items = listOf(
                        TopNavItem(Screen.Songs.route, "Gallery"),
                        TopNavItem(Screen.Favorites.route, "Hearts"),
                        TopNavItem(Screen.Folders.route, "Files"),
                        TopNavItem(Screen.Playlists.route, "Assets"),
                        TopNavItem(Screen.Albums.route, "Ink"),
                        TopNavItem(Screen.Artists.route, "Muses")
                    ),
                    currentRoute = currentRoute,
                    onItemClick = onNavigate,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
             }
        }

        if (isLoading) {
             item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                }
             }
        } else if (songs.isEmpty()) {
             item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                     Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NO LOVE YET", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
                     }
                }
             }
        } else {
             itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                     SongListItem(
                        song = song,
                        isFavorite = true,
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
        SongActionsBottomSheet(
            song = selectedSong!!,
            isFavorite = true,
            onDismiss = { showActionsSheet = false },
            onPlayNext = { viewModel.playNext(selectedSong!!) },
            onAddToQueue = { viewModel.addToQueue(selectedSong!!) },
            onToggleFavorite = { viewModel.toggleFavorite(selectedSong!!.id) }, 
            onAddToPlaylist = { /* Todo */ },
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
}
