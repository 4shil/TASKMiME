package com.fourshil.musicya.player

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
 * Since Media3 ExoPlayer doesn't natively support crossfade, this manager
 * provides a software-based approach by gradually adjusting volume during
 * track transitions.
 *
 * ## How it works:
 * 1. Monitors playback position relative to track duration
 * 2. When approaching end (within crossfade duration), starts fade-out
 * 3. Triggers next track and performs fade-in
 *
 * Note: This is a simplified implementation. True crossfade requires
 * mixing two audio streams simultaneously, which is not possible with
 * a single ExoPlayer instance without custom audio processing.
 */
@Singleton
class CrossfadeManager @Inject constructor() {
    
    private var player: Player? = null
    private var scope: CoroutineScope? = null
    private var crossfadeJob: Job? = null
    
    private val _durationSeconds = MutableStateFlow(0)
    
    /**
     * Crossfade duration in seconds (0 = disabled).
     */
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()
    
    /**
     * Whether crossfade is currently enabled.
     */
    val isEnabled: Boolean
        get() = _durationSeconds.value > 0
    
    /**
     * Initialize the manager with player and coroutine scope.
     * 
     * @param player The ExoPlayer instance
     * @param scope Coroutine scope for monitoring
     */
    fun initialize(player: Player, scope: CoroutineScope) {
        this.player = player
        this.scope = scope
        startMonitoring()
    }
    
    /**
     * Set crossfade duration.
     * 
     * @param seconds Duration in seconds (0-12). 0 disables crossfade.
     */
    fun setDuration(seconds: Int) {
        _durationSeconds.value = seconds.coerceIn(0, 12)
        
        if (seconds > 0) {
            startMonitoring()
        } else {
            stopMonitoring()
        }
    }
    
    private fun startMonitoring() {
        if (!isEnabled) return
        stopMonitoring()
        
        crossfadeJob = scope?.launch {
            while (isActive && isEnabled) {
                checkForCrossfade()
                delay(500) // Check every 500ms
            }
        }
    }
    
    private fun stopMonitoring() {
        crossfadeJob?.cancel()
        crossfadeJob = null
    }
    
    private suspend fun checkForCrossfade() {
        val p = player ?: return
        if (!p.isPlaying) return
        
        val duration = p.duration
        val position = p.currentPosition
        val crossfadeMs = _durationSeconds.value * 1000L
        
        if (duration <= 0 || crossfadeMs <= 0) return
        
        val timeRemaining = duration - position
        
        // Start fade-out when within crossfade window
        if (timeRemaining in 1..crossfadeMs) {
            val fadeProgress = 1f - (timeRemaining.toFloat() / crossfadeMs)
            applyFadeOut(fadeProgress)
        } else {
            // Reset volume if not in crossfade window
            p.volume = 1f
        }
    }
    
    private fun applyFadeOut(progress: Float) {
        val p = player ?: return
        // Smooth fade: 1.0 -> 0.0 over crossfade duration
        val volume = (1f - progress).coerceIn(0f, 1f)
        p.volume = volume
    }
    
    /**
     * Call when a new track starts to perform fade-in.
     */
    fun onTrackStarted() {
        if (!isEnabled) return
        
        scope?.launch {
            performFadeIn()
        }
    }
    
    private suspend fun performFadeIn() {
        val p = player ?: return
        val crossfadeMs = _durationSeconds.value * 1000L
        if (crossfadeMs <= 0) return
        
        val steps = 20
        val stepDelay = crossfadeMs / steps
        
        for (i in 0..steps) {
            if (crossfadeJob?.isActive != true) break
            val volume = i.toFloat() / steps
            p.volume = volume
            delay(stepDelay)
        }
        p.volume = 1f
    }
    
    /**
     * Release resources.
     */
    fun release() {
        stopMonitoring()
        player?.volume = 1f
        player = null
        scope = null
    }
}
