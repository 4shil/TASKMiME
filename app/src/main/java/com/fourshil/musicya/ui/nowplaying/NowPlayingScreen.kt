package com.fourshil.musicya.ui.nowplaying

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.components.MarqueeText
import com.fourshil.musicya.ui.theme.*

@Composable
fun NowPlayingScreen(
    viewModel: NowPlayingViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onQueueClick: () -> Unit = {}
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.position.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val shuffleEnabled by viewModel.shuffleEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    // Theme-aware colors
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) DeepBlack else OffWhite
    val contentColor = if (isDark) PureWhite else PureBlack
    val surfaceColor = if (isDark) PureBlack else PureWhite

    // Progress calculation
    val progress = if (duration > 0) position.toFloat() / duration else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .drawBehind {
                // Dot grid background pattern
                val dotSpacing = 24.dp.toPx()
                val dotRadius = 1.2f
                val dotColor = contentColor.copy(alpha = 0.08f)
                for (x in 0 until (size.width / dotSpacing).toInt() + 1) {
                    for (y in 0 until (size.height / dotSpacing).toInt() + 1) {
                        drawCircle(
                            color = dotColor,
                            radius = dotRadius,
                            center = Offset(x * dotSpacing, y * dotSpacing)
                        )
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Spacer(modifier = Modifier.height(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ACTIVE CANVAS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = contentColor.copy(alpha = 0.5f)
                )
                MarqueeText(
                    text = currentSong?.title ?: "UNTITLED",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-2).sp
                    ),
                    color = contentColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(0.08f))

            // --- ALBUM ART ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .widthIn(max = 340.dp),
                contentAlignment = Alignment.Center
            ) {
                // Shadow layer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = 4.dp, y = 4.dp)
                        .background(contentColor)
                )
                // Art container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(3.dp, contentColor)
                        .background(PureBlack)
                ) {
                    AsyncImage(
                        model = currentSong?.albumArtUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix().apply { setToSaturation(0f) } // Grayscale
                        )
                    )
                    // Red overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AccentRed.copy(alpha = 0.2f))
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.06f))

            // --- ARTIST ROW ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 340.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Favorite Button
                ArtisticButton(
                    onClick = { viewModel.toggleFavorite() },
                    modifier = Modifier.size(48.dp),
                    backgroundColor = surfaceColor,
                    icon = {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            null,
                            tint = if (isFavorite) AccentRed else contentColor
                        )
                    }
                )

                // Artist Badge
                Box(modifier = Modifier.weight(1f)) {
                    // Shadow
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = 4.dp, y = 4.dp)
                            .height(48.dp)
                            .background(contentColor)
                    )
                    // Badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(3.dp, contentColor)
                            .background(MustardYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentSong?.artist?.uppercase() ?: "UNKNOWN",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = PureBlack,
                            maxLines = 1
                        )
                    }
                }

                // Add to Playlist Button
                ArtisticButton(
                    onClick = { /* TODO: Add to playlist */ },
                    modifier = Modifier.size(48.dp),
                    backgroundColor = surfaceColor,
                    icon = { Icon(Icons.Default.Add, null, tint = contentColor) }
                )
            }

            Spacer(modifier = Modifier.weight(0.08f))

            // --- PROGRESS BAR ---
            Column(modifier = Modifier.fillMaxWidth().widthIn(max = 340.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .border(3.dp, contentColor)
                        .background(surfaceColor)
                ) {
                    // Progress fill
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(contentColor)
                    ) {
                        // Red head
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(8.dp)
                                .fillMaxHeight()
                                .background(AccentRed)
                                .border(width = 3.dp, color = contentColor)
                        )
                    }
                }
                // Time labels
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(position),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = contentColor
                    )
                    Text(
                        text = formatTime(duration),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = contentColor
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // --- PLAYBACK CONTROLS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 340.dp)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                ArtisticButton(
                    onClick = { viewModel.toggleShuffle() },
                    modifier = Modifier.size(48.dp),
                    backgroundColor = surfaceColor,
                    icon = {
                        Icon(
                            Icons.Default.Shuffle, null,
                            tint = if (shuffleEnabled) AccentRed else contentColor
                        )
                    }
                )

                // Skip Previous (No border)
                Icon(
                    Icons.Default.SkipPrevious, null,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { viewModel.skipToPrevious() },
                    tint = contentColor
                )

                // Play/Pause (Largest)
                ArtisticButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier.size(80.dp),
                    backgroundColor = surfaceColor,
                    icon = {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            null,
                            modifier = Modifier.size(48.dp),
                            tint = contentColor
                        )
                    }
                )

                // Skip Next (No border)
                Icon(
                    Icons.Default.SkipNext, null,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { viewModel.skipToNext() },
                    tint = contentColor
                )

                // Repeat
                ArtisticButton(
                    onClick = { viewModel.toggleRepeat() },
                    modifier = Modifier.size(48.dp),
                    backgroundColor = surfaceColor,
                    icon = {
                        Icon(
                            if (repeatMode == 2) Icons.Default.RepeatOne else Icons.Default.Repeat,
                            null,
                            tint = if (repeatMode != 0) AccentRed else contentColor
                        )
                    }
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

