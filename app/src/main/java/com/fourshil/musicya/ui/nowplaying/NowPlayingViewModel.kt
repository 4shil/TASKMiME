package com.fourshil.musicya.ui.nowplaying

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.player.PlayerController
import com.fourshil.musicya.util.AlbumArtHelper
import com.fourshil.musicya.util.Lyrics
import com.fourshil.musicya.util.LyricsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val musicDao: MusicDao,
    private val lyricsManager: LyricsManager,
    private val albumArtHelper: AlbumArtHelper
) : ViewModel() {

    val currentSong = playerController.currentSong
    
    // High-quality album art URI for Now Playing screen
    private val _highQualityArtUri = MutableStateFlow<Uri?>(null)
    val highQualityArtUri = _highQualityArtUri.asStateFlow()
    val isPlaying = playerController.isPlaying
    val shuffleEnabled = playerController.shuffleEnabled
    val repeatMode = playerController.repeatMode
    
    // Use PlayerController's position/duration directly
    val position = playerController.currentPosition
    val duration = playerController.duration

    val isFavorite = currentSong.flatMapLatest { song ->
        if (song != null) musicDao.isFavorite(song.id) else flowOf(false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    // Lyrics support
    private val _lyrics = MutableStateFlow<Lyrics?>(null)
    val lyrics = _lyrics.asStateFlow()
    
    private val _showLyrics = MutableStateFlow(false)
    val showLyrics = _showLyrics.asStateFlow()

    init {
        playerController.connect()
        // Start position updates when ViewModel is created
        playerController.startPositionUpdates()
        
        // Load lyrics and high-quality art when song changes
        viewModelScope.launch {
            currentSong.collect { song ->
                if (song != null) {
                    _lyrics.value = lyricsManager.getLyricsForSong(song)
                    // Load high-quality album art for Now Playing screen
                    _highQualityArtUri.value = albumArtHelper.getHighQualityArtUri(song.path, song.albumId)
                } else {
                    _lyrics.value = null
                    _highQualityArtUri.value = null
                }
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
    }

    fun toggleFavorite() {
        val song = currentSong.value ?: return
        viewModelScope.launch {
            musicDao.toggleFavorite(song.id)
        }
    }
    
    fun toggleLyricsView() {
        _showLyrics.value = !_showLyrics.value
    }
    
    /**
     * Get the current lyric line index based on playback position.
     */
    fun getCurrentLyricIndex(): Int {
        val currentLyrics = _lyrics.value ?: return -1
        return currentLyrics.getCurrentLineIndex(position.value)
    }

    override fun onCleared() {
        super.onCleared()
        // Stop updates when NowPlaying is not visible
        playerController.stopPositionUpdates()
    }
}


