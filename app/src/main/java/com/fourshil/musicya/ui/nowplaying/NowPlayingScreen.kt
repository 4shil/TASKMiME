package com.fourshil.musicya.ui.nowplaying

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fourshil.musicya.ui.components.LyricsBottomSheet
import com.fourshil.musicya.ui.components.MarqueeText
import com.fourshil.musicya.ui.components.MinimalControlButton
import com.fourshil.musicya.ui.components.MinimalIconButton
import com.fourshil.musicya.ui.theme.*
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Helper to format time - moved outside composable for performance
 */
private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

/**
 * Clean Minimalistic Now Playing Screen
 * Features swipe gestures on album art for skip next/previous
 */
@Composable
fun NowPlayingScreen(
    viewModel: NowPlayingViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onQueueClick: () -> Unit = {}
) {
    androidx.activity.compose.BackHandler(onBack = onBack)

    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.position.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val shuffleEnabled by viewModel.shuffleEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    
    // Lyrics state
    var showLyrics by remember { mutableStateOf(false) }

    // Swipe gesture state for album art
    val density = LocalDensity.current
    val swipeThreshold = with(density) { 100.dp.toPx() }
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "albumSwipe"
    )

    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground
    val accentColor = MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = backgroundColor,
        contentColor = contentColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = NeoDimens.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = NeoDimens.SpacingL),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MinimalIconButton(
                    onClick = onBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )

                Text(
                    "Now Playing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )

                MinimalIconButton(
                    onClick = onQueueClick,
                    icon = Icons.AutoMirrored.Filled.QueueMusic,
                    contentDescription = "View queue"
                )
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // Album Artwork with swipe gestures
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(NeoDimens.AlbumArtXL)
                    .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                    .graphicsLayer {
                        rotationZ = animatedOffsetX / 50f
                        alpha = 1f - (abs(animatedOffsetX) / (swipeThreshold * 2))
                    }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                when {
                                    offsetX < -swipeThreshold -> viewModel.skipToNext()
                                    offsetX > swipeThreshold -> viewModel.skipToPrevious()
                                }
                                offsetX = 0f
                            },
                            onDragCancel = { offsetX = 0f },
                            onHorizontalDrag = { _, dragAmount ->
                                offsetX = (offsetX + dragAmount).coerceIn(
                                    -swipeThreshold * 1.5f,
                                    swipeThreshold * 1.5f
                                )
                            }
                        )
                    }
            ) {
                // Album art with rounded corners
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(NeoDimens.CornerLarge),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = NeoDimens.ElevationHigh,
                    shadowElevation = NeoDimens.ElevationHigh
                ) {
                    AsyncImage(
                        model = currentSong?.albumArtUri,
                        contentDescription = "Album artwork",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(NeoDimens.CornerLarge))
                    )
                }
            }

            Spacer(modifier = Modifier.height(NeoDimens.SpacingXXL))

            // Song Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MarqueeText(
                    isActive = true,
                    text = currentSong?.title ?: "No song playing",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    ),
                    color = contentColor,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(NeoDimens.SpacingS))

                Text(
                    text = currentSong?.artist ?: "Unknown artist",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(NeoDimens.SpacingXXL))

            // Progress Section
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Slim progress bar
                Slider(
                    value = if (duration > 0) position.toFloat() / duration else 0f,
                    onValueChange = { fraction ->
                        viewModel.seekTo((fraction * duration).toLong())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatTime(position),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        formatTime(duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // Control buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = NeoDimens.SpacingXXL),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                MinimalIconButton(
                    onClick = { viewModel.toggleShuffle() },
                    icon = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    contentColor = if (shuffleEnabled) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    size = 48.dp,
                    iconSize = 24.dp
                )

                // Previous
                MinimalIconButton(
                    onClick = { viewModel.skipToPrevious() },
                    icon = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    size = 56.dp,
                    iconSize = 32.dp
                )

                // Play/Pause - Primary action
                MinimalControlButton(
                    onClick = { viewModel.togglePlayPause() },
                    icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    size = 72.dp,
                    iconSize = 36.dp
                )

                // Next
                MinimalIconButton(
                    onClick = { viewModel.skipToNext() },
                    icon = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    size = 56.dp,
                    iconSize = 32.dp
                )

                // Repeat
                MinimalIconButton(
                    onClick = { viewModel.toggleRepeat() },
                    icon = if (repeatMode == 1) Icons.Default.RepeatOne else Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    contentColor = if (repeatMode != 0) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    size = 48.dp,
                    iconSize = 24.dp
                )
            }

            // Secondary actions row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = NeoDimens.SpacingXL),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorite button
                IconButton(onClick = { 
                    currentSong?.let { viewModel.toggleFavorite(it.id) }
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) AccentError else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(NeoDimens.SpacingL))

                // Lyrics button
                IconButton(onClick = { showLyrics = true }) {
                    Icon(
                        imageVector = Icons.Default.Lyrics,
                        contentDescription = "Lyrics",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    
    // Lyrics Bottom Sheet
    if (showLyrics && currentSong != null) {
        LyricsBottomSheet(
            songPath = currentSong?.path ?: "",
            currentPositionMs = position,
            onDismiss = { showLyrics = false }
        )
    }
}
