package com.fourshil.musicya.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.fourshil.musicya.data.model.Song
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var positionUpdateJob: Job? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()
    
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()
    
    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled = _shuffleEnabled.asStateFlow()
    
    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode = _repeatMode.asStateFlow()
    
    // Sleep Timer
    private var sleepTimerJob: Job? = null
    private val _sleepTimerRemaining = MutableStateFlow(0L) // milliseconds remaining
    val sleepTimerRemaining = _sleepTimerRemaining.asStateFlow()
    
    // Playback Speed
    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed = _playbackSpeed.asStateFlow()
    
    // Crossfade Duration (seconds, 0 = disabled)
    private val _crossfadeDuration = MutableStateFlow(0)
    val crossfadeDuration = _crossfadeDuration.asStateFlow()
    
    val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) {
            try { controllerFuture?.get() } catch (e: Exception) { null }
        } else null
    
    fun connect() {
        if (controllerFuture != null) return
        
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        
        controllerFuture?.addListener({
            val controller = controller ?: return@addListener
            
            controller.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    // Start/stop position updates based on playback state
                    if (isPlaying) startPositionUpdates() else stopPositionUpdates()
                }
                
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    updateCurrentSong(mediaItem)
                }
                
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _duration.value = controller.duration
                    }
                }
                
                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    _shuffleEnabled.value = shuffleModeEnabled
                }
                
                override fun onRepeatModeChanged(repeatMode: Int) {
                    _repeatMode.value = repeatMode
                }
            })
            
            _isPlaying.value = controller.isPlaying
            _shuffleEnabled.value = controller.shuffleModeEnabled
            _repeatMode.value = controller.repeatMode
            updateCurrentSong(controller.currentMediaItem)
            
        }, MoreExecutors.directExecutor())
    }
    
    private fun updateCurrentSong(mediaItem: MediaItem?) {
        if (mediaItem == null) {
            _currentSong.value = null
            return
        }
        
        val meta = mediaItem.mediaMetadata
        val albumId = meta.extras?.getLong("album_id") ?: 0L
        
        _currentSong.value = Song(
            id = mediaItem.mediaId.toLongOrNull() ?: 0,
            title = meta.title?.toString() ?: "Unknown",
            artist = meta.artist?.toString() ?: "Unknown Artist",
            album = meta.albumTitle?.toString() ?: "Unknown Album",
            albumId = albumId,
            duration = controller?.duration ?: 0,
            uri = mediaItem.localConfiguration?.uri ?: android.net.Uri.EMPTY,
            path = meta.extras?.getString("path") ?: "",
            dateAdded = 0,
            size = 0
        )
    }
    
    fun playSong(song: Song) {
        val mediaItem = buildMediaItem(song)
        controller?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }
    
    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        val mediaItems = songs.map { buildMediaItem(it) }
        controller?.apply {
            setMediaItems(mediaItems, startIndex, 0)
            prepare()
            play()
        }
    }

    private fun buildMediaItem(song: Song): MediaItem {
        return MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .setArtworkUri(song.albumArtUri)
                    .setExtras(android.os.Bundle().apply {
                        putLong("album_id", song.albumId)
                        putString("path", song.path)
                    })
                    .build()
            )
            .build()
    }
    
    fun togglePlayPause() {
        controller?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }
    
    fun seekTo(position: Long) {
        controller?.seekTo(position)
        _currentPosition.value = position
    }
    
    /**
     * Get current playback position immediately (for one-time reads).
     */
    fun getCurrentPosition(): Long = controller?.currentPosition ?: 0L
    
    /**
     * Start polling position updates. Call from UI when NowPlaying is visible.
     */
    fun startPositionUpdates() {
        if (positionUpdateJob?.isActive == true) return
        positionUpdateJob = scope.launch {
            while (isActive) {
                controller?.let {
                    _currentPosition.value = it.currentPosition
                    _duration.value = it.duration.coerceAtLeast(0L)
                }
                delay(250) // Update 4x per second for smooth progress bar
            }
        }
    }
    
    /**
     * Stop polling position updates. Call when NowPlaying screen is dismissed.
     */
    fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }
    
    fun skipToNext() {
        controller?.seekToNext()
    }
    
    fun skipToPrevious() {
        controller?.seekToPrevious()
    }
    
    fun toggleShuffle() {
        controller?.let {
            it.shuffleModeEnabled = !it.shuffleModeEnabled
        }
    }
    
    fun toggleRepeat() {
        controller?.let {
            it.repeatMode = when (it.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
        }
    }

    fun playNext(song: Song) {
        val mediaItem = buildMediaItem(song)
        controller?.let {
            val nextIndex = it.currentMediaItemIndex + 1
            it.addMediaItem(nextIndex, mediaItem)
        }
    }

    fun addToQueue(song: Song) {
        addToQueue(listOf(song))
    }

    fun addToQueue(songs: List<Song>) {
        val mediaItems = songs.map { buildMediaItem(it) }
        controller?.addMediaItems(mediaItems)
    }
    
    fun getQueue(): List<Song> {
        val controller = controller ?: return emptyList()
        val songs = mutableListOf<Song>()
        
        for (i in 0 until controller.mediaItemCount) {
            val item = controller.getMediaItemAt(i)
            val meta = item.mediaMetadata
            val albumId = meta.extras?.getLong("album_id") ?: 0L
            songs.add(
                Song(
                    id = item.mediaId.toLongOrNull() ?: 0,
                    title = meta.title?.toString() ?: "Unknown",
                    artist = meta.artist?.toString() ?: "Unknown Artist",
                    album = meta.albumTitle?.toString() ?: "Unknown Album",
                    albumId = albumId,
                    duration = 0,
                    uri = item.localConfiguration?.uri ?: android.net.Uri.EMPTY,
                    path = meta.extras?.getString("path") ?: "",
                    dateAdded = 0,
                    size = 0
                )
            )
        }
        
        return songs
    }
    
    /**
     * Start a sleep timer. Playback will pause after the specified duration.
     * @param minutes Duration in minutes (0 to cancel)
     */
    fun setSleepTimer(minutes: Int) {
        cancelSleepTimer()
        if (minutes <= 0) return
        
        val durationMs = minutes * 60 * 1000L
        _sleepTimerRemaining.value = durationMs
        
        sleepTimerJob = scope.launch {
            var remaining = durationMs
            while (remaining > 0 && isActive) {
                delay(1000)
                remaining -= 1000
                _sleepTimerRemaining.value = remaining
            }
            if (isActive) {
                controller?.pause()
                _sleepTimerRemaining.value = 0
            }
        }
    }
    
    /**
     * Cancel the active sleep timer.
     */
    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _sleepTimerRemaining.value = 0
    }
    
    /**
     * Check if sleep timer is active.
     */
    fun isSleepTimerActive(): Boolean = sleepTimerJob?.isActive == true
    
    /**
     * Set playback speed.
     * @param speed Valid range: 0.25f to 3.0f (clamped)
     */
    fun setPlaybackSpeed(speed: Float) {
        val clampedSpeed = speed.coerceIn(0.25f, 3.0f)
        controller?.setPlaybackSpeed(clampedSpeed)
        _playbackSpeed.value = clampedSpeed
    }
    
    /**
     * Cycle through common playback speed presets.
     */
    fun cyclePlaybackSpeed() {
        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        val currentIndex = speeds.indexOfFirst { it == _playbackSpeed.value }
        val nextIndex = if (currentIndex == -1 || currentIndex == speeds.lastIndex) 0 else currentIndex + 1
        setPlaybackSpeed(speeds[nextIndex])
    }
    
    /**
     * Reset playback speed to normal (1.0x).
     */
    fun resetPlaybackSpeed() {
        setPlaybackSpeed(1.0f)
    }
    
    /**
     * Set crossfade duration for track transitions.
     * @param seconds Duration in seconds (0 = disabled)
     * Note: Media3 does not natively support crossfade. 
     * Full implementation requires manual volume ducking.
     */
    fun setCrossfadeDuration(seconds: Int) {
        _crossfadeDuration.value = seconds.coerceIn(0, 12)
        android.util.Log.d("PlayerController", "Crossfade set to ${_crossfadeDuration.value}s")
        // TODO: Implement actual crossfade using volume ducking on track transition
    }
    
    fun release() {
        stopPositionUpdates()
        cancelSleepTimer()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        scope.cancel()
    }
}
