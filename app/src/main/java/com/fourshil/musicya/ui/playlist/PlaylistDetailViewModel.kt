package com.fourshil.musicya.ui.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

enum class PlaylistType {
    ALBUM, ARTIST, FOLDER
}

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playerController: PlayerController,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs = _songs.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _subtitle = MutableStateFlow("")
    val subtitle = _subtitle.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _artUri = MutableStateFlow<String?>(null)
    val artUri = _artUri.asStateFlow()

    init {
        playerController.connect()
        
        // Get parameters from navigation
        val type = savedStateHandle.get<String>("type") ?: ""
        val id = savedStateHandle.get<String>("id") ?: ""
        
        loadPlaylist(type, id)
    }

    private fun loadPlaylist(type: String, id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            when (type) {
                "album" -> {
                    val albumId = id.toLongOrNull() ?: 0L
                    val albumSongs = repository.getSongsByAlbum(albumId)
                    _songs.value = albumSongs
                    if (albumSongs.isNotEmpty()) {
                        _title.value = albumSongs.first().album
                        _subtitle.value = "${albumSongs.first().artist} • ${albumSongs.size} songs"
                        _artUri.value = albumSongs.first().albumArtUri.toString()
                    }
                }
                "artist" -> {
                    val artistName = URLDecoder.decode(id, "UTF-8")
                    val artistSongs = repository.getSongsByArtist(artistName)
                    _songs.value = artistSongs
                    _title.value = artistName
                    _subtitle.value = "${artistSongs.size} songs"
                }
                "folder" -> {
                    val folderPath = URLDecoder.decode(id, "UTF-8")
                    val folderSongs = repository.getSongsByFolder(folderPath)
                    _songs.value = folderSongs
                    _title.value = folderPath.substringAfterLast("/")
                    _subtitle.value = "${folderSongs.size} songs"
                }
            }
            
            _isLoading.value = false
        }
    }

    fun playAll() {
        val allSongs = _songs.value
        if (allSongs.isNotEmpty()) {
            playerController.playSongs(allSongs, 0)
        }
    }

    fun shufflePlay() {
        val allSongs = _songs.value.shuffled()
        if (allSongs.isNotEmpty()) {
            playerController.playSongs(allSongs, 0)
        }
    }

    fun playSongAt(index: Int) {
        val allSongs = _songs.value
        if (index in allSongs.indices) {
            playerController.playSongs(allSongs, index)
        }
    }
}
