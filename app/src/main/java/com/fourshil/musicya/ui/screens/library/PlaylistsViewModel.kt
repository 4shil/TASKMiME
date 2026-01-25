package com.fourshil.musicya.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.data.db.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val musicDao: MusicDao
) : ViewModel() {
    
    val playlists = musicDao.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            musicDao.createPlaylist(Playlist(name = name))
        }
    }
    
    fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            musicDao.deletePlaylist(id)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            // Get current count for sort order
            // Simplified: just passing 0 or letting DAO handle it if we updated DAO
            // DAO addSongsToPlaylist accepts list.
            // Let's add a single add method or use the list one
            musicDao.addSongsToPlaylist(playlistId, listOf(songId))
        }
    }
}
