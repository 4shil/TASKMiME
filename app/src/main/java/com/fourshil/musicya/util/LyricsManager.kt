package com.fourshil.musicya.util

import android.content.Context
import android.util.LruCache
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
 * Uses caching to avoid repeated file system lookups.
 */
@Singleton
class LyricsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private sealed class LyricsCacheEntry {
        data class Found(val lyrics: Lyrics) : LyricsCacheEntry()
        data object Missing : LyricsCacheEntry()
    }

    // Cache for lyrics (songPath -> found/missing)
    private val lyricsCache = LruCache<String, LyricsCacheEntry>(50)
    
    // Cache for lyrics file paths (folder path -> set of lrc file names)
    private val folderLrcCache = LruCache<String, Set<String>>(100)
    
    /**
     * Find and load lyrics for a song.
     * Looks for .lrc file with same name as audio file.
     */
    suspend fun getLyricsForSong(song: Song): Lyrics? = withContext(Dispatchers.IO) {
        // Check cache first
        val cachedResult = lyricsCache.get(song.path)
        if (cachedResult != null || lyricsCache.snapshot().containsKey(song.path)) {
            return@withContext when (cachedResult) {
                is LyricsCacheEntry.Found -> cachedResult.lyrics
                LyricsCacheEntry.Missing, null -> null
            }
        }
        
        try {
            val audioFile = File(song.path)
            if (!audioFile.exists()) {
                lyricsCache.put(song.path, LyricsCacheEntry.Missing)
                return@withContext null
            }
            
            val parentDir = audioFile.parentFile ?: run {
                lyricsCache.put(song.path, LyricsCacheEntry.Missing)
                return@withContext null
            }
            val baseName = audioFile.nameWithoutExtension
            val folderPath = parentDir.absolutePath
            
            // Get or build cached list of lrc files in this folder
            val lrcFilesInFolder = folderLrcCache.get(folderPath) ?: run {
                val files = parentDir.listFiles { file -> 
                    file.extension.lowercase() == "lrc" 
                }?.map { it.name.lowercase() }?.toSet() ?: emptySet()
                folderLrcCache.put(folderPath, files)
                files
            }
            
            // Quick check if any matching lrc file exists
            val baseNameLower = baseName.lowercase()
            val possibleNames = listOf(
                "$baseNameLower.lrc",
                "${song.title.lowercase()}.lrc",
                "${song.artist.lowercase()} - ${song.title.lowercase()}.lrc"
            )
            
            val matchingLrcName = possibleNames.firstOrNull { it in lrcFilesInFolder }
            if (matchingLrcName != null) {
                // Find actual file (case-insensitive match)
                val lrcFiles = parentDir.listFiles { file -> 
                    file.name.lowercase() == matchingLrcName
                }
                val lrcFile = lrcFiles?.firstOrNull()
                if (lrcFile != null && lrcFile.canRead()) {
                    val lyrics = LyricsParser.parseFile(lrcFile)
                    if (lyrics != null) {
                        lyricsCache.put(song.path, LyricsCacheEntry.Found(lyrics))
                        return@withContext lyrics
                    }
                }
            }
            
            // Fallback: Check for case-insensitive base name match
            lrcFilesInFolder.find { 
                it.substringBeforeLast('.').equals(baseNameLower, ignoreCase = true) 
            }?.let { matchName ->
                val lrcFile = File(parentDir, matchName)
                if (lrcFile.exists() && lrcFile.canRead()) {
                    val lyrics = LyricsParser.parseFile(lrcFile)
                    if (lyrics != null) {
                        lyricsCache.put(song.path, LyricsCacheEntry.Found(lyrics))
                        return@withContext lyrics
                    }
                }
            }
            
            lyricsCache.put(song.path, LyricsCacheEntry.Missing)
            null
        } catch (e: Exception) {
            lyricsCache.put(song.path, LyricsCacheEntry.Missing)
            null
        }
    }
    
    /**
     * Check if lyrics exist for a song without loading them.
     */
    suspend fun hasLyrics(song: Song): Boolean = withContext(Dispatchers.IO) {
        // Check cache first
        if (lyricsCache.snapshot().containsKey(song.path)) {
            return@withContext when (val cached = lyricsCache.get(song.path)) {
                is LyricsCacheEntry.Found -> true
                LyricsCacheEntry.Missing, null -> false
            }
        }
        
        try {
            val audioFile = File(song.path)
            if (!audioFile.exists()) return@withContext false
            
            val parentDir = audioFile.parentFile ?: return@withContext false
            val baseName = audioFile.nameWithoutExtension.lowercase()
            
            // Use folder cache
            val folderPath = parentDir.absolutePath
            val lrcFilesInFolder = folderLrcCache.get(folderPath) ?: run {
                val files = parentDir.listFiles { file -> 
                    file.extension.lowercase() == "lrc" 
                }?.map { it.name.lowercase() }?.toSet() ?: emptySet()
                folderLrcCache.put(folderPath, files)
                files
            }
            
            "$baseName.lrc" in lrcFilesInFolder
        } catch (e: Exception) {
            false
        }
    }
}
