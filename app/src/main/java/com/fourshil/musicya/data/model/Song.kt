package com.fourshil.musicya.data.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val uri: Uri,
    val path: String,
    val dateAdded: Long,
    val size: Long
) {
    val durationFormatted: String
        get() {
            val minutes = (duration / 1000) / 60
            val seconds = (duration / 1000) % 60
            return "%d:%02d".format(minutes, seconds)
        }
    
    val albumArtUri: Uri = Uri.parse("content://media/external/audio/albumart/$albumId")
}

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val songCount: Int,
    val year: Int
) {
    val artUri: Uri = Uri.parse("content://media/external/audio/albumart/$id")
}

data class Artist(
    val id: Long,
    val name: String,
    val songCount: Int,
    val albumCount: Int
)

data class Folder(
    val path: String,
    val name: String,
    val songCount: Int
)
