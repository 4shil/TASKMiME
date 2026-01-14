package com.fourshil.musicya.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import com.fourshil.musicya.player.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val serviceConnection: MusicServiceConnection
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs = _songs.asStateFlow()

    // Map of Folder Name -> List of Songs
    val folders = _songs.combine(MutableStateFlow(Unit)) { songs, _ ->
        songs.groupBy { song ->
            val file = File(song.folderPath)
            file.name // Group by folder name (e.g., "Downloads", "Music")
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    fun loadLibrary() {
        viewModelScope.launch {
            _songs.value = repository.getSongs()
        }
    }

    fun playSong(song: Song) {
        serviceConnection.player?.let { player ->
            val mediaItem = MediaItem.Builder()
                .setUri(song.uri)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .setExtras(android.os.Bundle().apply { putString("path", song.path) })
                        .build()
                )
                .build()
            
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }
}
