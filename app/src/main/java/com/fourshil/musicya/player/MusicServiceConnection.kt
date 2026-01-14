package com.fourshil.musicya.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicServiceConnection @Inject constructor(
    @ApplicationContext context: Context
) {
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    val player: Player?
        get() = if (mediaControllerFuture?.isDone == true) mediaControllerFuture?.get() else null

    init {
        val sessionToken = SessionToken(context, ComponentName(context, AudioService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture?.addListener({
            // Controller is ready
        }, MoreExecutors.directExecutor())
    }

    fun release() {
        MediaController.releaseFuture(mediaControllerFuture!!)
    }
}
