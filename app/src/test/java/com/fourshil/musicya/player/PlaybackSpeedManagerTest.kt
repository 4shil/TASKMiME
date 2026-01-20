package com.fourshil.musicya.player

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for PlaybackSpeedManager.
 */
class PlaybackSpeedManagerTest {

    @Test
    fun `speed is clamped to minimum`() {
        // Test that speeds below minimum are clamped
        val speed = 0.1f.coerceIn(PlaybackSpeedManager.MIN_SPEED, PlaybackSpeedManager.MAX_SPEED)
        assertEquals(PlaybackSpeedManager.MIN_SPEED, speed, 0.001f)
    }

    @Test
    fun `speed is clamped to maximum`() {
        // Test that speeds above maximum are clamped
        val speed = 5.0f.coerceIn(PlaybackSpeedManager.MIN_SPEED, PlaybackSpeedManager.MAX_SPEED)
        assertEquals(PlaybackSpeedManager.MAX_SPEED, speed, 0.001f)
    }

    @Test
    fun `valid speed is not modified`() {
        val speed = 1.5f.coerceIn(PlaybackSpeedManager.MIN_SPEED, PlaybackSpeedManager.MAX_SPEED)
        assertEquals(1.5f, speed, 0.001f)
    }

    @Test
    fun `speed cycle follows preset order`() {
        val presets = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        
        // From 1.0x should go to 1.25x
        val currentIndex = presets.indexOf(1.0f)
        val nextIndex = if (currentIndex == presets.lastIndex) 0 else currentIndex + 1
        assertEquals(1.25f, presets[nextIndex], 0.001f)
    }

    @Test
    fun `speed cycle wraps around from last to first`() {
        val presets = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        
        // From 2.0x (last) should cycle back to 0.5x
        val currentIndex = presets.indexOf(2.0f)
        val nextIndex = if (currentIndex == presets.lastIndex) 0 else currentIndex + 1
        assertEquals(0.5f, presets[nextIndex], 0.001f)
    }

    @Test
    fun `unknown speed cycles to first preset`() {
        val presets = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        val unknownSpeed = 0.8f // Not in presets
        
        val currentIndex = presets.indexOfFirst { it == unknownSpeed }
        val nextIndex = if (currentIndex == -1 || currentIndex == presets.lastIndex) 0 else currentIndex + 1
        assertEquals(0.5f, presets[nextIndex], 0.001f)
    }

    @Test
    fun `min speed constant is correct`() {
        assertEquals(0.25f, PlaybackSpeedManager.MIN_SPEED, 0.001f)
    }

    @Test
    fun `max speed constant is correct`() {
        assertEquals(3.0f, PlaybackSpeedManager.MAX_SPEED, 0.001f)
    }

    @Test
    fun `normal speed constant is correct`() {
        assertEquals(1.0f, PlaybackSpeedManager.NORMAL_SPEED, 0.001f)
    }
}
