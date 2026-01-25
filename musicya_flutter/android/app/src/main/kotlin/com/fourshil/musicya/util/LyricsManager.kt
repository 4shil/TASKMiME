package com.fourshil.musicya.util

import android.content.Context
import com.fourshil.musicya.data.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages loading lyrics for songs.
 * Searches for LRC files in the same directory as the audio file.
 */
@Singleton
class LyricsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Find and load lyrics for a song.
     * Looks for .lrc file with same name as audio file.
     */
    suspend fun getLyricsForSong(song: Song): Lyrics? = withContext(Dispatchers.IO) {
        try {
            val audioFile = File(song.path)
            if (!audioFile.exists()) return@withContext null
            
            val parentDir = audioFile.parentFile ?: return@withContext null
            val baseName = audioFile.nameWithoutExtension
            
            // Try common LRC file naming patterns
            val lrcPatterns = listOf(
                "$baseName.lrc",
                "$baseName.LRC",
                "${song.title}.lrc",
                "${song.artist} - ${song.title}.lrc"
            )
            
            for (pattern in lrcPatterns) {
                val lrcFile = File(parentDir, pattern)
                if (lrcFile.exists() && lrcFile.canRead()) {
                    return@withContext LyricsParser.parseFile(lrcFile)
                }
            }
            
            // Try to find any .lrc file that might match (case insensitive)
            val lrcFiles = parentDir.listFiles { file -> 
                file.extension.lowercase() == "lrc" 
            }
            
            lrcFiles?.find { file ->
                file.nameWithoutExtension.equals(baseName, ignoreCase = true)
            }?.let { file ->
                return@withContext LyricsParser.parseFile(file)
            }
            
            null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Check if lyrics exist for a song without loading them.
     */
    suspend fun hasLyrics(song: Song): Boolean = withContext(Dispatchers.IO) {
        try {
            val audioFile = File(song.path)
            if (!audioFile.exists()) return@withContext false
            
            val parentDir = audioFile.parentFile ?: return@withContext false
            val baseName = audioFile.nameWithoutExtension
            
            val lrcFile = File(parentDir, "$baseName.lrc")
            lrcFile.exists() || File(parentDir, "$baseName.LRC").exists()
        } catch (e: Exception) {
            false
        }
    }
}
