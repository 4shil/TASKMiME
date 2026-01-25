package com.fourshil.musicya.ui.screens.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.IMusicRepository
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val repository: IMusicRepository,
    private val playerController: PlayerController,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val playlistId: Long = checkNotNull(savedStateHandle["playlistId"])
    
    // Fetch playlist info
    // For now assuming we just show songs.
    // In a real app we'd fetch the Playlist object to get the name too, 
    // or pass it as an arg.
    
    private val playlistSongIds = musicDao.getPlaylistSongIds(playlistId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    val songs: StateFlow<List<Song>> = playlistSongIds.map { ids ->
        if (ids.isEmpty()) emptyList() else repository.getSongsByIds(ids)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun playSong(song: Song) {
        val allSongs = songs.value
        val index = allSongs.indexOfFirst { it.id == song.id }
        if (index != -1) {
            playerController.playSongs(allSongs, index)
        }
    }
}
