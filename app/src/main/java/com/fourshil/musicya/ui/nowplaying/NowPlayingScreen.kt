package com.fourshil.musicya.ui.nowplaying

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fourshil.musicya.ui.components.HalftoneBackground
import com.fourshil.musicya.ui.components.LyricsBottomSheet
import com.fourshil.musicya.ui.components.MarqueeText
import com.fourshil.musicya.ui.theme.*

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
 * Neo-Brutalism Now Playing Screen
 * Clean, professional design with soft colors and smooth 60fps animations
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


    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground
    val surfaceColor = MaterialTheme.colorScheme.surface
    val accentColor = NeoCoral

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Subtle halftone background
        HalftoneBackground(
            modifier = Modifier.fillMaxSize(),
            color = contentColor,
            alpha = 0.03f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = NeoDimens.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoIconButton(
                    onClick = onBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    backgroundColor = surfaceColor
                )

                Text(
                    "Now Playing",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = contentColor
                )

                NeoIconButton(
                    onClick = onQueueClick,
                    icon = Icons.AutoMirrored.Filled.QueueMusic,
                    contentDescription = "View queue",
                    backgroundColor = surfaceColor
                )
            }
            
            // Lyrics button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                NeoIconButton(
                    onClick = { showLyrics = true },
                    icon = Icons.Default.Lyrics,
                    contentDescription = "Show lyrics",
                    backgroundColor = surfaceColor,
                    size = 40.dp,
                    iconSize = 20.dp
                )
            }

            Spacer(modifier = Modifier.weight(0.3f))

            // Album Artwork
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(NeoDimens.AlbumArtLarge + NeoDimens.ShadowMedium)
                    .padding(bottom = 24.dp)
            ) {
                // Shadow
                Box(
                    modifier = Modifier
                        .size(NeoDimens.AlbumArtLarge)
                        .offset(x = NeoDimens.ShadowMedium, y = NeoDimens.ShadowMedium)
                        .background(NeoShadowLight)
                )
                // Main container
                Box(
                    modifier = Modifier
                        .size(NeoDimens.AlbumArtLarge)
                        .border(NeoDimens.BorderMedium, MaterialTheme.colorScheme.outline)
                        .background(surfaceColor)
                ) {
                    AsyncImage(
                        model = currentSong?.albumArtUri,
                        contentDescription = "Album artwork",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Song Title
            MarqueeText(
                isActive = true,
                text = currentSong?.title ?: "No song playing",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center
                ),
                color = contentColor,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Artist
            Text(
                text = currentSong?.artist ?: "Unknown artist",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = contentColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(0.3f))

            // Progress Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .border(NeoDimens.BorderThin, MaterialTheme.colorScheme.outline)
                        .background(surfaceColor)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val p = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                                viewModel.seekTo((p * duration).toLong())
                            }
                        }
                ) {
                    val progressFraction = if (duration > 0) position.toFloat() / duration else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progressFraction)
                            .background(accentColor)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatTime(position),
                        style = MaterialTheme.typography.labelMedium,
                        color = contentColor.copy(alpha = 0.6f)
                    )
                    Text(
                        formatTime(duration),
                        style = MaterialTheme.typography.labelMedium,
                        color = contentColor.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.2f))

            // Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(
                    onClick = { viewModel.toggleShuffle() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (shuffleEnabled) accentColor else contentColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous
                IconButton(
                    onClick = { viewModel.skipToPrevious() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = contentColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Play/Pause - Main button with Neo-Brutalism styling
                Box(
                    modifier = Modifier.size(80.dp + NeoDimens.ShadowMedium)
                ) {
                    // Shadow
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .offset(x = NeoDimens.ShadowMedium, y = NeoDimens.ShadowMedium)
                            .background(NeoShadowLight, RoundedCornerShape(NeoDimens.CornerSmall))
                    )
                    // Button
                    Surface(
                        onClick = { viewModel.togglePlayPause() },
                        shape = RoundedCornerShape(NeoDimens.CornerSmall),
                        color = Slate900,
                        border = androidx.compose.foundation.BorderStroke(NeoDimens.BorderMedium, Slate700),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Crossfade(targetState = isPlaying, label = "PlayPause") { playing ->
                                Icon(
                                    if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (playing) "Pause" else "Play",
                                    modifier = Modifier.size(48.dp),
                                    tint = Slate50
                                )
                            }
                        }
                    }
                }

                // Next
                IconButton(
                    onClick = { viewModel.skipToNext() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = contentColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat
                IconButton(
                    onClick = { viewModel.toggleRepeat() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        if (repeatMode == 1) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (repeatMode != 0) accentColor else contentColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
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

/**
 * Neo-Brutalism Icon Button
 * Clean button with small shadow
 */
@Composable
private fun NeoIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp
) {
    Box(
        modifier = Modifier
            .size(size + NeoDimens.ShadowSmall)
            .clickable(onClick = onClick)
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = NeoDimens.ShadowSmall, y = NeoDimens.ShadowSmall)
                .background(NeoShadowLight, RoundedCornerShape(NeoDimens.CornerSmall))
        )
        // Button
        Box(
            modifier = Modifier
                .size(size)
                .background(backgroundColor, RoundedCornerShape(NeoDimens.CornerSmall))
                .border(NeoDimens.BorderThin, MaterialTheme.colorScheme.outline, RoundedCornerShape(NeoDimens.CornerSmall)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
