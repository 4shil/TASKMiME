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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerController @Inject constructor(
    @ApplicationContext private val context: Context
) {
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
    
    fun release() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
    }
}
