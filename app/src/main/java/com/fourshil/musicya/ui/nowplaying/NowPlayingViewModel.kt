package com.fourshil.musicya.ui.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val musicDao: MusicDao
) : ViewModel() {

    val currentSong = playerController.currentSong
    val isPlaying = playerController.isPlaying
    val shuffleEnabled = playerController.shuffleEnabled
    val repeatMode = playerController.repeatMode
    
    // Use PlayerController's position/duration directly
    val position = playerController.currentPosition
    val duration = playerController.duration

    val isFavorite = currentSong.flatMapLatest { song ->
        if (song != null) musicDao.isFavorite(song.id) else flowOf(false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        playerController.connect()
        // Start position updates when ViewModel is created
        playerController.startPositionUpdates()
    }

    fun togglePlayPause() = playerController.togglePlayPause()
    fun skipToNext() = playerController.skipToNext()
    fun skipToPrevious() = playerController.skipToPrevious()
    fun toggleShuffle() = playerController.toggleShuffle()
    fun toggleRepeat() = playerController.toggleRepeat()
    
    fun seekTo(positionMs: Long) {
        playerController.seekTo(positionMs)
    }

    fun toggleFavorite() {
        val song = currentSong.value ?: return
        viewModelScope.launch {
            musicDao.toggleFavorite(song.id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Stop updates when NowPlaying is not visible
        playerController.stopPositionUpdates()
    }
}

