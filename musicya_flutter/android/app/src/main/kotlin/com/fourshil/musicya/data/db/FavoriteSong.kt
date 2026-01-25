package com.fourshil.musicya.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a favorite song.
 * Only stores the song ID - actual song data comes from MediaStore.
 */
@Entity(tableName = "favorite_songs")
data class FavoriteSong(
    @PrimaryKey
    val songId: Long,
    val addedAt: Long = System.currentTimeMillis()
)
