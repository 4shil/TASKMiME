package com.fourshil.musicya.ui.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel() {

    val currentSong = playerController.currentSong
    val isPlaying = playerController.isPlaying
    val shuffleEnabled = playerController.shuffleEnabled
    val repeatMode = playerController.repeatMode

    private val _position = MutableStateFlow(0L)
    val position = _position.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private var positionUpdateJob: Job? = null

    init {
        playerController.connect()
        startPositionUpdater()
    }

    private fun startPositionUpdater() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch {
            while (isActive) {
                val controller = playerController.controller
                if (controller != null) {
                    _position.value = controller.currentPosition
                    _duration.value = if (controller.duration > 0) controller.duration else 0L
                }
                delay(200) // 5Hz is enough for the text, slider handled in UI
            }
        }
    }

    fun togglePlayPause() = playerController.togglePlayPause()
    fun skipToNext() = playerController.skipToNext()
    fun skipToPrevious() = playerController.skipToPrevious()
    fun toggleShuffle() = playerController.toggleShuffle()
    fun toggleRepeat() = playerController.toggleRepeat()
    
    fun seekTo(positionMs: Long) {
        playerController.seekTo(positionMs)
        _position.value = positionMs
    }

    override fun onCleared() {
        super.onCleared()
        positionUpdateJob?.cancel()
    }
}
