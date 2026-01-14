package com.fourshil.musicya.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blacklisted_paths")
data class BlacklistedPath(
    @PrimaryKey val path: String
)
