package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Folder
import com.fourshil.musicya.ui.components.PlaylistArtGrid
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Clean Minimalistic Folders Screen
 */
@Composable
fun FoldersScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onFolderClick: (String) -> Unit = {},
    currentRoute: String? = null,
    onNavigate: (String) -> Unit = {}
) {
    val folders by viewModel.folders.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            top = NeoDimens.HeaderHeight,
            bottom = NeoDimens.ListBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingXS)
    ) {
        when {
            isLoading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            folders.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingM)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                "No folders found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            else -> {
                items(
                    items = folders,
                    key = { it.path },
                    contentType = { "folder_item" }
                ) { folder ->
                    val folderSongs = songs.filter { 
                        val parent = java.io.File(it.path).parent ?: ""
                        parent == folder.path 
                    }
                    
                    FolderListItem(
                        folder = folder,
                        artUris = folderSongs.map { it.albumArtUri },
                        onClick = { onFolderClick(folder.path) }
                    )
                }
            }
        }
    }
}

/**
 * Clean Folder List Item
 */
@Composable
private fun FolderListItem(
    folder: Folder,
    artUris: List<android.net.Uri>,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = NeoDimens.ScreenPadding, vertical = NeoDimens.SpacingXS),
        shape = RoundedCornerShape(NeoDimens.CornerMedium),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = NeoDimens.ElevationLow
    ) {
        Row(
            modifier = Modifier.padding(NeoDimens.SpacingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Folder icon or art grid
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(NeoDimens.CornerSmall))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (artUris.isNotEmpty()) {
                    PlaylistArtGrid(uris = artUris.take(4), size = 48.dp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(NeoDimens.SpacingM))

            // Folder info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "${folder.songCount} songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(NeoDimens.IconMedium)
            )
        }
    }
}
