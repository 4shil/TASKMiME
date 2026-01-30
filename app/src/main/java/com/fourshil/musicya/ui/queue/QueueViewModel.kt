package com.fourshil.musicya.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel() {

    // Use reactive flows from PlayerController directly
    val queue = playerController.queue
    val currentIndex = playerController.currentQueueIndex

    init {
        playerController.connect()
        // No more polling!
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
