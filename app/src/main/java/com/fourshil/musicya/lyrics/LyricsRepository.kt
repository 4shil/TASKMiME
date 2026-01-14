package com.fourshil.musicya.lyrics

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsRepository @Inject constructor() {

    suspend fun getLyricsForFile(audioFilePath: String): List<LyricsLine> = withContext(Dispatchers.IO) {
        if (audioFilePath.isBlank()) return@withContext emptyList()

        val audioFile = File(audioFilePath)
        val parent = audioFile.parentFile ?: return@withContext emptyList()
        val nameWithoutExtension = audioFile.nameWithoutExtension
        
        // Check for .lrc file
        val lrcFile = File(parent, "$nameWithoutExtension.lrc")
        
        if (lrcFile.exists()) {
            return@withContext LrcParser.parse(lrcFile)
        }
        
        // Future: Check embedded tags or online search
        emptyList()
    }
}
