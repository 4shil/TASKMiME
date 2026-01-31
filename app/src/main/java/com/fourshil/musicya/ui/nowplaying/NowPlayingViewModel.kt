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
import kotlinx.coroutines.async
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

    // Cache favorite IDs in memory for efficient lookup
    private val favoriteIdsCache = musicDao.getFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    
    // Derive isFavorite from cached set instead of per-song query
    val isFavorite = combine(currentSong, favoriteIdsCache) { song, favorites ->
        song != null && song.id in favorites
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    // Lyrics support
    private val _lyrics = MutableStateFlow<Lyrics?>(null)
    val lyrics = _lyrics.asStateFlow()
    
    private val _showLyrics = MutableStateFlow(false)
    val showLyrics = _showLyrics.asStateFlow()

    init {
        playerController.connect()
        // Start fast position updates for NowPlaying screen
        playerController.startPositionUpdates(fastUpdates = true)
        
        // Load lyrics and high-quality art when song changes - in parallel
        viewModelScope.launch {
            currentSong.collect { song ->
                if (song != null) {
                    // Load both in parallel for faster response
                    val lyricsDeferred = async { lyricsManager.getLyricsForSong(song) }
                    val artDeferred = async { albumArtHelper.getHighQualityArtUri(song.path, song.albumId) }
                    
                    _lyrics.value = lyricsDeferred.await()
                    _highQualityArtUri.value = artDeferred.await()
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