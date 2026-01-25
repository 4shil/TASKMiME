package com.fourshil.musicya.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a user-created playlist.
 */
@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
