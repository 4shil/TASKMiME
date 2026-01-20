package com.fourshil.musicya.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity tracking play history for songs.
 * Stores play count and last played timestamp for "Most Played" and "Recently Played" features.
 */
@Entity(
    tableName = "song_play_history",
    indices = [
        Index(value = ["playCount"]),    // For efficient Most Played sorting
        Index(value = ["lastPlayedAt"])  // For efficient Recently Played sorting
    ]
)
data class SongPlayHistory(
    @PrimaryKey
    val songId: Long,
    val playCount: Int = 0,
    val lastPlayedAt: Long = 0L
)

