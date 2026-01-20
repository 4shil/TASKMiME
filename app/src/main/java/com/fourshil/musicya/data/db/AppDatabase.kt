package com.fourshil.musicya.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for LYRA music player.
 * Stores favorites, custom playlists, and play history.
 */
@Database(
    entities = [
        FavoriteSong::class,
        Playlist::class,
        PlaylistSong::class,
        SongPlayHistory::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao
}

