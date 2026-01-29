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
import kotlin.math.pow

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
 */
@Singleton
class CrossfadeManager @Inject constructor() {
    
    companion object {
        private const val TAG = "CrossfadeManager"
        private const val MONITOR_INTERVAL_MS = 200L // Slower check is fine, we just need to catch the window
        private const val FADE_IN_INTERVAL_MS = 16L // ~60fps for smooth volume change
    }
    
    private var player: Player? = null
    private var scope: CoroutineScope? = null
    private var monitorJob: Job? = null
    private var fadeInJob: Job? = null
    
    private val _durationSeconds = MutableStateFlow(0)
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()
    
    val isEnabled: Boolean
        get() = _durationSeconds.value > 0

    fun initialize(player: Player, scope: CoroutineScope) {
        Log.d(TAG, "Initializing CrossfadeManager")
        this.player = player
        this.scope = scope
        
        if (isEnabled) {
            startMonitoring()
        }
    }
    
    fun setDuration(seconds: Int) {
        val newDuration = seconds.coerceIn(0, 12)
        _durationSeconds.value = newDuration
        
        Log.d(TAG, "Crossfade duration set to $newDuration seconds")
        
        if (newDuration > 0) {
            startMonitoring()
        } else {
            stopAll()
            // Reset volume if disabled
            player?.volume = 1f
        }
    }
    
    private fun startMonitoring() {
        if (player == null || scope == null) return
        if (monitorJob?.isActive == true) return
        
        monitorJob = scope?.launch {
            while (isActive) {
                if (isEnabled) {
                    checkFadeOut()
                }
                delay(MONITOR_INTERVAL_MS)
            }
        }
    }
    
    private fun stopAll() {
        monitorJob?.cancel()
        monitorJob = null
        fadeInJob?.cancel()
        fadeInJob = null
    }
    
    /**
     * Checks if we need to fade out the current track.
     */
    private fun checkFadeOut() {
        val p = player ?: return
        if (!p.isPlaying) return
        
        val duration = p.duration
        // Guard against unknown duration (e.g. streams or not prepared)
        if (duration == androidx.media3.common.C.TIME_UNSET || duration <= 0) return

        val position = p.currentPosition
        val crossfadeMs = _durationSeconds.value * 1000L
        
        if (crossfadeMs <= 0) return
        
        // Only fade out if there is a next track
        if (!p.hasNextMediaItem()) {
            if (p.volume != 1f) p.volume = 1f
            return
        }
        
        val timeRemaining = duration - position
        
        // If we are getting close to the end...
        if (timeRemaining > 0 && timeRemaining <= crossfadeMs) {
            // Cancel any fade-in if we are now fading out (rare conflict, but possible)
            if (fadeInJob?.isActive == true) {
                 fadeInJob?.cancel()
                 fadeInJob = null
            }

            // Calculate linear progress (0.0 to 1.0)
            // 0.0 at start of fade window, 1.0 at very end of track
            val fadeProgress = 1f - (timeRemaining.toFloat() / crossfadeMs)
            
            // Fade Volume: 1.0 -> 0.0
            val volume = (1f - fadeProgress).coerceIn(0f, 1f)
            p.volume = volume
        } 
        // If we are NOT in the fade out window...
        else if (timeRemaining > crossfadeMs) {
            // Restore volume ONLY if we are NOT currently fading in
            // This prevents the monitor from snapping volume to 1f while fade-in is running
            if (fadeInJob?.isActive != true && p.volume != 1f) {
                p.volume = 1f
            }
        }
    }
    
    /**
     * Called when a new track starts. Triggers the fade-in.
     */
    fun onTrackStarted() {
        val p = player ?: return
        val s = scope ?: return
        
        if (!isEnabled) {
            p.volume = 1f
            return
        }
        
        // Cancel any pending fade-in
        fadeInJob?.cancel()
        
        // Start completely silent for the seamless transition
        p.volume = 0f
        
        fadeInJob = s.launch {
            val crossfadeMs = _durationSeconds.value * 1000L
            if (crossfadeMs <= 0) {
                p.volume = 1f
                return@launch
            }
            
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
                
                delay(FADE_IN_INTERVAL_MS) 
            }
            
            p.volume = 1f
        }
    }
    
    fun release() {
        stopAll()
        // Ensure volume is restored on release
        player?.volume = 1f
        player = null
        scope = null
    }
}
