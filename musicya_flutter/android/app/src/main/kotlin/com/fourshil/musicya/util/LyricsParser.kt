package com.fourshil.musicya.util

import java.io.File
import java.util.regex.Pattern

/**
 * Represents a single line of lyrics with its timestamp.
 */
data class LyricLine(
    val timeMs: Long,
    val text: String
)

/**
 * Parsed lyrics with metadata and timed lines.
 */
data class Lyrics(
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val lines: List<LyricLine> = emptyList()
) {
    /**
     * Find the current lyric line for a given position.
     */
    fun getCurrentLineIndex(positionMs: Long): Int {
        if (lines.isEmpty()) return -1
        
        for (i in lines.indices.reversed()) {
            if (lines[i].timeMs <= positionMs) {
                return i
            }
        }
        return 0
    }
    
    companion object {
        val EMPTY = Lyrics()
    }
}

/**
 * Parser for LRC (Lyric) files.
 * Supports standard LRC format with timestamps like [mm:ss.xx] or [mm:ss:xx]
 */
object LyricsParser {
    
    // Pattern for timestamp: [mm:ss.xx] or [mm:ss:xx] or [mm:ss]
    private val TIMESTAMP_PATTERN = Pattern.compile("\\[(\\d{1,2}):(\\d{2})([.:](\\d{1,3}))?\\]")
    
    // Pattern for metadata tags: [tag:value]
    private val METADATA_PATTERN = Pattern.compile("\\[([a-zA-Z]+):(.+?)\\]")
    
    /**
     * Parse LRC content from a string.
     */
    fun parse(content: String): Lyrics {
        var title: String? = null
        var artist: String? = null
        var album: String? = null
        val lines = mutableListOf<LyricLine>()
        
        content.lines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) return@forEach
            
            // Check for metadata
            val metaMatcher = METADATA_PATTERN.matcher(trimmed)
            if (metaMatcher.find() && !trimmed.contains(Regex("\\[\\d"))) {
                when (metaMatcher.group(1)?.lowercase()) {
                    "ti" -> title = metaMatcher.group(2)?.trim()
                    "ar" -> artist = metaMatcher.group(2)?.trim()
                    "al" -> album = metaMatcher.group(2)?.trim()
                }
                return@forEach
            }
            
            // Parse timestamped lyrics
            val timestamps = mutableListOf<Long>()
            val timestampMatcher = TIMESTAMP_PATTERN.matcher(trimmed)
            var lastEnd = 0
            
            while (timestampMatcher.find()) {
                val minutes = timestampMatcher.group(1)?.toLongOrNull() ?: 0
                val seconds = timestampMatcher.group(2)?.toLongOrNull() ?: 0
                val centiseconds = timestampMatcher.group(4)?.let { 
                    // Handle both .xx (centiseconds) and .xxx (milliseconds)
                    when (it.length) {
                        1 -> it.toLongOrNull()?.times(100) ?: 0
                        2 -> it.toLongOrNull()?.times(10) ?: 0
                        3 -> it.toLongOrNull() ?: 0
                        else -> 0
                    }
                } ?: 0
                
                val timeMs = (minutes * 60 * 1000) + (seconds * 1000) + centiseconds
                timestamps.add(timeMs)
                lastEnd = timestampMatcher.end()
            }
            
            // Get the text after all timestamps
            val text = trimmed.substring(lastEnd).trim()
            
            // Create a LyricLine for each timestamp (some LRC files have multiple timestamps per line)
            timestamps.forEach { time ->
                if (text.isNotEmpty()) {
                    lines.add(LyricLine(time, text))
                }
            }
        }
        
        // Sort by timestamp
        lines.sortBy { it.timeMs }
        
        return Lyrics(
            title = title,
            artist = artist,
            album = album,
            lines = lines
        )
    }
    
    /**
     * Parse LRC content from a file.
     */
    fun parseFile(file: File): Lyrics? {
        return try {
            if (file.exists() && file.canRead()) {
                parse(file.readText())
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
