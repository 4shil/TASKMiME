package com.fourshil.musicya.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel() {

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex = _currentIndex.asStateFlow()

    init {
        playerController.connect()
        startQueueUpdater()
    }

    private fun startQueueUpdater() {
        viewModelScope.launch {
            while (true) {
                updateQueue()
                delay(500)
            }
        }
    }

    private fun updateQueue() {
        val controller = playerController.controller ?: return
        
        val items = mutableListOf<Song>()
        for (i in 0 until controller.mediaItemCount) {
            val mediaItem = controller.getMediaItemAt(i)
            items.add(mediaItemToSong(mediaItem))
        }
        
        _queue.value = items
        _currentIndex.value = controller.currentMediaItemIndex
    }

    private fun mediaItemToSong(mediaItem: MediaItem): Song {
        val meta = mediaItem.mediaMetadata
        val albumId = meta.extras?.getLong("album_id") ?: 0L
        val path = meta.extras?.getString("path") ?: ""
        
        return Song(
            id = mediaItem.mediaId.toLongOrNull() ?: 0,
            title = meta.title?.toString() ?: "Unknown",
            artist = meta.artist?.toString() ?: "Unknown Artist",
            album = meta.albumTitle?.toString() ?: "Unknown Album",
            albumId = albumId,
            duration = 0,
            uri = mediaItem.localConfiguration?.uri ?: android.net.Uri.EMPTY,
            path = path,
            dateAdded = 0,
            size = 0
        )
    }

    fun playAt(index: Int) {
        playerController.controller?.seekTo(index, 0)
    }

    fun removeAt(index: Int) {
        playerController.controller?.removeMediaItem(index)
    }

    fun clearQueue() {
        playerController.controller?.clearMediaItems()
    }
}
