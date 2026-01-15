package com.fourshil.musicya.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Folder
import com.fourshil.musicya.ui.components.PlaylistArtGrid

@Composable
fun FoldersScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onFolderClick: (String) -> Unit = {}
) {
    val folders by viewModel.folders.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (folders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No folders found", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(folders, key = { it.path }) { folder ->
                // Find songs in this folder for the artwork grid
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

@Composable
fun FolderListItem(
    folder: Folder,
    artUris: List<android.net.Uri>,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(text = folder.name)
        },
        supportingContent = {
            Text(
                text = "${folder.songCount} songs",
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
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
