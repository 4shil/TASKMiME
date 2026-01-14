package com.fourshil.musicya.data.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: Uri,
    val path: String, // Absolute filesystem path
    val folderPath: String // Parent directory
)
