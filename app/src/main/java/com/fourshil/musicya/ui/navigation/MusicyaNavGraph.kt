package com.fourshil.musicya.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.fourshil.musicya.player.PlayerController
import com.fourshil.musicya.ui.theme.PureBlack
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicyaNavGraph(
    playerController: PlayerController
) {
    val navController = rememberNavController()
    val nowPlayingViewModel: NowPlayingViewModel = hiltViewModel()
    
    // Song state
    // Song state
    val currentSong by nowPlayingViewModel.currentSong.collectAsState()
    // position and duration moved to ConnectedMiniPlayer to avoid root recomposition over overhead

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define Core Routes
    val libraryRoutes = listOf(
        Screen.Songs.route,
        Screen.Albums.route,
        Screen.Artists.route,
        Screen.Playlists.route,
        Screen.Favorites.route,
        Screen.RecentlyPlayed.route,
        Screen.MostPlayed.route,
        Screen.NeverPlayed.route
    )

    // Should we show the bottom bar?
    val showBottomNav = libraryRoutes.contains(currentRoute) || currentRoute == Screen.Folders.route

    // Unified Header Logic
    val currentTitle = when (currentRoute) {
        Screen.Songs.route -> "SONGS"
        Screen.Albums.route -> "ALBUMS"
        Screen.Artists.route -> "ARTISTS"
        Screen.Folders.route -> "FOLDERS"
        Screen.Favorites.route -> "FAVORITES"
        Screen.Playlists.route -> "PLAYLISTS"
        Screen.RecentlyPlayed.route -> "RECENT"
        Screen.MostPlayed.route -> "POPULAR"
        Screen.NeverPlayed.route -> "UNHEARD"
        else -> "MUSICYA"
    }

    // Halftone Overlay for the entire app
    val isDark = isSystemInDarkTheme()
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        HalftoneBackground(
            color = if (isDark) Color.White else PureBlack,
            modifier = Modifier.matchParentSize()
        )
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
               if (libraryRoutes.contains(currentRoute) || currentRoute == Screen.Folders.route) {
                   UnifiedLibraryHeader(
                       title = currentTitle,
                       currentRoute = currentRoute,
                       onMenuClick = { navController.navigate(Screen.Settings.route) },
                       onSearchClick = { navController.navigate(Screen.Search.route) },
                       onNavigate = { route ->
                            if (route != currentRoute) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                       }
                   )
               }
            },
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
                            onNavigate = { } // Navigation handled by parent
                        )
                    }
                    composable(Screen.Albums.route) {
                        AlbumsScreen(
                            onAlbumClick = { id -> navController.navigate(Screen.PlaylistDetail.createRoute("album", id.toString())) },
                            currentRoute = currentRoute,
                            onNavigate = { }
                        )
                    }
                    composable(Screen.Artists.route) {
                        ArtistsScreen(
                            onArtistClick = { name -> navController.navigate(Screen.PlaylistDetail.createRoute("artist", name)) },
                            currentRoute = currentRoute,
                            onNavigate = { }
                        )
                    }
                    composable(Screen.Folders.route) {
                        FoldersScreen(
                            onFolderClick = { path -> navController.navigate(Screen.PlaylistDetail.createRoute("folder", path)) },
                            currentRoute = currentRoute,
                            onNavigate = { }
                        )
                    }
                    composable(Screen.Favorites.route) { 
                        FavoritesScreen(
                            currentRoute = currentRoute,
                            onNavigate = { }
                        )
                    }
                    
                    composable(Screen.RecentlyPlayed.route) {
                        RecentlyPlayedScreen(
                            currentRoute = currentRoute,
                            onNavigate = { }
                        )
                    }
                    
                    composable(Screen.MostPlayed.route) {
                        MostPlayedScreen(
                            currentRoute = currentRoute,
                            onNavigate = { }
                        )
                    }

                    composable(Screen.NeverPlayed.route) {
                        NeverPlayedScreen(
                            currentRoute = currentRoute,
                            onNavigate = { }
                        )
                    }
                    
                    composable(Screen.Playlists.route) {
                        PlaylistsScreen(
                            onPlaylistClick = { id -> navController.navigate(Screen.PlaylistDetail.createRoute("playlist", id.toString())) },
                            currentRoute = currentRoute,
                            onNavigate = { }
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
                            playerController = playerController,
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
                        // Pass ViewModel or Lambda to avoid reading frequent state at this level
                        // However, since we already read `position` above, we need to remove THAT reading.
                        // But wait, removing the reading above affects `MiniPlayer` passing.
                        // We will inline the connection here for now or delegate to a wrapper.
                        
                        ConnectedMiniPlayer(
                            viewModel = nowPlayingViewModel,
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
