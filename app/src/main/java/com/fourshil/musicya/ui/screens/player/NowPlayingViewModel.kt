package com.fourshil.musicya.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel() {

    val currentSong = playerController.currentSong
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val isPlaying = playerController.isPlaying
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val currentPosition = playerController.currentPosition
        .stateIn(viewModelScope, SharingStarted.Lazily, 0L)
    
    val duration = playerController.duration
        .stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    fun togglePlayPause() = playerController.togglePlayPause()
    fun skipToNext() = playerController.skipToNext()
    fun skipToPrevious() = playerController.skipToPrevious()
    fun seekTo(position: Long) = playerController.seekTo(position)
}
