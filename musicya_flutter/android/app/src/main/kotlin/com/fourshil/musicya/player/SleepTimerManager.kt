package com.fourshil.musicya.player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages sleep timer functionality for the music player.
 * 
 * The sleep timer allows users to automatically pause playback after a set duration.
 * This is useful for falling asleep to music without leaving it playing all night.
 *
 * @property scope The coroutine scope for timer operations
 * @property onTimerComplete Callback invoked when the timer expires (should pause playback)
 */
@Singleton
class SleepTimerManager @Inject constructor() {
    
    private lateinit var scope: CoroutineScope
    private var onTimerComplete: (() -> Unit)? = null
    private var timerJob: Job? = null
    
    private val _remainingMs = MutableStateFlow(0L)
    
    /**
     * Remaining time in milliseconds until the timer expires.
     * Emits 0 when no timer is active.
     */
    val remainingMs = _remainingMs.asStateFlow()
    
    /**
     * Initialize the manager with required dependencies.
     * Must be called before using any timer functions.
     *
     * @param scope Coroutine scope for timer job
     * @param onComplete Callback when timer finishes (typically pauses playback)
     */
    fun initialize(scope: CoroutineScope, onComplete: () -> Unit) {
        this.scope = scope
        this.onTimerComplete = onComplete
    }
    
    /**
     * Start a sleep timer for the specified duration.
     * 
     * If a timer is already running, it will be cancelled and replaced.
     * The timer counts down in 1-second intervals and invokes the completion
     * callback when it reaches zero.
     *
     * @param minutes Duration in minutes. Pass 0 to cancel any active timer.
     */
    fun setTimer(minutes: Int) {
        cancel()
        if (minutes <= 0) return
        
        val durationMs = minutes * 60 * 1000L
        _remainingMs.value = durationMs
        
        timerJob = scope.launch {
            var remaining = durationMs
            while (remaining > 0 && isActive) {
                delay(1000)
                remaining -= 1000
                _remainingMs.value = remaining
            }
            if (isActive) {
                onTimerComplete?.invoke()
                _remainingMs.value = 0
            }
        }
    }
    
    /**
     * Cancel the active sleep timer.
     * Has no effect if no timer is running.
     */
    fun cancel() {
        timerJob?.cancel()
        timerJob = null
        _remainingMs.value = 0
    }
    
    /**
     * Check if a sleep timer is currently active.
     * @return true if timer is running and has time remaining
     */
    fun isActive(): Boolean = timerJob?.isActive == true
    
    /**
     * Release resources. Call when the player is being destroyed.
     */
    fun release() {
        cancel()
        onTimerComplete = null
    }
}
