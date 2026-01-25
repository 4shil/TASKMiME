package com.fourshil.musicya.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.db.FavoriteSong
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.IMusicRepository
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val repository: IMusicRepository,
    private val playerController: PlayerController
) : ViewModel() {

    // Get list of favorite song IDs
    private val favoriteIds: StateFlow<List<Long>> = musicDao.getFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Convert IDs to actual Song objects
    // Note: This is a bit inefficient if the list is huge, strictly speaking we'd want a join query,
    // but given the architecture separated Repository (MediaStore) and Dao (Room), we map manually.
    val favoriteSongs: StateFlow<List<Song>> = favoriteIds.map { ids ->
        if (ids.isEmpty()) emptyList() else repository.getSongsByIds(ids)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun playSong(song: Song) {
        val songs = favoriteSongs.value
        val index = songs.indexOfFirst { it.id == song.id }
        if (index != -1) {
            playerController.playSongs(songs, index)
        }
    }
}
