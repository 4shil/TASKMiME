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
        contentPadding = PaddingValues(bottom = 160.dp)
    ) {
        item {
             Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(24.dp))
                 // Header
                Text(
                    text = "FOLDERS",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                TopNavigationChips(
                    items = listOf(
                        TopNavItem(Screen.Songs.route, "Songs"),
                        TopNavItem(Screen.Favorites.route, "Favorites"),
                        TopNavItem(Screen.Folders.route, "Folders"),
                        TopNavItem(Screen.Playlists.route, "Playlists"),
                        TopNavItem(Screen.Albums.route, "Albums"),
                        TopNavItem(Screen.Artists.route, "Artists")
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
                
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
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
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Box(
                modifier = Modifier.size(56.dp).background(Color.Transparent),
                 contentAlignment = Alignment.Center
             ) {
                 if (artUris.isNotEmpty()) {
                     PlaylistArtGrid(uris = artUris, size = 56.dp)
                 } else {
                     Icon(Icons.Default.Folder, null, tint = PureBlack, modifier = Modifier.size(32.dp))
                 }
             }

             Spacer(modifier = Modifier.width(16.dp))

             Column(modifier = Modifier.weight(1f)) {
                 Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    maxLines = 1
                 )
                 Text(
                    text = "${folder.songCount} FILES",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = PureBlack.copy(alpha=0.6f)
                 )
             }
             
             Icon(Icons.Default.ChevronRight, null, tint = PureBlack)
        }
    }
}
