package com.fourshil.musicya.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.images.Artwork
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class to extract and cache album art from audio files.
 * Uses JAudioTagger to read embedded artwork, with fallback to MediaStore albumart.
 */
@Singleton
class AlbumArtHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Memory cache for album art URIs (songPath -> cachedArtUri)
    private val artCache = LruCache<String, Uri>(100)
    
    // Cache directory for extracted artwork
    private val cacheDir: File by lazy {
        File(context.cacheDir, "album_art").apply { mkdirs() }
    }
    
    /**
     * Get album art URI for a song. Tries to extract embedded art first,
     * then falls back to MediaStore albumart.
     */
    suspend fun getAlbumArtUri(songPath: String, albumId: Long): Uri = withContext(Dispatchers.IO) {
        // Check memory cache first
        artCache.get(songPath)?.let { return@withContext it }
        
        // Try to extract embedded artwork
        val extractedUri = extractEmbeddedArt(songPath)
        if (extractedUri != null) {
            artCache.put(songPath, extractedUri)
            return@withContext extractedUri
        }
        
        // Fallback to MediaStore albumart
        val mediaStoreUri = Uri.parse("content://media/external/audio/albumart/$albumId")
        artCache.put(songPath, mediaStoreUri)
        mediaStoreUri
    }
    
    /**
     * Extract embedded artwork from audio file using JAudioTagger.
     * Returns null if no embedded art found.
     */
    private fun extractEmbeddedArt(songPath: String): Uri? {
        return try {
            val file = File(songPath)
            if (!file.exists()) return null
            
            // Check if we already have cached art for this file
            val cacheFile = File(cacheDir, "${file.name.hashCode()}.jpg")
            if (cacheFile.exists()) {
                return Uri.fromFile(cacheFile)
            }
            
            // Read audio file tags
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tag ?: return null
            val artwork: Artwork = tag.firstArtwork ?: return null
            
            // Get artwork bytes
            val artBytes = artwork.binaryData ?: return null
            if (artBytes.isEmpty()) return null
            
            // Decode and compress bitmap
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size, options)
            
            // Calculate sample size for max 500px
            val maxSize = 500
            options.inSampleSize = calculateInSampleSize(options, maxSize, maxSize)
            options.inJustDecodeBounds = false
            
            val bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size, options)
                ?: return null
            
            // Save to cache
            FileOutputStream(cacheFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            bitmap.recycle()
            
            Uri.fromFile(cacheFile)
        } catch (e: Exception) {
            // JAudioTagger may throw various exceptions for unsupported formats
            null
        }
    }
    
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height, width) = options.outHeight to options.outWidth
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    /**
     * Clear the album art cache
     */
    fun clearCache() {
        artCache.evictAll()
        cacheDir.listFiles()?.forEach { it.delete() }
    }
}
