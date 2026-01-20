package com.fourshil.musicya.data.repository

import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.data.model.Folder
import com.fourshil.musicya.data.model.Song

/**
 * Repository interface for music data operations.
 * Enables dependency injection and testing with fake implementations.
 */
interface IMusicRepository {
    
    /**
     * Get all songs from the device storage.
     */
    suspend fun getAllSongs(): List<Song>
    
    /**
     * Get all albums from the device storage.
     */
    suspend fun getAllAlbums(): List<Album>
    
    /**
     * Get all artists from the device storage.
     */
    suspend fun getAllArtists(): List<Artist>
    
    /**
     * Get all folders containing music files.
     */
    suspend fun getFolders(): List<Folder>
    
    /**
     * Get all songs belonging to a specific album.
     */
    suspend fun getSongsByAlbum(albumId: Long): List<Song>
    
    /**
     * Get all songs by a specific artist.
     */
    suspend fun getSongsByArtist(artistName: String): List<Song>
    
    /**
     * Get all songs in a specific folder.
     */
    suspend fun getSongsByFolder(folderPath: String): List<Song>
    
    /**
     * Get songs by their IDs, preserving order.
     */
    suspend fun getSongsByIds(songIds: List<Long>): List<Song>
    
    /**
     * Clear the song cache to force a refresh on next load.
     */
    fun clearCache()
    
    /**
     * Get all song IDs efficiently (for Select All functionality).
     */
    suspend fun getAllSongIds(): List<Long>
    
    /**
     * Delete songs from the device storage.
     * @return Number of songs successfully deleted
     */
    suspend fun deleteSongs(songIds: List<Long>): Int
}
