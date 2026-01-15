package com.fourshil.musicya.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for LYRA music player.
 * Stores favorites and custom playlists.
 */
@Database(
    entities = [
        FavoriteSong::class,
        Playlist::class,
        PlaylistSong::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao
}
