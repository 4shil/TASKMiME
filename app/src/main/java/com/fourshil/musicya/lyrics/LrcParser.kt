package com.fourshil.musicya.lyrics

import java.io.BufferedReader
import java.io.File
import java.io.StringReader
import java.util.regex.Pattern

object LrcParser {
    // Regex matches [mm:ss.xx] or [mm:ss.xxx] or [mm:ss]
    private val TIME_TAG_PATTERN = Pattern.compile("\\[(\\d+):(\\d{1,2})(?:\\.(\\d{1,3}))?]")

    fun parse(file: File): List<LyricsLine> {
        if (!file.exists()) return emptyList()
        return parse(file.readText())
    }

    fun parse(lrcContent: String): List<LyricsLine> {
        val lines = mutableListOf<LyricsLine>()
        val reader = BufferedReader(StringReader(lrcContent))
        
        var lineText: String? = reader.readLine()
        while (lineText != null) {
            parseLine(lineText, lines)
            lineText = reader.readLine()
        }
        
        return lines.sorted()
    }

    private fun parseLine(line: String, output: MutableList<LyricsLine>) {
        if (line.isBlank()) return
        
        val matcher = TIME_TAG_PATTERN.matcher(line)
        val timestamps = mutableListOf<Long>()
        
        // Find all initial timestamps [00:00.00][00:10.00] Line content
        var lastEndIndex = 0
        while (matcher.find()) {
            val min = matcher.group(1)?.toLongOrNull() ?: 0L
            val sec = matcher.group(2)?.toLongOrNull() ?: 0L
            val msStr = matcher.group(3)
            
            // Normalize ms: "10" (.10) -> 100ms, "100" -> 100ms, "1" -> 100ms usually? 
            // Typically .xx is centiseconds. .10 = 100ms. .5 = 500ms? 
            // Standard LRC is mm:ss.xx (centiseconds).
            val ms = if (msStr != null) {
                if (msStr.length == 2) msStr.toLong() * 10
                else if (msStr.length == 1) msStr.toLong() * 100
                else msStr.take(3).toLong()
            } else 0L

            val time = (min * 60 * 1000) + (sec * 1000) + ms
            timestamps.add(time)
            lastEndIndex = matcher.end()
        }

        if (timestamps.isEmpty()) return

        val content = line.substring(lastEndIndex).trim()
        
        // Add a line for every timestamp found (handles repeatable lines)
        for (time in timestamps) {
            output.add(LyricsLine(time, content))
        }
    }
}
