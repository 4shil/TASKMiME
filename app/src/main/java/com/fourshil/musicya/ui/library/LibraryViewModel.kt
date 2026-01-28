package com.fourshil.musicya.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.data.db.Playlist
import com.fourshil.musicya.data.db.PlaylistSong
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.data.model.Folder
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fourshil.musicya.data.repository.SongsPagingSource
import com.fourshil.musicya.data.repository.DeleteOutcome
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import android.content.IntentSender

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playerController: PlayerController,
    private val musicDao: MusicDao
) : ViewModel() {

    // Paging for UI List (Scalability)
    val pagedSongs: Flow<PagingData<Song>> = Pager(
        config = PagingConfig(pageSize = 50, enablePlaceholders = false),
        pagingSourceFactory = { SongsPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)

    // Full list for Player Queue & Logic
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs = _songs.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists = _artists.asStateFlow()

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders = _folders.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    
    // Favorites
    val favoriteIds = musicDao.getFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Playlists
    val playlists = musicDao.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Permission Events
    private val _deletePermissionEvent = Channel<IntentSender>()
    val deletePermissionEvent = _deletePermissionEvent.receiveAsFlow()

    init {
        playerController.connect()
        // Load full library in background for player queue
        loadLibrary()
    }

    fun loadLibrary() {
        viewModelScope.launch {
            _isLoading.value = true
            // Load all songs for player queue (this might take time but Paging shows UI first)
            val allSongs = repository.getAllSongs()
            _songs.value = allSongs
            
            _albums.value = repository.getAllAlbums()
            _artists.value = repository.getAllArtists()
            _folders.value = repository.getFolders()
            _isLoading.value = false
        }
    }

    fun refresh() {
        repository.clearCache()
        loadLibrary()
    }

    fun playSong(song: Song) {
        playerController.playSong(song)
    }

    fun playSongAt(index: Int) {
        val allSongs = _songs.value
        if (index in allSongs.indices) {
            playerController.playSongs(allSongs, index)
        }
    }
    
    /**
     * Play a song while setting all provided songs as the queue.
     * This enables proper skip next/previous functionality.
     */
    /**
     * Optimized playback: Finds the song in the precached library list.
     * Starts from that index with the full queue, without UI thread overhead.
     */
    fun playSongFromLibrary(startSongId: Long) {
        viewModelScope.launch {
            // Ensure library is loaded/fresh ideally, but using _songs.value is instant
            val currentList = _songs.value
            if (currentList.isNotEmpty()) {
                val index = currentList.indexOfFirst { it.id == startSongId }
                if (index >= 0) {
                    playerController.playSongs(currentList, index)
                } else {
                    // Fallback if not found (e.g. filtered list or stale cache)
                    // Just play the individual song if we can find it, or generic error
                    // For now, try loading specific song
                    val songs = repository.getSongsByIds(listOf(startSongId))
                    if (songs.isNotEmpty()) {
                        playerController.playSong(songs.first())
                    }
                }
            } else {
                // Library empty? Try to reload and play?
                // Just fallback to playing by ID
                 val songs = repository.getSongsByIds(listOf(startSongId))
                 if (songs.isNotEmpty()) {
                     playerController.playSong(songs.first())
                 }
            }
        }
    }

    fun playAlbum(albumId: Long) {
        viewModelScope.launch {
            val albumSongs = repository.getSongsByAlbum(albumId)
            if (albumSongs.isNotEmpty()) {
                playerController.playSongs(albumSongs)
            }
        }
    }

    fun playArtist(artistName: String) {
        viewModelScope.launch {
            val artistSongs = repository.getSongsByArtist(artistName)
            if (artistSongs.isNotEmpty()) {
                playerController.playSongs(artistSongs)
            }
        }
    }

    fun playFolder(folderPath: String) {
        viewModelScope.launch {
            val folderSongs = repository.getSongsByFolder(folderPath)
            if (folderSongs.isNotEmpty()) {
                playerController.playSongs(folderSongs)
            }
        }
    }
    
    // ============ Song Actions ============
    
    fun playNext(song: Song) {
        playerController.playNext(song)
    }
    
    fun addToQueue(song: Song) {
        playerController.addToQueue(song)
    }
    
    fun addToQueue(songs: List<Song>) {
        playerController.addToQueue(songs)
    }
    
    // ============ Favorites ============
    
    fun isFavorite(songId: Long): Flow<Boolean> = musicDao.isFavorite(songId)
    
    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            musicDao.toggleFavorite(songId)
        }
    }
    
    fun addToFavorites(songIds: List<Long>) {
        viewModelScope.launch {
            songIds.forEach { songId ->
                musicDao.addFavorite(com.fourshil.musicya.data.db.FavoriteSong(songId))
            }
        }
    }
    
    // ============ Playlists ============
    
    fun createPlaylist(name: String) {
        viewModelScope.launch {
            musicDao.createPlaylist(Playlist(name = name))
        }
    }
    
    fun addToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            musicDao.addSongToPlaylist(PlaylistSong(playlistId, songId))
        }
    }
    
    fun addToPlaylist(playlistId: Long, songIds: List<Long>) {
        viewModelScope.launch {
            musicDao.addSongsToPlaylist(playlistId, songIds)
        }
    }
    
    // ============ Delete Songs ============
    
    /**
     * Delete songs from device storage.
     */
    fun deleteSongs(songIds: List<Long>, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch {
            when (val result = repository.deleteSongs(songIds)) {
                is DeleteOutcome.Success -> {
                    if (result.count > 0) {
                        loadLibrary()
                        pagedSongs // trigger refresh if possible, or invalidate paging source
                        // PagingSource invalidation is tricky without the source instance.
                        // Ideally we call 'adapter.refresh()' in UI, or expose a 'refreshSignal'
                    }
                    onComplete(result.count)
                }
                is DeleteOutcome.RequirePermission -> {
                    _deletePermissionEvent.send(result.intentSender)
                }
                is DeleteOutcome.Error -> {
                    // Handle generic error
                    onComplete(0)
                }
            }
        }
    }
    
    // ============ Select All ============
    
    /**
     * Get all song IDs for Select All functionality.
     */
    suspend fun getAllSongIds(): List<Long> {
        return repository.getAllSongIds()
    }
}

