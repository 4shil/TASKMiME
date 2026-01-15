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

@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
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
        
        android.util.Log.d("MusicRepository", "Starting song query...")
        
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
            android.util.Log.d("MusicRepository", "Found ${songs.size} songs")
        } catch (e: Exception) {
            android.util.Log.e("MusicRepository", "Error querying songs", e)
            e.printStackTrace()
        }
        
        songs
    }
    
    suspend fun getAllAlbums(): List<Album> = withContext(Dispatchers.IO) {
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
    
    suspend fun getAllArtists(): List<Artist> = withContext(Dispatchers.IO) {
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
    
    suspend fun getFolders(): List<Folder> = withContext(Dispatchers.IO) {
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
    
    suspend fun getSongsByAlbum(albumId: Long): List<Song> = withContext(Dispatchers.IO) {
        getAllSongs().filter { it.albumId == albumId }
    }
    
    suspend fun getSongsByArtist(artistName: String): List<Song> = withContext(Dispatchers.IO) {
        getAllSongs().filter { it.artist.equals(artistName, ignoreCase = true) }
    }
    
    suspend fun getSongsByFolder(folderPath: String): List<Song> = withContext(Dispatchers.IO) {
        getAllSongs().filter { File(it.path).parent == folderPath }
    }
    
    suspend fun getSongsByIds(songIds: List<Long>): List<Song> = withContext(Dispatchers.IO) {
        val allSongs = getAllSongs().associateBy { it.id }
        songIds.mapNotNull { allSongs[it] }
    }
}
