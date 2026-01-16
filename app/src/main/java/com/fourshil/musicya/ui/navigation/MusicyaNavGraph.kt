package com.fourshil.musicya.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.ui.components.*
import com.fourshil.musicya.ui.library.*
import com.fourshil.musicya.ui.nowplaying.NowPlayingScreen
import com.fourshil.musicya.ui.nowplaying.NowPlayingViewModel
import com.fourshil.musicya.ui.playlist.PlaylistDetailScreen
import com.fourshil.musicya.ui.library.PlaylistsScreen
import com.fourshil.musicya.ui.queue.QueueScreen
import com.fourshil.musicya.ui.search.SearchScreen
import com.fourshil.musicya.ui.settings.EqualizerScreen
import com.fourshil.musicya.ui.settings.SettingsScreen
import com.fourshil.musicya.ui.theme.PureBlack
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicyaNavGraph() {
    val navController = rememberNavController()
    val nowPlayingViewModel: NowPlayingViewModel = hiltViewModel()
    
    // Song state
    val currentSong by nowPlayingViewModel.currentSong.collectAsState()
    val isPlaying by nowPlayingViewModel.isPlaying.collectAsState()
    val position by nowPlayingViewModel.position.collectAsState()
    val duration by nowPlayingViewModel.duration.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define Core Routes
    val libraryRoutes = listOf(
        Screen.Songs.route,
        Screen.Albums.route,
        Screen.Artists.route,
        Screen.Playlists.route,
        Screen.Favorites.route
    )

    // Should we show the bottom bar?
    val showBottomNav = libraryRoutes.contains(currentRoute) || currentRoute == Screen.Folders.route

    // Is Dark mode active? (Based on theme)
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // Halftone Overlay for the entire app
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        HalftoneBackground(
            color = if (isDark) Color.White else PureBlack,
            modifier = Modifier.matchParentSize()
        )
        
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                // Navigation Bar
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Songs.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Songs.route) {
                        SongsScreen(
                            onMenuClick = { navController.navigate(Screen.Settings.route) },
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    composable(Screen.Albums.route) {
                        AlbumsScreen(
                            onAlbumClick = { id -> navController.navigate(Screen.PlaylistDetail.createRoute("album", id.toString())) },
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    composable(Screen.Artists.route) {
                        ArtistsScreen(
                            onArtistClick = { name -> navController.navigate(Screen.PlaylistDetail.createRoute("artist", name)) },
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    composable(Screen.Folders.route) {
                        FoldersScreen(
                            onFolderClick = { path -> navController.navigate(Screen.PlaylistDetail.createRoute("folder", path)) },
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    composable(Screen.Favorites.route) { 
                        FavoritesScreen(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    
                    composable(Screen.Playlists.route) {
                        PlaylistsScreen(
                            onPlaylistClick = { id -> navController.navigate(Screen.PlaylistDetail.createRoute("playlist", id.toString())) },
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    
                    composable(Screen.NowPlaying.route) {
                        NowPlayingScreen(
                            onBack = { navController.popBackStack() },
                            onQueueClick = { navController.navigate(Screen.Queue.route) }
                        )
                    }
                    
                    composable(Screen.Queue.route) { QueueScreen(onBack = { navController.popBackStack() }) }
                    composable(Screen.Search.route) { SearchScreen(onBack = { navController.popBackStack() }) }
                    composable(Screen.Settings.route) {
                         SettingsScreen(
                            onBack = { navController.popBackStack() }, 
                            onEqualizerClick = { navController.navigate(Screen.Equalizer.route) }
                         )
                    }
                    composable(Screen.Equalizer.route) { EqualizerScreen(onBack = { navController.popBackStack() }) }
                    
                    composable(
                         route = Screen.PlaylistDetail.route,
                         arguments = listOf(navArgument("type") { type = NavType.StringType }, navArgument("id") { type = NavType.StringType })
                    ) { PlaylistDetailScreen(onBack = { navController.popBackStack() }) }
                }

                // Floating "Live Broadcast" MiniPlayer & Navigation
                // We place them in a Column aligned to bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mini Player (Only if not in now playing screen and song exists)
                    if (currentSong != null && currentRoute != Screen.NowPlaying.route) {
                        MiniPlayer(
                            song = currentSong,
                            isPlaying = isPlaying,
                            progress = if (duration > 0) position.toFloat() / duration else 0f,
                            onPlayPauseClick = { nowPlayingViewModel.togglePlayPause() },
                            onNextClick = { nowPlayingViewModel.skipToNext() },
                            onClick = { navController.navigate(Screen.NowPlaying.route) },
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .padding(bottom = if (showBottomNav) 8.dp else 24.dp)
                        )
                    }


                }
            }
        }
    }
}
