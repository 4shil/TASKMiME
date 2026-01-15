package com.fourshil.musicya.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs = _songs.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists = _artists.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private var allSongs: List<Song> = emptyList()
    private var allAlbums: List<Album> = emptyList()
    private var allArtists: List<Artist> = emptyList()

    init {
        playerController.connect()
        loadData()
        observeQuery()
    }

    private fun loadData() {
        viewModelScope.launch {
            allSongs = repository.getAllSongs()
            allAlbums = repository.getAllAlbums()
            allArtists = repository.getAllArtists()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            _query
                .debounce(300)
                .collect { q ->
                    if (q.isBlank()) {
                        _songs.value = emptyList()
                        _albums.value = emptyList()
                        _artists.value = emptyList()
                    } else {
                        _isSearching.value = true
                        val queryLower = q.lowercase()
                        _songs.value = allSongs.filter {
                            it.title.lowercase().contains(queryLower) ||
                            it.artist.lowercase().contains(queryLower)
                        }.take(20)
                        _albums.value = allAlbums.filter {
                            it.name.lowercase().contains(queryLower) ||
                            it.artist.lowercase().contains(queryLower)
                        }.take(10)
                        _artists.value = allArtists.filter {
                            it.name.lowercase().contains(queryLower)
                        }.take(10)
                        _isSearching.value = false
                    }
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun playSong(song: Song) {
        playerController.playSong(song)
    }

    fun playAlbum(albumId: Long) {
        viewModelScope.launch {
            val albumSongs = repository.getSongsByAlbum(albumId)
            if (albumSongs.isNotEmpty()) {
                playerController.playSongs(albumSongs)
            }
        }
    }
}
