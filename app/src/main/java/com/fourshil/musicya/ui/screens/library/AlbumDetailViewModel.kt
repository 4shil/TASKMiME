package com.fourshil.musicya.ui.screens.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.IMusicRepository
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val repository: IMusicRepository,
    private val playerController: PlayerController,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val albumId: Long = checkNotNull(savedStateHandle["albumId"])
    
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()
    
    // We might want to store album name too, could pass it or fetch
    // For now assuming the view gets it or we fetch first song
    
    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _songs.value = repository.getSongsByAlbum(albumId)
        }
    }
    
    fun playSong(song: Song) {
        val allSongs = _songs.value
        val index = allSongs.indexOfFirst { it.id == song.id }
        if (index != -1) {
            playerController.playSongs(allSongs, index)
        }
    }
}
