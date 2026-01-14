package com.fourshil.musicya.ui.library

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.fourshil.musicya.data.model.Song

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = viewModel(),
    onSongClick: (Song) -> Unit,
    onBack: () -> Unit
) {
    val permissionString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    val permissionState = rememberPermissionState(permissionString)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        } else {
            viewModel.loadLibrary()
        }
    }
    
    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            viewModel.loadLibrary()
        }
    }

    val folders by viewModel.folders.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Text(
            "Library",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(24.dp)
        )

        if (!permissionState.status.isGranted) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("Grant Permission to scan music")
                }
            }
        } else {
            LazyColumn {
                folders.forEach { (folderName, songs) ->
                    item {
                        FolderHeader(folderName, songs.size)
                    }
                    items(songs) { song ->
                        SongItem(song, onClick = { onSongClick(song) })
                    }
                }
            }
        }
    }
}

@Composable
fun FolderHeader(name: String, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha=0.1f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(Icons.Rounded.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$count songs",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun SongItem(song: Song, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(song.title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            Text(song.artist, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
