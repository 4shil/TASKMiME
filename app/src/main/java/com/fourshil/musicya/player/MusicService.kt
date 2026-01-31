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
import com.fourshil.musicya.data.SettingsPreferences
import com.fourshil.musicya.data.db.MusicDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Background media playback service using Media3.
 * 
 * Features:
 * - Gapless playback
 * - Audio focus handling
 * - Equalizer integration
 * - Play history tracking
 * - Crossfade support (loaded from settings)
 */
@AndroidEntryPoint
class MusicService : MediaSessionService() {

    @Inject lateinit var audioEngine: AudioEngine
    @Inject lateinit var musicDao: MusicDao
    @Inject lateinit var crossfadeManager: CrossfadeManager
    @Inject lateinit var settingsPreferences: SettingsPreferences

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()
        
        try {
            // Build ExoPlayer with audio focus handling and gapless playback
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
                .apply {
                    // Enable gapless playback
                    pauseAtEndOfMediaItems = false
                    // Ensure volume starts at 100%
                    volume = 1f
                }
        } catch (e: Exception) {
            android.util.Log.e("MusicService", "Failed to create ExoPlayer", e)
            return
        }
            
        // Attach Audio Engine and Crossfade Manager
        player?.let { exoPlayer ->
            serviceScope.launch {
                try {
                    audioEngine.attach(exoPlayer.audioSessionId)
                } catch (e: Exception) {
                    android.util.Log.e("MusicService", "Failed to attach audio engine", e)
                }
            }
            
            // Initialize crossfade manager
            try {
                crossfadeManager.initialize(exoPlayer, serviceScope)
            } catch (e: Exception) {
                android.util.Log.e("MusicService", "Failed to initialize crossfade manager", e)
            }
            
            // Load crossfade duration from preferences
            serviceScope.launch {
                try {
                    val savedDuration = settingsPreferences.crossfadeDuration.first()
                    crossfadeManager.setDuration(savedDuration)
                    android.util.Log.d("MusicService", "Loaded crossfade duration: ${savedDuration}s")
                } catch (e: Exception) {
                    android.util.Log.e("MusicService", "Failed to load crossfade duration", e)
                }
            }
            
            // Observe crossfade changes from settings
            serviceScope.launch {
                try {
                    settingsPreferences.crossfadeDuration.collect { duration ->
                        crossfadeManager.setDuration(duration)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MusicService", "Failed to observe crossfade changes", e)
                }
            }
            
            // Player listener for audio session changes and play tracking
            exoPlayer.addListener(object : Player.Listener {
                override fun onAudioSessionIdChanged(audioSessionId: Int) {
                    serviceScope.launch {
                        try {
                            audioEngine.attach(audioSessionId)
                        } catch (e: Exception) {
                            android.util.Log.e("MusicService", "Failed to attach audio engine on session change", e)
                        }
                    }
                }
                
                // Track song plays and trigger crossfade fade-in
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    if (mediaItem != null) {
                        // Trigger crossfade fade-in on new track
                        try {
                            crossfadeManager.onTrackStarted()
                        } catch (e: Exception) {
                            android.util.Log.e("MusicService", "Failed to trigger crossfade on track start", e)
                        }
                        
                        if (reason != Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
                            val songId = mediaItem.mediaId.toLongOrNull()
                            if (songId != null) {
                                serviceScope.launch(Dispatchers.IO) {
                                    try {
                                        musicDao.recordPlay(songId)
                                    } catch (e: Exception) {
                                        android.util.Log.e("MusicService", "Failed to record play", e)
                                    }
                                }
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
        
        // Build MediaSession (only if player was created successfully)
        val currentPlayer = player
        if (currentPlayer != null) {
            mediaSession = MediaSession.Builder(this, currentPlayer)
                .setSessionActivity(pendingIntent)
                .build()
        } else {
            android.util.Log.e("MusicService", "Player is null, cannot create MediaSession")
            return
        }
            
        // Essential: Set notification provider for Foreground Service
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
            player.release()
            release()
            mediaSession = null
        }
        serviceScope.cancel()
        crossfadeManager.release()
        audioEngine.release()
        super.onDestroy()
    }
}
