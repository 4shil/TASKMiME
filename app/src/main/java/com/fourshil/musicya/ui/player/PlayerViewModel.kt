package com.fourshil.musicya.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.fourshil.musicya.player.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val serviceConnection: MusicServiceConnection
) : ViewModel() {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _currentSongPath = MutableStateFlow<String?>(null)
    val currentSongPath = _currentSongPath.asStateFlow()
    
    private val _songTitle = MutableStateFlow("Unknown")
    val songTitle = _songTitle.asStateFlow()
    
    private val _songArtist = MutableStateFlow("Unknown Artist")
    val songArtist = _songArtist.asStateFlow()

    init {
        viewModelScope.launch {
            // Wait for connection
            while (serviceConnection.player == null) {
                delay(100)
            }
            val player = serviceConnection.player!!
            setupPlayerListener(player)
            
            // Test Stream
            if (player.mediaItemCount == 0) {
                val mediaItem = MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
                player.setMediaItem(mediaItem)
                player.prepare()
            }
        }
        
        // Progress poller
        viewModelScope.launch {
            while (true) {
                val player = serviceConnection.player
                if (player != null && player.isPlaying) {
                    _currentPosition.value = player.currentPosition
                    _duration.value = player.duration
                }
                delay(1000)
            }
        }
    }

    private fun setupPlayerListener(player: Player) {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.mediaMetadata?.let { meta ->
                    _songTitle.value = meta.title?.toString() ?: "Unknown"
                    _songArtist.value = meta.artist?.toString() ?: "Unknown Artist"
                    _currentSongPath.value = meta.extras?.getString("path")
                }
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
               if (playbackState == Player.STATE_READY) {
                   _duration.value = player.duration
               }
            }
        })
        _isPlaying.value = player.isPlaying
        _duration.value = player.duration
        player.currentMediaItem?.mediaMetadata?.let { meta ->
             _songTitle.value = meta.title?.toString() ?: "Unknown"
             _songArtist.value = meta.artist?.toString() ?: "Unknown Artist"
             _currentSongPath.value = meta.extras?.getString("path")
        }
    }

    fun togglePlayPause() {
        serviceConnection.player?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    fun seekTo(position: Long) {
        serviceConnection.player?.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection.release()
    }
}
