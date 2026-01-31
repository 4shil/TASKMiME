package com.fourshil.musicya.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.fourshil.musicya.ui.nowplaying.NowPlayingViewModel

@Composable
fun ConnectedMiniPlayer(
    viewModel: NowPlayingViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.position.collectAsState()
    val duration by viewModel.duration.collectAsState()

    if (currentSong != null) {
        MiniPlayer(
            song = currentSong,
            isPlaying = isPlaying,
            progress = if (duration > 0) position.toFloat() / duration else 0f,
            onPlayPauseClick = { viewModel.togglePlayPause() },
            onNextClick = { viewModel.skipToNext() },
            onPreviousClick = { viewModel.skipToPrevious() },
            onClick = onClick,
            modifier = modifier
        )
    }
}
