package com.fourshil.musicya.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central controller for music playback operations.
 * 
 * Manages the connection to [MusicService] via Media3's [MediaController] and exposes
 * playback state through Kotlin [StateFlow]s for reactive UI updates. Delegates specialized
 * functionality to focused manager classes:
 * - [SleepTimerManager] for sleep timer operations
 * - [PlaybackSpeedManager] for playback speed control
 *
 * ## Usage
 * Call [connect] early in the app lifecycle (typically from ViewModel init) to establish
 * the MediaController connection. All playback operations are safe to call immediately;
 * they will be queued until the controller is ready.
 *
 * @property context Application context for MediaController binding
 * @property sleepTimerManager Manages sleep timer functionality
 * @property speedManager Manages playback speed control
 */
@Singleton
class PlayerController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sleepTimerManager: SleepTimerManager,
    private val speedManager: PlaybackSpeedManager,
    private val crossfadeManager: CrossfadeManager
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var positionUpdateJob: Job? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    
    // Playback state
    private val _isPlaying = MutableStateFlow(false)
    /** Whether playback is currently active */
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentSong = MutableStateFlow<Song?>(null)
    /** The currently playing song, or null if nothing is playing */
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0L)
    /** Current playback position in milliseconds */
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    /** Duration of the current track in milliseconds */
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    private val _shuffleEnabled = MutableStateFlow(false)
    /** Whether shuffle mode is enabled */
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled.asStateFlow()
    
    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    /** Current repeat mode (OFF, ALL, or ONE) */
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    /** Current playback queue */
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _currentQueueIndex = MutableStateFlow(-1)
    /** Current index in the playback queue */
    val currentQueueIndex: StateFlow<Int> = _currentQueueIndex.asStateFlow()
    
    // Track queue count to avoid unnecessary rebuilds
    private var lastQueueCount = -1  // Use -1 to force initial update
    private var lastFirstItemId: String? = null  // Track first item to detect queue changes
    
    // Track position update interval - use slower updates for MiniPlayer, faster for NowPlaying
    private var positionUpdateInterval = 500L
    
    // Delegated managers - expose their state flows
    /** Remaining sleep timer time in milliseconds */
    val sleepTimerRemaining: StateFlow<Long> = sleepTimerManager.remainingMs
    
    /** Current playback speed (1.0 = normal) */
    val playbackSpeed: StateFlow<Float> = speedManager.speed
    
    // Crossfade setting
    private val _crossfadeDuration = MutableStateFlow(0)
    /** Crossfade duration in seconds (0 = disabled) */
    val crossfadeDuration: StateFlow<Int> = _crossfadeDuration.asStateFlow()
    
    /**
     * The underlying MediaController, if connected and ready.
     * Returns null if connection is pending or failed.
     */
    val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) {
            try { controllerFuture?.get() } catch (e: Exception) { null }
        } else null
    
    /**
     * Establish connection to the MusicService.
     * Safe to call multiple times; subsequent calls are no-ops.
     */
    fun connect() {
        if (controllerFuture != null) return
        
        // Initialize managers with required dependencies
        sleepTimerManager.initialize(scope) { controller?.pause() }
        speedManager.initialize { speed -> controller?.setPlaybackSpeed(speed) }
        
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        
        controllerFuture?.addListener({
            val mediaController = controller ?: return@addListener
            
            mediaController.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    if (isPlaying) startPositionUpdates() else stopPositionUpdates()
                }
                
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    updateCurrentSong(mediaItem)
                }
                
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _duration.value = mediaController.duration
                    }
                }
                
                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    _shuffleEnabled.value = shuffleModeEnabled
                }
                
                override fun onRepeatModeChanged(repeatMode: Int) {
                    _repeatMode.value = repeatMode
                }

                override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
                    updateQueue(mediaController)
                }
            })
            
            // Sync initial state
            _isPlaying.value = mediaController.isPlaying
            _shuffleEnabled.value = mediaController.shuffleModeEnabled
            _repeatMode.value = mediaController.repeatMode
            updateCurrentSong(mediaController.currentMediaItem)
            updateQueue(mediaController)
            
        }, MoreExecutors.directExecutor())
    }
    
    private fun updateCurrentSong(mediaItem: MediaItem?) {
        // Also update index
        _currentQueueIndex.value = controller?.currentMediaItemIndex ?: -1

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
            uri = mediaItem.localConfiguration?.uri ?: Uri.EMPTY,
            path = meta.extras?.getString("path") ?: "",
            dateAdded = 0,
            size = 0
        )
    }

    private fun updateQueue(controller: MediaController) {
        val count = controller.mediaItemCount
        val currentIndex = controller.currentMediaItemIndex
        
        // Get first item ID to detect content changes (not just count changes)
        val firstItemId = if (count > 0) controller.getMediaItemAt(0).mediaId else null
        
        // Rebuild queue if count changed OR first item changed (different playlist)
        if (count != lastQueueCount || firstItemId != lastFirstItemId) {
            lastQueueCount = count
            lastFirstItemId = firstItemId
            val newQueue = ArrayList<Song>(count)
            for (i in 0 until count) {
                val item = controller.getMediaItemAt(i)
                val meta = item.mediaMetadata
                newQueue.add(
                    Song(
                        id = item.mediaId.toLongOrNull() ?: 0,
                        title = meta.title?.toString() ?: "Unknown",
                        artist = meta.artist?.toString() ?: "Unknown Artist",
                        album = meta.albumTitle?.toString() ?: "Unknown Album",
                        albumId = meta.extras?.getLong("album_id") ?: 0L,
                        duration = 0, // Duration not available in queue items usually
                        uri = item.localConfiguration?.uri ?: Uri.EMPTY,
                        path = meta.extras?.getString("path") ?: "",
                        dateAdded = 0,
                        size = 0
                    )
                )
            }
            _queue.value = newQueue
        }
        _currentQueueIndex.value = currentIndex
    }
    
    // ============ Playback Control ============
    
    /**
     * Play a single song, replacing the current queue.
     * @param song The song to play
     */
    fun playSong(song: Song) {
        // Reset queue tracking to force update
        lastQueueCount = -1
        lastFirstItemId = null
        
        val ctrl = controller
        if (ctrl == null) {
            // Controller not ready, queue the operation
            scope.launch {
                // Wait for controller to be ready (max 3 seconds)
                var attempts = 0
                while (controller == null && attempts < 30) {
                    kotlinx.coroutines.delay(100)
                    attempts++
                }
                controller?.let { c ->
                    val mediaItem = buildMediaItem(song)
                    c.setMediaItem(mediaItem)
                    c.prepare()
                    c.play()
                }
            }
            return
        }
        val mediaItem = buildMediaItem(song)
        ctrl.setMediaItem(mediaItem)
        ctrl.prepare()
        ctrl.play()
    }
    
    /**
     * Play a list of songs starting at a specific index.
     * @param songs List of songs to play
     * @param startIndex Index of the song to start with (default: 0)
     */
    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        
        // Reset queue tracking to force update
        lastQueueCount = -1
        lastFirstItemId = null
        
        val ctrl = controller
        if (ctrl == null) {
            // Controller not ready, queue the operation
            scope.launch {
                // Wait for controller to be ready (max 3 seconds)
                var attempts = 0
                while (controller == null && attempts < 30) {
                    kotlinx.coroutines.delay(100)
                    attempts++
                }
                controller?.let { c ->
                    val mediaItems = songs.map { buildMediaItem(it) }
                    c.setMediaItems(mediaItems, startIndex, 0)
                    c.prepare()
                    c.play()
                }
            }
            return
        }
        val mediaItems = songs.map { buildMediaItem(it) }
        ctrl.setMediaItems(mediaItems, startIndex, 0)
        ctrl.prepare()
        ctrl.play()
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
                    .setExtras(Bundle().apply {
                        putLong("album_id", song.albumId)
                        putString("path", song.path)
                    })
                    .build()
            )
            .build()
    }
    
    /** Toggle between play and pause states */
    fun togglePlayPause() {
        controller?.let { if (it.isPlaying) it.pause() else it.play() }
    }
    
    /**
     * Seek to a specific position in the current track.
     * @param position Position in milliseconds
     */
    fun seekTo(position: Long) {
        controller?.seekTo(position)
        _currentPosition.value = position
    }
    
    /** Get current playback position immediately (for one-time reads) */
    fun getCurrentPosition(): Long = controller?.currentPosition ?: 0L
    
    /** 
     * Start polling position updates for progress bar.
     * @param fastUpdates If true, use faster update interval (for NowPlaying screen)
     */
    fun startPositionUpdates(fastUpdates: Boolean = false) {
        positionUpdateInterval = if (fastUpdates) 200L else 500L
        if (positionUpdateJob?.isActive == true) {
            // If already running, just update interval on next iteration
            return
        }
        positionUpdateJob = scope.launch {
            while (isActive) {
                controller?.let {
                    _currentPosition.value = it.currentPosition
                    _duration.value = it.duration.coerceAtLeast(0L)
                }
                delay(positionUpdateInterval)
            }
        }
    }
    
    /** Stop polling position updates */
    fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }
    
    /** Skip to the next track */
    fun skipToNext() = controller?.seekToNext()
    
    /** Skip to the previous track */
    fun skipToPrevious() = controller?.seekToPrevious()
    
    /** Toggle shuffle mode on/off */
    fun toggleShuffle() {
        controller?.let { it.shuffleModeEnabled = !it.shuffleModeEnabled }
    }
    
    /** Cycle repeat mode: OFF → ALL → ONE → OFF */
    fun toggleRepeat() {
        controller?.let {
            it.repeatMode = when (it.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
        }
    }
    
    // ============ Queue Management ============
    
    /**
     * Insert a song to play after the current track.
     * @param song The song to insert
     */
    fun playNext(song: Song) {
        val mediaItem = buildMediaItem(song)
        controller?.let {
            val nextIndex = it.currentMediaItemIndex + 1
            it.addMediaItem(nextIndex, mediaItem)
        }
    }
    
    /**
     * Add a song to the end of the queue.
     * @param song The song to add
     */
    fun addToQueue(song: Song) = addToQueue(listOf(song))
    
    /**
     * Add multiple songs to the end of the queue.
     * @param songs The songs to add
     */
    fun addToQueue(songs: List<Song>) {
        val mediaItems = songs.map { buildMediaItem(it) }
        controller?.addMediaItems(mediaItems)
    }
    
    /**
     * Get the current playback queue.
     * @return List of songs in the queue
     */
    fun getQueue(): List<Song> {
        val ctrl = controller ?: return emptyList()
        return (0 until ctrl.mediaItemCount).map { i ->
            val item = ctrl.getMediaItemAt(i)
            val meta = item.mediaMetadata
            Song(
                id = item.mediaId.toLongOrNull() ?: 0,
                title = meta.title?.toString() ?: "Unknown",
                artist = meta.artist?.toString() ?: "Unknown Artist",
                album = meta.albumTitle?.toString() ?: "Unknown Album",
                albumId = meta.extras?.getLong("album_id") ?: 0L,
                duration = 0,
                uri = item.localConfiguration?.uri ?: Uri.EMPTY,
                path = meta.extras?.getString("path") ?: "",
                dateAdded = 0,
                size = 0
            )
        }
    }
    
    // ============ Sleep Timer (Delegated) ============
    
    /**
     * Start a sleep timer. Playback will pause after the specified duration.
     * @param minutes Duration in minutes (0 to cancel)
     */
    fun setSleepTimer(minutes: Int) = sleepTimerManager.setTimer(minutes)
    
    /** Cancel the active sleep timer */
    fun cancelSleepTimer() = sleepTimerManager.cancel()
    
    /** Check if sleep timer is active */
    fun isSleepTimerActive(): Boolean = sleepTimerManager.isActive()
    
    // ============ Playback Speed (Delegated) ============
    
    /**
     * Set playback speed.
     * @param speed Valid range: 0.25 to 3.0
     */
    fun setPlaybackSpeed(speed: Float) = speedManager.setSpeed(speed)
    
    /** Cycle through speed presets: 0.5x → 0.75x → 1.0x → 1.25x → 1.5x → 2.0x */
    fun cyclePlaybackSpeed() = speedManager.cycleSpeed()
    
    /** Reset speed to normal (1.0x) */
    fun resetPlaybackSpeed() = speedManager.reset()
    
    // ============ Crossfade ============
    
    /**
     * Set crossfade duration for track transitions.
     * @param seconds Duration in seconds (0 = disabled)
     * Note: Media3 does not natively support crossfade.
     */
    fun setCrossfadeDuration(seconds: Int) {
        val duration = seconds.coerceIn(0, 12)
        _crossfadeDuration.value = duration
        crossfadeManager.setDuration(duration)
    }
    
    // ============ Lifecycle ============
    
    /** Release all resources. Call when the player is no longer needed. */
    fun release() {
        stopPositionUpdates()
        sleepTimerManager.release()
        speedManager.release()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        scope.cancel()
    }
}
