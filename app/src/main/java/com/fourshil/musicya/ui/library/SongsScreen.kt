package com.fourshil.musicya.ui.library

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.*
import com.fourshil.musicya.ui.theme.NeoDimens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.itemContentType

/**
 * Clean Minimalistic Songs Screen
 * With paging support and selection mode
 */
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
    
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            viewModel.refresh()
            pagedSongs.refresh()
        } else {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    NeoScaffold(
        containerColor = NeoBackground,
        topBar = {
            if (selectionState.isSelectionMode) {
                val scope = rememberCoroutineScope()
                // Selection mode header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = NeoDimens.ScreenPadding)
                        .padding(top = NeoDimens.SpacingM)
                ) {
                    NeoCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = NeoGreen, // Selection Green
                        shadowSize = 4.dp,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                IconButton(onClick = { selectionState.clearSelection() }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Cancel selection",
                                        tint = Color.Black
                                    )
                                }
                                Text(
                                    "${selectionState.selectedCount} SELECTED",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                            Row {
                                IconButton(onClick = {
                                    scope.launch {
                                        val allIds = viewModel.getAllSongIds()
                                        selectionState.selectAll(allIds)
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.SelectAll,
                                        contentDescription = "Select all",
                                        tint = Color.Black
                                    )
                                }
                                IconButton(onClick = { showBulkActionsSheet = true }) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "More actions",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (selectionState.isSelectionMode) Modifier.padding(padding)
                    else Modifier
                )
        ) {
            when {
                !permissionsState.allPermissionsGranted -> {
                    // Permission required state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingL)
                        ) {
                            Text(
                                "Permission Required",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Please grant access to your music files",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                                Text("Grant Permission")
                            }
                        }
                    }
                }
                
                pagedSongs.loadState.refresh is LoadState.Loading -> {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                
                pagedSongs.itemCount == 0 && pagedSongs.loadState.refresh !is LoadState.Loading -> {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingM)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                "No songs found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                else -> {
                    // Song list
                    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
                    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(
                            top = 0.dp,
                            bottom = NeoDimens.ListBottomPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingXS)
                    ) {
                        items(
                            count = pagedSongs.itemCount,
                            key = pagedSongs.itemKey { it.id },
                            contentType = pagedSongs.itemContentType { "song" }
                        ) { index ->
                            val song = pagedSongs[index]
                            
                            if (song != null) {
                                val isSelected = selectionState.isSelected(song.id)
                                
                                SongListItem(
                                    song = song,
                                    isSelected = isSelected,
                                    inSelectionMode = selectionState.isSelectionMode,
                                    isScrolling = isScrolling,
                                    onClick = {
                                        if (selectionState.isSelectionMode) {
                                            selectionState.toggleSelection(song.id)
                                        } else {
                                            // Collect all currently loaded songs for the queue
                                            val allVisibleSongs = (0 until pagedSongs.itemCount).mapNotNull { pagedSongs[it] }
                                            viewModel.playSongWithQueue(song, allVisibleSongs)
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
    
    // Bottom Sheets and Dialogs
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
        val idsToDelete = if (selectionState.isSelectionMode) {
            selectionState.selectedIds.toList()
        } else {
            selectedSong?.let { listOf(it.id) } ?: emptyList()
        }
        
        DeleteConfirmDialog(
            songCount = count,
            onConfirm = {
                viewModel.deleteSongs(idsToDelete)
                selectionState.clearSelection()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}
