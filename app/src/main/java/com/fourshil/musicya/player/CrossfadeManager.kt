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
 * Manages crossfade transitions between tracks.
 * 
 * True crossfade behavior: the next song starts playing BEFORE the current song ends,
 * so both songs overlap briefly during the transition.
 * 
 * ## How it works:
 * 1. Monitors playback position continuously
 * 2. When approaching track end (within crossfade duration):
 *    - Starts fading out current song volume
 *    - At the midpoint of fade, triggers skip to next track
 *    - New track starts with fade-in from low volume
 * 3. The overlap creates the crossfade effect
 */
@Singleton
class CrossfadeManager @Inject constructor() {
    
    companion object {
        private const val TAG = "CrossfadeManager"
        private const val MONITOR_INTERVAL_MS = 50L // Very frequent for smooth fade
    }
    
    private var player: Player? = null
    private var scope: CoroutineScope? = null
    private var monitorJob: Job? = null
    private var fadeInJob: Job? = null
    
    // State tracking
    private var isFadingOut = false
    private var hasTriggeredSkip = false
    
    private val _durationSeconds = MutableStateFlow(0)
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()
    
    val isEnabled: Boolean
        get() = _durationSeconds.value > 0

    /**
     * Initialize the manager with player and coroutine scope.
     */
    fun initialize(player: Player, scope: CoroutineScope) {
        Log.d(TAG, "Initializing CrossfadeManager")
        this.player = player
        this.scope = scope
        
        if (isEnabled) {
            startMonitoring()
        }
    }
    
    /**
     * Set crossfade duration.
     * @param seconds Duration in seconds (0-12). 0 disables crossfade.
     */
    fun setDuration(seconds: Int) {
        val newDuration = seconds.coerceIn(0, 12)
        _durationSeconds.value = newDuration
        
        Log.d(TAG, "Crossfade duration set to $newDuration seconds")
        
        if (newDuration > 0) {
            startMonitoring()
        } else {
            stopAll()
            player?.volume = 1f
        }
    }
    
    private fun startMonitoring() {
        if (player == null || scope == null) {
            Log.w(TAG, "Cannot start monitoring - not initialized")
            return
        }
        
        // Don't restart if already monitoring
        if (monitorJob?.isActive == true) return
        
        Log.d(TAG, "Starting crossfade monitoring")
        monitorJob = scope?.launch {
            while (isActive && isEnabled) {
                checkCrossfade()
                delay(MONITOR_INTERVAL_MS)
            }
        }
    }
    
    private fun stopAll() {
        monitorJob?.cancel()
        monitorJob = null
        fadeInJob?.cancel()
        fadeInJob = null
        isFadingOut = false
        hasTriggeredSkip = false
    }
    
    private fun checkCrossfade() {
        val p = player ?: return
        
        // Only check during active playback
        if (!p.isPlaying) {
            return
        }
        
        val duration = p.duration
        val position = p.currentPosition
        val crossfadeMs = _durationSeconds.value * 1000L
        
        // Validate
        if (duration <= 0 || crossfadeMs <= 0) return
        
        // Need at least one more track in queue
        val hasNextTrack = p.hasNextMediaItem()
        if (!hasNextTrack) {
            // No next track - reset state
            if (isFadingOut) {
                isFadingOut = false
                hasTriggeredSkip = false
                p.volume = 1f
            }
            return
        }
        
        val timeRemaining = duration - position
        
        // Check if we're in the crossfade window
        if (timeRemaining > 0 && timeRemaining <= crossfadeMs) {
            // Calculate fade progress (0.0 at start of fade window, 1.0 at end)
            val fadeProgress = 1f - (timeRemaining.toFloat() / crossfadeMs)
            
            // Apply fade-out: volume goes from 1.0 to 0.0
            val volume = (1f - fadeProgress).coerceIn(0f, 1f)
            p.volume = volume
            
            if (!isFadingOut) {
                isFadingOut = true
                hasTriggeredSkip = false
                Log.d(TAG, "Started crossfade, ${timeRemaining}ms remaining")
            }
            
            // At 50% through the fade (halfway point), skip to next track
            // This makes the next song start while current is at 50% volume
            if (!hasTriggeredSkip && fadeProgress >= 0.5f) {
                hasTriggeredSkip = true
                Log.d(TAG, "Triggering early skip to next track")
                p.seekToNext()
            }
        } else if (timeRemaining > crossfadeMs) {
            // Not in crossfade window - ensure full volume
            if (isFadingOut) {
                isFadingOut = false
                hasTriggeredSkip = false
                p.volume = 1f
            }
        }
    }
    
    /**
     * Call when a new track starts to perform fade-in.
     */
    fun onTrackStarted() {
        val p = player ?: return
        val s = scope ?: return
        
        if (!isEnabled) {
            p.volume = 1f
            return
        }
        
        // Cancel any existing fade-in
        fadeInJob?.cancel()
        
        Log.d(TAG, "New track started - beginning fade-in")
        
        // Reset fade state
        isFadingOut = false
        hasTriggeredSkip = false
        
        // Start at low volume and fade up
        val startVolume = if (isFadingOut) 0.3f else 0f
        p.volume = startVolume
        
        fadeInJob = s.launch {
            val crossfadeMs = _durationSeconds.value * 1000L
            if (crossfadeMs <= 0) {
                p.volume = 1f
                return@launch
            }
            
            // Fade-in over half the crossfade duration
            val fadeInMs = crossfadeMs / 2
            val startTime = System.currentTimeMillis()
            
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / fadeInMs).coerceIn(0f, 1f)
                
                // Fade from startVolume to 1.0
                p.volume = startVolume + (1f - startVolume) * progress
                
                if (progress >= 1f) {
                    Log.d(TAG, "Fade-in complete")
                    break
                }
                
                delay(50)
            }
            
            p.volume = 1f
        }
    }
    
    /**
     * Release all resources.
     */
    fun release() {
        Log.d(TAG, "Releasing CrossfadeManager")
        stopAll()
        player?.volume = 1f
        player = null
        scope = null
    }
}
