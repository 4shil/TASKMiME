package com.fourshil.musicya.player

import android.util.Log
import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages "Seamless Gapless Fade" transitions between tracks.
 * 
 * Instead of attempting a "True Crossfade" (which requires two active players and is impossible 
 * with a single ExoPlayer instance), this manager implements a professional 
 * Fade-Out -> Gapless Switch -> Fade-In transition.
 * 
 * ## Behavior
 * 1. **Fade Out**: As the track ends, volume smoothly decreases to 0.
 * 2. **Native switch**: ExoPlayer switches tracks gaplessly while volume is 0.
 * 3. **Fade In**: The new track starts at 0 volume and fades up to 100%.
 * 
 * This creates a smooth "mix" feel without the jarring cut of the previous implementation.
 * 
 * ## Performance
 * Uses smart scheduling - only polls when within fade window instead of continuous polling.
 */
@Singleton
class CrossfadeManager @Inject constructor() {
    
    companion object {
        private const val TAG = "CrossfadeManager"
        private const val FADE_INTERVAL_MS = 50L // ~20fps for smooth volume change
        private const val CHECK_INTERVAL_MS = 1000L // Check once per second when not in fade window
    }
    
    private var player: Player? = null
    private var scope: CoroutineScope? = null
    private var fadeOutJob: Job? = null
    private var fadeInJob: Job? = null
    private var playerListener: Player.Listener? = null
    
    private val _durationSeconds = MutableStateFlow(0)
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()
    
    val isEnabled: Boolean
        get() = _durationSeconds.value > 0

    fun initialize(player: Player, scope: CoroutineScope) {
        Log.d(TAG, "Initializing CrossfadeManager")
        this.player = player
        this.scope = scope
        
        // Use Player.Listener for smarter monitoring
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying && isEnabled) {
                    scheduleFadeOutCheck()
                } else {
                    fadeOutJob?.cancel()
                }
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY && isEnabled && player.isPlaying) {
                    scheduleFadeOutCheck()
                }
            }
        }
        playerListener = listener
        player.addListener(listener)
        
        if (isEnabled && player.isPlaying) {
            scheduleFadeOutCheck()
        }
    }
    
    fun setDuration(seconds: Int) {
        val newDuration = seconds.coerceIn(0, 12)
        val wasEnabled = isEnabled
        _durationSeconds.value = newDuration
        
        Log.d(TAG, "Crossfade duration set to $newDuration seconds")
        
        if (newDuration > 0 && !wasEnabled) {
            // Just enabled - start monitoring if playing
            if (player?.isPlaying == true) {
                scheduleFadeOutCheck()
            }
        } else if (newDuration == 0) {
            stopAll()
            // Reset volume if disabled
            player?.volume = 1f
        }
    }
    
    /**
     * Smart scheduling: Calculate when we need to start fading and schedule accordingly.
     */
    private fun scheduleFadeOutCheck() {
        if (fadeOutJob?.isActive == true) return
        val p = player ?: return
        val s = scope ?: return
        
        fadeOutJob = s.launch {
            while (isActive && isEnabled) {
                val duration = p.duration
                if (duration == androidx.media3.common.C.TIME_UNSET || duration <= 0 || !p.isPlaying) {
                    delay(CHECK_INTERVAL_MS)
                    continue
                }
                
                val position = p.currentPosition
                val crossfadeMs = _durationSeconds.value * 1000L
                val timeRemaining = duration - position
                
                // If no next track, just wait
                if (!p.hasNextMediaItem()) {
                    if (p.volume != 1f) p.volume = 1f
                    delay(CHECK_INTERVAL_MS)
                    continue
                }
                
                if (timeRemaining > crossfadeMs + 500) {
                    // Not near fade window - sleep until closer
                    val sleepTime = (timeRemaining - crossfadeMs - 200).coerceIn(100, CHECK_INTERVAL_MS)
                    delay(sleepTime)
                } else if (timeRemaining > 0 && timeRemaining <= crossfadeMs) {
                    // In fade window - do smooth fade out
                    if (fadeInJob?.isActive == true) {
                        fadeInJob?.cancel()
                        fadeInJob = null
                    }
                    
                    val fadeProgress = 1f - (timeRemaining.toFloat() / crossfadeMs)
                    val volume = (1f - fadeProgress).coerceIn(0f, 1f)
                    p.volume = volume
                    delay(FADE_INTERVAL_MS)
                } else {
                    // Restore volume if not fading in
                    if (fadeInJob?.isActive != true && p.volume != 1f) {
                        p.volume = 1f
                    }
                    delay(FADE_INTERVAL_MS)
                }
            }
        }
    }
    
    private fun stopAll() {
        fadeOutJob?.cancel()
        fadeOutJob = null
        fadeInJob?.cancel()
        fadeInJob = null
    }
    
    /**
     * Called when a new track starts. Triggers the fade-in.
     */
    fun onTrackStarted() {
        val p = player ?: return
        val s = scope ?: return
        
        if (!isEnabled) {
            // Ensure volume is at 100% when crossfade is disabled
            try {
                p.volume = 1f
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set volume", e)
            }
            return
        }
        
        // Cancel any pending fade-in
        fadeInJob?.cancel()
        
        fadeInJob = s.launch {
            try {
                val crossfadeMs = _durationSeconds.value * 1000L
                if (crossfadeMs <= 0) {
                    p.volume = 1f
                    return@launch
                }
                
                // Start completely silent for the seamless transition
                p.volume = 0f
                
                val fadeInDuration = crossfadeMs
                val startTime = System.currentTimeMillis()
                
                Log.d(TAG, "Starting fade-in for ${fadeInDuration}ms")
                
                while (isActive) {
                    val elapsed = System.currentTimeMillis() - startTime
                    val progress = (elapsed.toFloat() / fadeInDuration).coerceIn(0f, 1f)
                    
                    // Linear fade in: 0.0 -> 1.0
                    p.volume = progress
                    
                    if (progress >= 1f) {
                        break
                    }
                    
                    delay(FADE_INTERVAL_MS) 
                }
                
                p.volume = 1f
            } catch (e: Exception) {
                Log.e(TAG, "Fade-in failed, restoring volume", e)
                try {
                    p.volume = 1f
                } catch (ignored: Exception) {}
            }
        }
    }
    
    fun release() {
        stopAll()
        // Remove listener
        playerListener?.let { player?.removeListener(it) }
        playerListener = null
        // Ensure volume is restored on release
        player?.volume = 1f
        player = null
        scope = null
    }
}
