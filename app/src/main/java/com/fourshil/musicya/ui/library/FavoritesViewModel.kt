package com.fourshil.musicya.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.data.db.Playlist
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val musicRepository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    
    private val _favoriteSongs = MutableStateFlow<List<Song>>(emptyList())
    val favoriteSongs = _favoriteSongs.asStateFlow()
    
    val favoriteIds = musicDao.getFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Playlists  
    val playlists = musicDao.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    init {
        loadFavorites()
    }
    
    private fun loadFavorites() {
        viewModelScope.launch {
            musicDao.getFavoriteIds().collect { favoriteIds ->
                _isLoading.value = true
                val allSongs = musicRepository.getAllSongs()
                val favorites = allSongs.filter { it.id in favoriteIds }
                _favoriteSongs.value = favorites
                _isLoading.value = false
            }
        }
    }
    
    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            musicDao.toggleFavorite(songId)
        }
    }
    
    fun isFavorite(songId: Long): Flow<Boolean> = musicDao.isFavorite(songId)
    
    fun playSongAt(index: Int) {
        val songs = _favoriteSongs.value
        if (songs.isNotEmpty() && index in songs.indices) {
            playerController.playSongs(songs, index)
        }
    }
    
    fun playNext(song: Song) {
        playerController.playNext(song)
    }
    
    fun addToQueue(song: Song) {
        playerController.addToQueue(song)
    }
    
    // ============ Playlists ============
    
    fun createPlaylist(name: String) {
        viewModelScope.launch {
            musicDao.createPlaylist(com.fourshil.musicya.data.db.Playlist(name = name))
        }
    }
    
    fun addToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            musicDao.addSongToPlaylist(com.fourshil.musicya.data.db.PlaylistSong(playlistId, songId))
        }
    }
}
