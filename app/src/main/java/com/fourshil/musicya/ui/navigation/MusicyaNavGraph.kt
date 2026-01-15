package com.fourshil.musicya.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.fourshil.musicya.ui.components.MiniPlayer
import com.fourshil.musicya.ui.library.*
import com.fourshil.musicya.ui.nowplaying.NowPlayingScreen
import com.fourshil.musicya.ui.nowplaying.NowPlayingViewModel
import com.fourshil.musicya.ui.playlist.PlaylistDetailScreen
import com.fourshil.musicya.ui.queue.QueueScreen
import com.fourshil.musicya.ui.search.SearchScreen
import com.fourshil.musicya.ui.settings.EqualizerScreen
import com.fourshil.musicya.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicyaNavGraph() {
    val navController = rememberNavController()
    val nowPlayingViewModel: NowPlayingViewModel = hiltViewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentSong by nowPlayingViewModel.currentSong.collectAsState()
    val isPlaying by nowPlayingViewModel.isPlaying.collectAsState()
    val position by nowPlayingViewModel.position.collectAsState()
    val duration by nowPlayingViewModel.duration.collectAsState()

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Songs.route, "Songs") { Icon(Icons.Default.MusicNote, null) },
        BottomNavItem(Screen.Albums.route, "Albums") { Icon(Icons.Default.Album, null) },
        BottomNavItem(Screen.Artists.route, "Artists") { Icon(Icons.Default.Person, null) },
        BottomNavItem(Screen.Folders.route, "Folders") { Icon(Icons.Default.Folder, null) }
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom nav on full-screen pages
    val fullScreenRoutes = listOf(
        Screen.NowPlaying.route,
        Screen.Queue.route,
        Screen.Search.route,
        Screen.Settings.route,
        Screen.Equalizer.route,
        Screen.PlaylistDetail.route
    )
    // Routes where mini player should be hidden (only full-screen player)
    val hideMiniPlayerRoutes = listOf(
        Screen.NowPlaying.route
    )
    val showBottomNav = fullScreenRoutes.none { currentRoute?.startsWith(it.substringBefore("{")) == true }
    val showMiniPlayer = hideMiniPlayerRoutes.none { currentRoute?.startsWith(it.substringBefore("{")) == true }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                
                // Header
                Text(
                    text = "LYRA",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(28.dp)
                )
                
                NavigationDrawerItem(
                    label = { Text("Favorites") },
                    icon = { Icon(Icons.Default.Favorite, null) },
                    selected = currentRoute == Screen.Favorites.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Favorites.route)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Playlists") },
                    icon = { Icon(Icons.Default.QueueMusic, null) },
                    selected = currentRoute == Screen.Playlists.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Playlists.route)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    icon = { Icon(Icons.Default.Settings, null) },
                    selected = currentRoute == Screen.Settings.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Settings.route)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (showBottomNav) {
                    TopAppBar(
                        title = { Text("LYRA") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                Column {
                    // Mini Player - show on all pages except now playing
                    if (showMiniPlayer && currentSong != null) {
                        MiniPlayer(
                            song = currentSong,
                            isPlaying = isPlaying,
                            progress = if (duration > 0) position.toFloat() / duration else 0f,
                            onPlayPauseClick = { nowPlayingViewModel.togglePlayPause() },
                            onNextClick = { nowPlayingViewModel.skipToNext() },
                            onClick = { navController.navigate(Screen.NowPlaying.route) }
                        )
                    }

                    // Bottom Navigation
                    if (showBottomNav) {
                        NavigationBar {
                            bottomNavItems.forEach { item ->
                                NavigationBarItem(
                                    icon = item.icon,
                                    label = { Text(item.label) },
                                    selected = currentRoute == item.route,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Songs.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(Screen.Songs.route) {
                    SongsScreen()
                }
                composable(Screen.Albums.route) {
                    AlbumsScreen(
                        onAlbumClick = { albumId ->
                            navController.navigate(Screen.PlaylistDetail.createRoute("album", albumId.toString()))
                        }
                    )
                }
                composable(Screen.Artists.route) {
                    ArtistsScreen(
                        onArtistClick = { artistName ->
                            navController.navigate(Screen.PlaylistDetail.createRoute("artist", artistName))
                        }
                    )
                }
                composable(Screen.Folders.route) {
                    FoldersScreen(
                        onFolderClick = { folderPath ->
                            navController.navigate(Screen.PlaylistDetail.createRoute("folder", folderPath))
                        }
                    )
                }
                composable(Screen.Favorites.route) {
                    FavoritesScreen()
                }
                composable(Screen.Playlists.route) {
                    PlaylistsScreen(
                        onPlaylistClick = { playlistId ->
                            navController.navigate(Screen.PlaylistDetail.createRoute("playlist", playlistId.toString()))
                        }
                    )
                }
                composable(Screen.NowPlaying.route) {
                    NowPlayingScreen(
                        onBack = { navController.popBackStack() },
                        onQueueClick = { navController.navigate(Screen.Queue.route) }
                    )
                }
                composable(Screen.Queue.route) {
                    QueueScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Search.route) {
                    SearchScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        onEqualizerClick = { navController.navigate(Screen.Equalizer.route) }
                    )
                }
                composable(Screen.Equalizer.route) {
                    EqualizerScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                // Playlist Detail Screen
                composable(
                    route = Screen.PlaylistDetail.route,
                    arguments = listOf(
                        navArgument("type") { type = NavType.StringType },
                        navArgument("id") { type = NavType.StringType }
                    )
                ) {
                    PlaylistDetailScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

