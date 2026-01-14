package com.fourshil.musicya.player

import android.content.Intent
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import com.fourshil.musicya.audiofx.AudioEffectController
import javax.inject.Inject

@AndroidEntryPoint
class AudioService : MediaLibraryService() {

    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var audioEffectController: AudioEffectController

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, player).build()
        
        // Initialize Effects
        player.addListener(object : Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                if (audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
                    audioEffectController.initialize(audioSessionId)
                }
            }
        })
        // Trigger if already set (though often 0 until playback)
        if (player.audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
            audioEffectController.initialize(player.audioSessionId)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        audioEffectController.release()
        super.onDestroy()
    }

    // Task removed confirmation helps preventing service not being killed
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player?.playWhenReady == false || player?.mediaItemCount == 0) {
            stopSelf()
        }
    }
}
