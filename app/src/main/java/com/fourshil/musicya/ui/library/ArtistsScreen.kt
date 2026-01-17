package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.components.PlaylistArtGrid
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack

import com.fourshil.musicya.ui.components.TopNavItem
import com.fourshil.musicya.ui.components.TopNavigationChips
import com.fourshil.musicya.ui.navigation.Screen

@Composable
fun ArtistsScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onArtistClick: (String) -> Unit = {},
    currentRoute: String? = null,
    onNavigate: (String) -> Unit = {}
) {
    val artists by viewModel.artists.collectAsState()
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
                
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "THE",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        ),
                        modifier = Modifier.align(Alignment.TopStart),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "ARTISTS",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
             }

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

        if (isLoading) {
             item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                }
             }
        } else if (artists.isEmpty()) {
             item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("NO VOICES", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
                }
             }
        } else {
            items(
                items = artists,
                key = { it.id },
                contentType = { "artist_item" }
            ) { artist ->
                 val artistSongs = songs.filter { it.artist == artist.name }
                 Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    ArtistArtisticItem(
                        artist = artist,
                        artUris = artistSongs.map { it.albumArtUri },
                        onClick = { onArtistClick(artist.name) }
                    )
                 }
            }
        }
    }
}

@Composable
fun ArtistArtisticItem(
    artist: Artist,
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
            // Art Grid
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .border(3.dp, PureBlack)
                    .background(PureBlack)
            ) {
                 PlaylistArtGrid(uris = artUris, size = 64.dp)
            }
           
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = artist.name.uppercase(),
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${artist.songCount} TRKS // ${artist.albumCount} ALBS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MangaRed
                )
            }
        }
    }
}
