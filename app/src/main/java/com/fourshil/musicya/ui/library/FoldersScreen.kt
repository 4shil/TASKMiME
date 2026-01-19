package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Folder
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.components.PlaylistArtGrid
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack

import com.fourshil.musicya.ui.components.TopNavItem
import com.fourshil.musicya.ui.components.TopNavigationChips
import com.fourshil.musicya.ui.navigation.Screen
import com.fourshil.musicya.ui.navigation.NavigationUtils

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
        contentPadding = PaddingValues(bottom = NeoDimens.ListBottomPadding)
    ) {
        item {
             Spacer(modifier = Modifier.height(NeoDimens.HeaderHeight))
        }

        if (isLoading) {
             item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                }
             }
        } else if (folders.isEmpty()) {
             item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("EMPTY DRIVE", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
                }
             }
        } else {
            items(
                items = folders,
                key = { it.path },
                contentType = { "folder_item" }
            ) { folder ->
                // Find songs in this folder for the artwork grid
                val folderSongs = songs.filter { 
                    val parent = java.io.File(it.path).parent ?: ""
                    parent == folder.path 
                }
                
                Box(modifier = Modifier.padding(horizontal = NeoDimens.ScreenPadding, vertical = NeoDimens.SpacingS)) {
                    FolderArtisticItem(
                        folder = folder,
                        artUris = folderSongs.map { it.albumArtUri },
                        onClick = { onFolderClick(folder.path) }
                    )
                }
            }
        }
    }
}

@Composable
fun FolderArtisticItem(
    folder: Folder,
    artUris: List<android.net.Uri>,
    onClick: () -> Unit
) {
    ArtisticCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        showHalftone = false // Performance optimization
    ) {
         Row(
            modifier = Modifier.padding(NeoDimens.SpacingL),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Box(
                modifier = Modifier.size(56.dp).background(Color.Transparent),
                 contentAlignment = Alignment.Center
             ) {
                 if (artUris.isNotEmpty()) {
                     PlaylistArtGrid(uris = artUris, size = 56.dp)
                 } else {
                     Icon(Icons.Default.Folder, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(NeoDimens.IconLarge))
                 }
             }

             Spacer(modifier = Modifier.width(NeoDimens.SpacingL))

             Column(modifier = Modifier.weight(1f)) {
                 Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                 )
                 Text(
                    text = "${folder.songCount} FILES",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f)
                 )
             }
             
             Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}
