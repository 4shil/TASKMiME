package com.fourshil.musicya.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.fourshil.musicya.MainActivity
import com.fourshil.musicya.data.db.MusicDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicService : MediaSessionService() {

    @Inject lateinit var audioEngine: AudioEngine
    @Inject lateinit var musicDao: MusicDao

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()
        
        // Build ExoPlayer with audio focus handling
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true // Handle audio focus
            )
            .setHandleAudioBecomingNoisy(true) // Pause when headphones unplugged
            .build()
            
        // Attach Audio Engine to the player's session
        player?.let {
            serviceScope.launch {
                audioEngine.attach(it.audioSessionId)
            }
            
            // Player listener for audio session changes and play tracking
            it.addListener(object : Player.Listener {
                override fun onAudioSessionIdChanged(audioSessionId: Int) {
                    serviceScope.launch {
                        audioEngine.attach(audioSessionId)
                    }
                }
                
                // Track song plays when media transitions
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    if (mediaItem != null && reason != Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
                        val songId = mediaItem.mediaId.toLongOrNull()
                        if (songId != null) {
                            serviceScope.launch(Dispatchers.IO) {
                                musicDao.recordPlay(songId)
                            }
                        }
                    }
                }
            })
        }
        
        // Create a PendingIntent to launch MainActivity
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Build MediaSession
        mediaSession = MediaSession.Builder(this, player!!)
            .setSessionActivity(pendingIntent)
            .build()
            
        // Essential: Set notification provider to ensure Foreground Service behavior
        setMediaNotificationProvider(androidx.media3.session.DefaultMediaNotificationProvider(this))
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player?.release()
            release()
            mediaSession = null
        }
        serviceScope.cancel()
        audioEngine.release() // Release effects
        super.onDestroy()
    }
}
