package com.fourshil.musicya.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import com.fourshil.musicya.data.db.Playlist
import com.fourshil.musicya.ui.theme.Slate50
import com.fourshil.musicya.ui.theme.Slate900
import com.fourshil.musicya.ui.theme.NeoPink

/**
 * Bottom sheet for adding song(s) to a playlist.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistBottomSheet(
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (Long) -> Unit,
    onCreateNew: () -> Unit
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
            Text(
                text = "Add to Playlist",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            
            // Create new playlist option
            ListItem(
                modifier = Modifier.clickable { 
                    onCreateNew()
                    onDismiss()
                },
                headlineContent = { Text("Create New Playlist") },
                leadingContent = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            
            HorizontalDivider()
            
            // Existing playlists
            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No playlists yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(playlists) { playlist ->
                        ListItem(
                            modifier = Modifier.clickable {
                                onPlaylistSelected(playlist.id)
                                onDismiss()
                            },
                            headlineContent = { Text(playlist.name) },
                            leadingContent = {
                                Icon(Icons.Default.QueueMusic, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog for creating a new playlist.
 */
@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    val contentColor = MaterialTheme.colorScheme.onSurface
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    NeoDialogWrapper(
        title = "NEW PLAYLIST",
        onDismiss = onDismiss,
        contentColor = contentColor,
        surfaceColor = surfaceColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                label = { Text("Playlist Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = contentColor,
                    unfocusedTextColor = contentColor,
                    focusedBorderColor = contentColor,
                    unfocusedBorderColor = contentColor.copy(alpha = 0.5f),
                    focusedLabelColor = contentColor,
                    unfocusedLabelColor = contentColor.copy(alpha = 0.5f),
                    cursorColor = contentColor
                )
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoButton(
                    onClick = onDismiss,
                    backgroundColor = surfaceColor,
                    modifier = Modifier.weight(1f).height(56.dp),
                    borderWidth = 2.dp,
                    shadowSize = 2.dp
                ) {
                    Text(
                        text = "CANCEL",
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
                
                NeoButton(
                    onClick = { 
                        if (playlistName.isNotBlank()) {
                            onCreate(playlistName.trim())
                            onDismiss()
                        }
                    },
                    backgroundColor = contentColor,
                    modifier = Modifier.weight(1f).height(56.dp),
                    borderWidth = 2.dp,
                    shadowSize = 4.dp
                ) {
                    Text(
                         text = "CREATE",
                         fontWeight = FontWeight.Black,
                         color = surfaceColor,
                         letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

/**
 * Dialog to confirm song deletion.
 */
@Composable
fun DeleteConfirmDialog(
    songCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onSurface
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    NeoDialogWrapper(
        title = "DELETE SONGS?",
        onDismiss = onDismiss,
        contentColor = contentColor,
        surfaceColor = surfaceColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "This will permanently delete ${if (songCount == 1) "this song" else "these songs"} from your device. This action cannot be undone.",
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoButton(
                    onClick = onDismiss,
                    backgroundColor = surfaceColor,
                    modifier = Modifier.weight(1f).height(56.dp),
                    borderWidth = 2.dp,
                    shadowSize = 2.dp
                ) {
                    Text(
                        text = "CANCEL",
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
                
                NeoButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    backgroundColor = MaterialTheme.colorScheme.error, // Use Error color
                    modifier = Modifier.weight(1f).height(56.dp),
                    borderWidth = 2.dp,
                    shadowSize = 4.dp
                ) {
                    Text("DELETE", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
}
