package com.fourshil.musicya.ui.screens.home

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
class HomeViewModel @Inject constructor(
    private val repository: IMusicRepository,
    private val playerController: PlayerController
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _songs.value = repository.getAllSongs()
        }
    }

    fun playSong(song: Song) {
        // If clicking a song in the list, we probably want to play it contextually (e.g. as part of the list)
        // For now, let's just play the song and set the queue to the full list starting from this song.
        val allSongs = _songs.value
        val index = allSongs.indexOfFirst { it.id == song.id }
        if (index != -1) {
            playerController.playSongs(allSongs, index)
        } else {
            playerController.playSong(song)
        }
    }
}
