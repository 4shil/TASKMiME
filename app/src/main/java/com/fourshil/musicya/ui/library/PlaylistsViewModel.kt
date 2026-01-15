package com.fourshil.musicya.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.data.db.Playlist
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val musicRepository: MusicRepository
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

    /**
     * Get songs for a playlist to display artwork grid.
     */
    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>> = flow {
        musicDao.getPlaylistSongs(playlistId).collect { playlistSongs ->
            val allSongs = musicRepository.getAllSongs()
            val songs = playlistSongs.mapNotNull { ps ->
                allSongs.find { it.id == ps.songId }
            }
            emit(songs)
        }
    }
}
