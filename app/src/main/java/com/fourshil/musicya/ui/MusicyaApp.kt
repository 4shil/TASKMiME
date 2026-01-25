package com.fourshil.musicya.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fourshil.musicya.ui.screens.home.HomeScreen

@Composable
fun MusicyaApp() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(navController = navController, startDestination = "library") {
            // Main Library Hub (Replacing simpler HomeScreen)
            composable("library") {
                com.fourshil.musicya.ui.screens.library.LibraryScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onSongClick = { navController.navigate("now_playing") }
                )
            }
            
            // Dictionary Routes
            composable(
                route = "album_detail/{albumId}",
                arguments = listOf(androidx.navigation.navArgument("albumId") { type = androidx.navigation.NavType.LongType })
            ) {
                com.fourshil.musicya.ui.screens.library.AlbumDetailScreen(
                    onBack = { navController.popBackStack() },
                    onSongClick = { navController.navigate("now_playing") }
                )
            }
            
            composable(
                route = "artist_detail/{artistName}",
                arguments = listOf(androidx.navigation.navArgument("artistName") { type = androidx.navigation.NavType.StringType })
            ) {
                com.fourshil.musicya.ui.screens.library.ArtistDetailScreen(
                    onBack = { navController.popBackStack() },
                    onSongClick = { navController.navigate("now_playing") }
                )
            }
            
            composable(
                route = "playlist_detail/{playlistId}",
                arguments = listOf(androidx.navigation.navArgument("playlistId") { type = androidx.navigation.NavType.LongType })
            ) {
                com.fourshil.musicya.ui.screens.library.PlaylistDetailScreen(
                    onBack = { navController.popBackStack() },
                    onSongClick = { navController.navigate("now_playing") }
                )
            }

            // Sub-Screens
            composable("albums") {
                com.fourshil.musicya.ui.screens.library.AlbumsScreen(
                    onBack = { navController.popBackStack() },
                    onAlbumClick = { album -> navController.navigate("album_detail/${album.id}") }
                )
            }
            
            composable("playlists") {
                com.fourshil.musicya.ui.screens.library.PlaylistsScreen(
                    onBack = { navController.popBackStack() },
                    onPlaylistClick = { playlist -> navController.navigate("playlist_detail/${playlist.id}") }
                )
            }
            
            composable("settings") {
                com.fourshil.musicya.ui.screens.settings.SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("artists") {
                com.fourshil.musicya.ui.screens.library.ArtistsScreen(
                    onBack = { navController.popBackStack() },
                    onArtistClick = { artist -> 
                        val encodedName = java.net.URLEncoder.encode(artist.name, java.nio.charset.StandardCharsets.UTF_8.toString())
                        navController.navigate("artist_detail/$encodedName") 
                    }
                )
            }
            composable("favorites") {
                com.fourshil.musicya.ui.screens.library.FavoritesScreen(
                    onBack = { navController.popBackStack() },
                    onSongClick = { navController.navigate("now_playing") }
                )
            }
            
            composable("now_playing") {
                com.fourshil.musicya.ui.screens.player.NowPlayingScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
