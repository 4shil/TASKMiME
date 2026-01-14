package com.fourshil.musicya.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.player.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LyricsViewModel @Inject constructor(
    private val lyricsRepository: LyricsRepository,
    private val serviceConnection: MusicServiceConnection
) : ViewModel() {

    private val _lyrics = MutableStateFlow<List<LyricsLine>>(emptyList())
    val lyrics = _lyrics.asStateFlow()

    private val _activeLineIndex = MutableStateFlow(-1)
    val activeLineIndex = _activeLineIndex.asStateFlow()

    private var currentSongPath: String? = null
    private var syncJob: Job? = null

    init {
        // Observe player changes (hacky polling for now or better listener?)
        // Ideally MusicServiceConnection exposes mediaItem changes.
        // For MVP, we will poll or rely on UI to trigger 'loadLyrics'
        
        viewModelScope.launch {
            while (true) {
                checkCurrentSong()
                updateActiveLine()
                delay(200) // 5Hz update for lyrics sync
            }
        }
    }

    private suspend fun checkCurrentSong() {
        val player = serviceConnection.player ?: return
        val mediaItem = player.currentMediaItem ?: return
        
        // Assuming we put the path in MediaMetadata or Tag. 
        // Or if we passed Uri string.
        // In MusicRepository, we used MediaItem.fromUri(song.uri).
        // If URI is file:// scheme, we can get path. 
        // If content://, we might need the original path. 
        // Let's assume for now the path was stored in MediaItem.localConfiguration?.uri?
        // Or we just try to get it.
        
        // Workaround: We will use a shared "Current Song" state in future, 
        // but for now let's hope the URI.path gives us a hint or we rely on UI passing it.
        // Actually, let's make a method `loadLyrics(path)` called by UI when song changes.
    }
    
    // UI calls this when it detects song change
    fun loadLyrics(path: String) {
        if (currentSongPath == path) return
        currentSongPath = path
        
        viewModelScope.launch {
            _lyrics.value = lyricsRepository.getLyricsForFile(path)
            _activeLineIndex.value = -1
        }
    }

    private fun updateActiveLine() {
        val player = serviceConnection.player ?: return
        if (!player.isPlaying) return

        val pos = player.currentPosition
        val currentLyrics = _lyrics.value
        if (currentLyrics.isEmpty()) return

        // Find the last line whose timestamp is <= current position
        // This is the active line.
        // Binary search or linear scan (linear is fine for ~50 lines)
        
        var index = -1
        for (i in currentLyrics.indices) {
            if (currentLyrics[i].timestamp <= pos) {
                index = i
            } else {
                break
            }
        }
        
        if (_activeLineIndex.value != index) {
            _activeLineIndex.value = index
        }
    }
}
