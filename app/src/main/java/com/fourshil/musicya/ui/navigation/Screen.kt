package com.fourshil.musicya.ui.navigation

sealed class Screen(val route: String) {
    data object Songs : Screen("songs")
    data object Albums : Screen("albums")
    data object Artists : Screen("artists")
    data object Folders : Screen("folders")
    data object NowPlaying : Screen("now_playing")
    data object Queue : Screen("queue")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object Equalizer : Screen("equalizer")
    data object AlbumDetail : Screen("album/{albumId}") {
        fun createRoute(albumId: Long) = "album/$albumId"
    }
    data object ArtistDetail : Screen("artist/{artistName}") {
        fun createRoute(artistName: String) = "artist/$artistName"
    }
    data object FolderDetail : Screen("folder/{folderPath}") {
        fun createRoute(folderPath: String) = "folder/${java.net.URLEncoder.encode(folderPath, "UTF-8")}"
    }
}
