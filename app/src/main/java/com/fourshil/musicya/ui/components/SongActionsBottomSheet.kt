package com.fourshil.musicya.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.data.model.Song

/**
 * Bottom sheet with song action options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongActionsBottomSheet(
    song: Song,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onViewDetails: () -> Unit,
    onDelete: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Song header
            ListItem(
                headlineContent = {
                    Text(
                        text = song.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                supportingContent = {
                    Text(
                        text = "${song.artist} • ${song.album}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Actions
            ActionItem(
                icon = Icons.Default.PlaylistPlay,
                text = "Play Next",
                onClick = {
                    onPlayNext()
                    onDismiss()
                }
            )
            
            ActionItem(
                icon = Icons.Default.QueueMusic,
                text = "Add to Queue",
                onClick = {
                    onAddToQueue()
                    onDismiss()
                }
            )
            
            ActionItem(
                icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                text = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                onClick = {
                    onToggleFavorite()
                    onDismiss()
                }
            )
            
            ActionItem(
                icon = Icons.Default.PlaylistAdd,
                text = "Add to Playlist",
                onClick = {
                    onAddToPlaylist()
                    onDismiss()
                }
            )
            
            ActionItem(
                icon = Icons.Default.Info,
                text = "Details",
                onClick = {
                    onViewDetails()
                    onDismiss()
                }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            ActionItem(
                icon = Icons.Default.Delete,
                text = "Delete",
                tint = MaterialTheme.colorScheme.error,
                onClick = {
                    onDelete()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun ActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = {
            Text(text = text, color = tint)
        },
        leadingContent = {
            Icon(icon, contentDescription = null, tint = tint)
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Bulk actions bottom sheet for multiple selected songs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkActionsBottomSheet(
    selectedCount: Int,
    onDismiss: () -> Unit,
    onAddToQueue: () -> Unit,
    onAddToFavorites: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onDelete: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Header
            ListItem(
                headlineContent = {
                    Text(
                        text = "$selectedCount songs selected",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Bulk Actions
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        onAddToQueue()
                        onDismiss()
                    }),
                headlineContent = { Text("Add to Queue") },
                leadingContent = { Icon(Icons.Default.QueueMusic, null) }
            )
            
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        onAddToFavorites()
                        onDismiss()
                    }),
                headlineContent = { Text("Add to Favorites") },
                leadingContent = { Icon(Icons.Default.Favorite, null) }
            )
            
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        onAddToPlaylist()
                        onDismiss()
                    }),
                headlineContent = { Text("Add to Playlist") },
                leadingContent = { Icon(Icons.Default.PlaylistAdd, null) }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        onDelete()
                        onDismiss()
                    }),
                headlineContent = { 
                    Text("Delete", color = MaterialTheme.colorScheme.error) 
                },
                leadingContent = { 
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) 
                }
            )
        }
    }
}
