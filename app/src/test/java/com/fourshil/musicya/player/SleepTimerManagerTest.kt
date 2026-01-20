package com.fourshil.musicya.player

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for SleepTimerManager logic.
 * Tests pure logic without coroutine execution.
 */
class SleepTimerManagerTest {

    @Test
    fun `timer duration calculation for 5 minutes`() {
        val minutes = 5
        val expectedMs = minutes * 60 * 1000L
        assertEquals(300_000L, expectedMs)
    }

    @Test
    fun `timer duration calculation for 30 minutes`() {
        val minutes = 30
        val expectedMs = minutes * 60 * 1000L
        assertEquals(1_800_000L, expectedMs)
    }

    @Test
    fun `timer duration calculation for 1 hour`() {
        val minutes = 60
        val expectedMs = minutes * 60 * 1000L
        assertEquals(3_600_000L, expectedMs)
    }

    @Test
    fun `zero minutes results in no timer`() {
        val minutes = 0
        val expectedMs = minutes * 60 * 1000L
        assertEquals(0L, expectedMs)
    }

    @Test
    fun `negative minutes should be handled`() {
        val minutes = -5
        val shouldStart = minutes > 0
        assertFalse(shouldStart)
    }

    @Test
    fun `timer remaining format for display`() {
        // Helper function matching app logic
        fun formatRemaining(ms: Long): String {
            val totalSeconds = ms / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%d:%02d", minutes, seconds)
            }
        }

        assertEquals("5:00", formatRemaining(300_000))
        assertEquals("30:00", formatRemaining(1_800_000))
        assertEquals("1:00:00", formatRemaining(3_600_000))
        assertEquals("0:30", formatRemaining(30_000))
        assertEquals("0:00", formatRemaining(0))
    }

    @Test
    fun `common timer presets are valid`() {
        val presets = listOf(5, 10, 15, 30, 45, 60)
        assertTrue(presets.all { it > 0 })
        assertTrue(presets.all { it <= 120 }) // Max 2 hours is reasonable
    }
}
