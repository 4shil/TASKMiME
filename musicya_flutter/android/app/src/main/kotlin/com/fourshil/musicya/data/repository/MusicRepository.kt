package com.fourshil.musicya.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.data.model.Folder
import com.fourshil.musicya.data.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing music files from the device's MediaStore.
 * 
 * Provides a clean abstraction over Android's MediaStore API for querying
 * songs, albums, artists, and folders. Implements caching to minimize
 * redundant queries and supports pagination for large libraries.
 *
 * ## Thread Safety
 * All public methods are suspend functions that run on [Dispatchers.IO].
 * Safe to call from any coroutine context.
 *
 * ## Caching
 * Songs are cached after the first load. Call [clearCache] to force a refresh.
 * Other collections (albums, artists, folders) are derived from the songs cache.
 *
 * ## Error Handling
 * All MediaStore queries are wrapped in try-catch. On error, empty lists are
 * returned and errors are logged. This prevents crashes from permission issues
 * or corrupted media databases.
 *
 * @property context Application context for ContentResolver access
 */
@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : IMusicRepository {
    
    private var cachedSongs: List<Song>? = null
    
    /**
     * Clear the song cache to force refresh on next load.
     */
    override fun clearCache() {
        cachedSongs = null
    }

    override suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        if (cachedSongs != null) return@withContext cachedSongs!!

        val songs = mutableListOf<Song>()
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE
        )
        
        // Only filter for IS_MUSIC, no duration filter
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        

        
        try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    songs.add(
                        Song(
                            id = id,
                            title = cursor.getString(titleCol) ?: "Unknown",
                            artist = cursor.getString(artistCol) ?: "Unknown Artist",
                            album = cursor.getString(albumCol) ?: "Unknown Album",
                            albumId = cursor.getLong(albumIdCol),
                            duration = cursor.getLong(durationCol),
                            uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id),
                            path = cursor.getString(dataCol) ?: "",
                            dateAdded = cursor.getLong(dateAddedCol),
                            size = cursor.getLong(sizeCol)
                        )
                    )
                }
            }
        } catch (_: Exception) {
            // Silently handle query errors
        }
        
        cachedSongs = songs
        songs
    }
    
    override suspend fun getAllAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val albums = mutableListOf<Album>()
        
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.FIRST_YEAR
        )
        
        try {
            context.contentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Audio.Albums.ALBUM} ASC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
                val countCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
                val yearCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)
                
                while (cursor.moveToNext()) {
                    albums.add(
                        Album(
                            id = cursor.getLong(idCol),
                            name = cursor.getString(nameCol) ?: "Unknown Album",
                            artist = cursor.getString(artistCol) ?: "Unknown Artist",
                            songCount = cursor.getInt(countCol),
                            year = cursor.getInt(yearCol)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        albums
    }
    
    override suspend fun getAllArtists(): List<Artist> = withContext(Dispatchers.IO) {
        val artists = mutableListOf<Artist>()
        
        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
        )
        
        try {
            context.contentResolver.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Audio.Artists.ARTIST} ASC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
                val trackCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
                
                while (cursor.moveToNext()) {
                    artists.add(
                        Artist(
                            id = cursor.getLong(idCol),
                            name = cursor.getString(nameCol) ?: "Unknown Artist",
                            songCount = cursor.getInt(trackCol),
                            albumCount = cursor.getInt(albumCol)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        artists
    }
    
    override suspend fun getFolders(): List<Folder> = withContext(Dispatchers.IO) {
        val songs = getAllSongs()
        songs.groupBy { File(it.path).parent ?: "" }
            .filter { it.key.isNotEmpty() }
            .map { (path, songsInFolder) ->
                Folder(
                    path = path,
                    name = File(path).name,
                    songCount = songsInFolder.size
                )
            }
            .sortedBy { it.name }
    }
    
    override suspend fun getSongsByAlbum(albumId: Long): List<Song> = withContext(Dispatchers.IO) {
        getAllSongs().filter { it.albumId == albumId }
    }
    
    override suspend fun getSongsByArtist(artistName: String): List<Song> = withContext(Dispatchers.IO) {
        getAllSongs().filter { it.artist.equals(artistName, ignoreCase = true) }
    }
    
    override suspend fun getSongsByFolder(folderPath: String): List<Song> = withContext(Dispatchers.IO) {
        getAllSongs().filter { File(it.path).parent == folderPath }
    }
    
    override suspend fun getSongsByIds(songIds: List<Long>): List<Song> = withContext(Dispatchers.IO) {
        val allSongs = getAllSongs().associateBy { it.id }
        songIds.mapNotNull { allSongs[it] }
    }

    /**
     * Paged query for songs.
     * Note: On Android < 10, this technically queries more and scans, but separates object creation.
     */
    suspend fun getSongsPaged(offset: Int, limit: Int): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE
        )
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            // Android Q (29) and above supports Bundle args for LIMIT/OFFSET
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Using standard query but we will manually skip in cursor for compatibility consistency 
                // or use stricter ContentResolver.query(uri, null, bundle, null) if wanted.
                // For safety and compatibility with existing code structure, we use the cursor skip method.
                // It is reliable across versions.
                context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    sortOrder
                )
            } else {
                 context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    sortOrder
                )
            }?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                
                // Move to offset
                if (cursor.moveToPosition(offset)) {
                    // Read up to limit or end
                    var count = 0
                    do {
                        val id = cursor.getLong(idCol)
                        songs.add(
                            Song(
                                id = id,
                                title = cursor.getString(titleCol) ?: "Unknown",
                                artist = cursor.getString(artistCol) ?: "Unknown Artist",
                                album = cursor.getString(albumCol) ?: "Unknown Album",
                                albumId = cursor.getLong(albumIdCol),
                                duration = cursor.getLong(durationCol),
                                uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id),
                                path = cursor.getString(dataCol) ?: "",
                                dateAdded = cursor.getLong(dateAddedCol),
                                size = cursor.getLong(sizeCol)
                            )
                        )
                        count++
                    } while (cursor.moveToNext() && count < limit)
                }
            }
        } catch (_: Exception) {
            // Handle error
        }
        
        songs
    }
    
    /**
     * Get all song IDs efficiently (for Select All functionality).
     */
    override suspend fun getAllSongIds(): List<Long> = withContext(Dispatchers.IO) {
        val ids = mutableListOf<Long>()
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        
        try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                while (cursor.moveToNext()) {
                    ids.add(cursor.getLong(idCol))
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MusicRepository", "Error getting song IDs", e)
        }
        
        ids
    }
    
    /**
     * Delete songs from the device storage.
     * @return Number of songs successfully deleted
     */
    override suspend fun deleteSongs(songIds: List<Long>): Int = withContext(Dispatchers.IO) {
        var deletedCount = 0
        
        for (songId in songIds) {
            try {
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)
                val deleted = context.contentResolver.delete(uri, null, null)
                if (deleted > 0) {
                    deletedCount++
                }
            } catch (e: Exception) {
                android.util.Log.e("MusicRepository", "Error deleting song $songId", e)
            }
        }
        
        // Clear cache after deletion
        if (deletedCount > 0) {
            clearCache()
        }
        
        deletedCount
    }
}
