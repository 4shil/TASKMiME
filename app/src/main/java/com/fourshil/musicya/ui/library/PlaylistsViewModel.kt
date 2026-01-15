package com.fourshil.musicya.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.data.db.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val musicDao: MusicDao
) : ViewModel() {
    
    val playlists = musicDao.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun createPlaylist(name: String) {
        viewModelScope.launch {
            musicDao.createPlaylist(Playlist(name = name))
        }
    }
    
    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            musicDao.deletePlaylist(playlistId)
        }
    }
    
    fun renamePlaylist(playlistId: Long, newName: String) {
        viewModelScope.launch {
            musicDao.renamePlaylist(playlistId, newName)
        }
    }
    
    fun getPlaylistSongCount(playlistId: Long): Flow<Int> {
        return musicDao.getPlaylistSongCount(playlistId)
    }
}
