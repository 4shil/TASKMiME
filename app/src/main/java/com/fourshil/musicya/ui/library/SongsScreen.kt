package com.fourshil.musicya.ui.library

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.*
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.Slate50
import com.fourshil.musicya.ui.theme.Slate700
import com.fourshil.musicya.ui.theme.Slate900
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.fourshil.musicya.ui.components.TopNavItem
import com.fourshil.musicya.ui.components.TopNavigationChips
import com.fourshil.musicya.ui.navigation.Screen

import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.itemContentType

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onSongClick: (Int) -> Unit = {},
    onMenuClick: () -> Unit = {},
    currentRoute: String? = null,
    onNavigate: (String) -> Unit = {}
) {
    // Collect Paging Data
    val pagedSongs = viewModel.pagedSongs.collectAsLazyPagingItems()
    
    // Keep full list for logic (Selection, Player)
    val fullSongs by viewModel.songs.collectAsState()
    
    val isLoading by viewModel.isLoading.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    
    val selectionState = rememberSelectionState()
    
    var showActionsSheet by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showAddToPlaylistSheet by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBulkActionsSheet by remember { mutableStateOf(false) }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    
    val permissionsState = rememberMultiplePermissionsState(permissions)
    
    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
    
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            viewModel.loadLibrary()
        }
    }

    // Force load on entry if permission is granted but list is empty
    LaunchedEffect(Unit) {
        if (permissionsState.allPermissionsGranted && fullSongs.isEmpty()) {
            viewModel.loadLibrary()
        }
    }
    
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            if (selectionState.isSelectionMode) {
                SelectionTopBar(
                    selectedCount = selectionState.selectedCount,
                    onClose = { selectionState.clearSelection() },
                    onSelectAll = { selectionState.selectAll(fullSongs.map { it.id }) },
                    onActions = { showBulkActionsSheet = true }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
                
            when {
                !permissionsState.allPermissionsGranted -> {
                    PermissionRequiredView { permissionsState.launchMultiplePermissionRequest() }
                }
                // Initial Loading of Pager
                pagedSongs.loadState.refresh is LoadState.Loading -> {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                pagedSongs.itemCount == 0 && pagedSongs.loadState.refresh !is LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("NULL ARCHIVE", style = MaterialTheme.typography.headlineLarge)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 160.dp), // Space for bottom bar + mini player
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!selectionState.isSelectionMode) {
                              item {
                                   Spacer(modifier = Modifier.height(262.dp))
                              }
                        }

                        items(
                            count = pagedSongs.itemCount,
                            key = pagedSongs.itemKey { it.id },
                            contentType = pagedSongs.itemContentType { "song" }
                        ) { index ->
                            val song = pagedSongs[index]
                            
                            if (song != null) {
                                val isFavorite = song.id in favoriteIds
                                val isSelected = selectionState.isSelected(song.id)
                                
                                SongListItem(
                                    song = song,
                                    isFavorite = isFavorite,
                                    isSelected = isSelected,
                                    isSelectionMode = selectionState.isSelectionMode,
                                    onClick = {
                                        if (selectionState.isSelectionMode) {
                                            selectionState.toggleSelection(song.id)
                                        } else {
                                            // Play using global index (assuming sort order consistency)
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
            }
        }
    }
    
    // Bottom Sheets (Keeping logic same, just assuming they will overlay)
    // For full redesign, sheets should also be styled, but prioritized screens first.
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
    
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onCreate = { name ->
                viewModel.createPlaylist(name)
            }
        )
    }
    
    if (showDetailsDialog && selectedSong != null) {
        SongDetailsDialog(
            song = selectedSong!!,
            onDismiss = { showDetailsDialog = false }
        )
    }
    
    if (showDeleteDialog) {
        val count = if (selectionState.isSelectionMode) selectionState.selectedCount else 1
        DeleteConfirmDialog(
            songCount = count,
            onConfirm = {
                selectionState.clearSelection()
                // viewmodel delete logic
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}


