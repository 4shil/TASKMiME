package com.fourshil.musicya.lyrics

data class LyricsLine(
    val timestamp: Long,
    val content: String
) : Comparable<LyricsLine> {
    override fun compareTo(other: LyricsLine): Int = timestamp.compareTo(other.timestamp)
}
