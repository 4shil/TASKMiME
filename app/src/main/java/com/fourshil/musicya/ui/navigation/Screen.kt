package com.fourshil.musicya.ui.navigation

import java.net.URLEncoder

sealed class Screen(val route: String) {
    data object Songs : Screen("songs")
    data object Albums : Screen("albums")
    data object Artists : Screen("artists")
    data object Folders : Screen("folders")
    data object Favorites : Screen("favorites")
    data object Playlists : Screen("playlists")
    data object NowPlaying : Screen("now_playing")
    data object Queue : Screen("queue")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object Equalizer : Screen("equalizer")
    
    // Playlist detail for albums, artists, folders, and custom playlists
    data object PlaylistDetail : Screen("playlist/{type}/{id}") {
        fun createRoute(type: String, id: String): String {
            val encodedId = URLEncoder.encode(id, "UTF-8")
            return "playlist/$type/$encodedId"
        }
    }
}

