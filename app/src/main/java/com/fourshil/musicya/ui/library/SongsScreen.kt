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
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack
import com.fourshil.musicya.ui.theme.PureWhite
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onSongClick: (Int) -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val songs by viewModel.songs.collectAsState()
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
    
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            if (selectionState.isSelectionMode) {
                SelectionTopBar(
                    selectedCount = selectionState.selectedCount,
                    onClose = { selectionState.clearSelection() },
                    onSelectAll = { selectionState.selectAll(songs.map { it.id }) },
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
            if (!selectionState.isSelectionMode) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Header: STUDIO FEED
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box {
                         // Tag "ART-V02"
                        Box(
                            modifier = Modifier
                                .offset(x = 60.dp, y = (-12).dp)
                                .rotate(-8f)
                                .background(PureBlack)
                                .border(2.dp, PureWhite)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "ART-V02",
                                color = PureWhite,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                            )
                        }

                        Column {
                            Text(
                                text = "STUDIO",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 48.sp,
                                    lineHeight = 40.sp,
                                    fontWeight = FontWeight.Black
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "FEED",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 48.sp,
                                    lineHeight = 40.sp,
                                    fontWeight = FontWeight.Black
                                ),
                                color = MangaRed
                            )
                        }
                    }

                    // Simple Menu Logic
                     ArtisticButton(
                        onClick = onMenuClick,
                        icon = { Icon(Icons.Default.Menu, null) },
                        modifier = Modifier.size(56.dp)
                     )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Artistic Search Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(4.dp, PureBlack)
                        .background(PureWhite)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, tint = MangaRed, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "SEARCH THE ARCHIVE...",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = PureBlack.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            when {
                !permissionsState.allPermissionsGranted -> {
                    PermissionRequiredView { permissionsState.launchMultiplePermissionRequest() }
                }
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PureBlack)
                    }
                }
                songs.isEmpty() -> {
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
                        itemsIndexed(
                            items = songs,
                            key = { _, song -> song.id },
                            contentType = { _, _ -> "song" }
                        ) { index, song ->
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

@Composable
private fun PermissionRequiredView(onRequest: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("LOCKED", style = MaterialTheme.typography.displayMedium)
             Spacer(modifier = Modifier.height(16.dp))
            ArtisticButton(
                text = "ACCESS DATA",
                onClick = onRequest
            )
        }
    }
}

@Composable
private fun SelectionTopBar(
    selectedCount: Int,
    onClose: () -> Unit,
    onSelectAll: () -> Unit,
    onActions: () -> Unit
) {
    // Floating style top bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ArtisticCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MangaRed,
            borderColor = PureBlack
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, null, tint = PureWhite)
                }
                Text(
                    text = "$selectedCount CHOSEN",
                    style = MaterialTheme.typography.titleLarge,
                    color = PureWhite
                )
                 Row {
                    IconButton(onClick = onSelectAll) {
                        Icon(Icons.Default.SelectAll, null, tint = PureWhite)
                    }
                    IconButton(onClick = onActions) {
                        Icon(Icons.Default.MoreVert, null, tint = PureWhite)
                    }
                 }
            }
        }
    }
}
