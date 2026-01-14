package com.fourshil.musicya.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.fourshil.musicya.data.local.MusicDao
import com.fourshil.musicya.data.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicDao: MusicDao
) {

    suspend fun getSongs(): List<Song> = withContext(Dispatchers.IO) {
        val blacklisted = musicDao.getBlacklistedPaths().first().map { it.path }
        
        val songs = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )
        
        // Filter out short clips (user req: Short Track Filter < 45s, but let's implement basic first)
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn) ?: "Unknown"
                val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                val album = cursor.getString(albumColumn) ?: "Unknown Album"
                val duration = cursor.getLong(durationColumn)
                val path = cursor.getString(dataColumn) ?: ""
                
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                val folderPath = if (path.isNotEmpty()) File(path).parent ?: "" else ""

                // Check blacklist and duration (45s)
                val isBlacklisted = blacklisted.any { folderPath.startsWith(it) }
                val isTooShort = duration < 45000 // 45 seconds
                
                if (!isBlacklisted && !isTooShort) {
                    songs.add(
                        Song(id, title, artist, album, duration, uri, path, folderPath)
                    )
                }
            }
        }
        songs
    }
}
