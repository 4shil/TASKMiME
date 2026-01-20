package com.fourshil.musicya.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages playback speed control for the music player.
 * 
 * Provides functionality to set, cycle, and reset playback speed
 * with proper validation and state management.
 */
@Singleton
class PlaybackSpeedManager @Inject constructor() {
    
    private var onSpeedChange: ((Float) -> Unit)? = null
    
    private val _speed = MutableStateFlow(1.0f)
    
    /**
     * Current playback speed as a state flow.
     * Normal speed is 1.0. Range is 0.25 to 3.0.
     */
    val speed = _speed.asStateFlow()
    
    /**
     * Preset speed values for cycling through common speeds.
     */
    private val speedPresets = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    
    companion object {
        /** Minimum allowed playback speed */
        const val MIN_SPEED = 0.25f
        
        /** Maximum allowed playback speed */
        const val MAX_SPEED = 3.0f
        
        /** Default/normal playback speed */
        const val NORMAL_SPEED = 1.0f
    }
    
    /**
     * Initialize the manager with the speed change callback.
     * 
     * @param onSpeedChange Callback invoked when speed changes (to update MediaController)
     */
    fun initialize(onSpeedChange: (Float) -> Unit) {
        this.onSpeedChange = onSpeedChange
    }
    
    /**
     * Set playback speed to a specific value.
     * The value will be clamped to the valid range [0.25, 3.0].
     *
     * @param newSpeed The desired playback speed
     */
    fun setSpeed(newSpeed: Float) {
        val clampedSpeed = newSpeed.coerceIn(MIN_SPEED, MAX_SPEED)
        _speed.value = clampedSpeed
        onSpeedChange?.invoke(clampedSpeed)
    }
    
    /**
     * Cycle through common playback speed presets.
     * 
     * Cycles in order: 0.5x → 0.75x → 1.0x → 1.25x → 1.5x → 2.0x → 0.5x
     * If the current speed doesn't match a preset, cycles to 0.5x.
     */
    fun cycleSpeed() {
        val currentIndex = speedPresets.indexOfFirst { it == _speed.value }
        val nextIndex = if (currentIndex == -1 || currentIndex == speedPresets.lastIndex) {
            0
        } else {
            currentIndex + 1
        }
        setSpeed(speedPresets[nextIndex])
    }
    
    /**
     * Reset playback speed to normal (1.0x).
     */
    fun reset() {
        setSpeed(NORMAL_SPEED)
    }
    
    /**
     * Get a formatted display string for the current speed.
     * @return Speed formatted as "1.0x", "1.5x", etc.
     */
    fun getDisplayString(): String {
        val value = _speed.value
        return if (value == value.toInt().toFloat()) {
            "${value.toInt()}.0x"
        } else {
            "${value}x"
        }
    }
    
    /**
     * Release resources.
     */
    fun release() {
        onSpeedChange = null
    }
}
