package com.fourshil.musicya.ui.nowplaying

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
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

    // Helper to format time
    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    Box(modifier = Modifier.fillMaxSize().background(Zinc50)) {
        // Dot Grid Background
        DotGridBackground(
            modifier = Modifier.fillMaxSize().alpha(0.08f),
            dotColor = PureBlack
        )

        // Main Vertical Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ACTIVE CANVAS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 10.sp,
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = PureBlack.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                MarqueeText(
                    text = currentSong?.title ?: "UNTITLED",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Black,
                        fontSize = 56.sp, // Responsive title simulation
                        letterSpacing = (-2).sp,
                        lineHeight = 56.sp
                    ),
                    color = PureBlack,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // --- ALBUM ARTWORK ---
            // 1:1 Representation: Border 3px, Offset Shadow, No Filters
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(340.dp) // Max width from HTML was 340px
                    .padding(bottom = 32.dp)
            ) {
                // Background Shadow (translate-x-1 translate-y-1 -> approx 4dp)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 4.dp, y = 4.dp)
                        .background(PureBlack)
                )

                // Main Image Container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(3.dp, PureBlack)
                        .background(PureBlack)
                ) {
                    AsyncImage(
                        model = currentSong?.albumArtUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // --- ARTIST ROW ---
            Row(
                modifier = Modifier
                    .width(340.dp)
                    .height(48.dp)
                    .padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Heart Button
                NeoSquareButton(
                    onClick = { viewModel.toggleFavorite() },
                    icon = if (isFavorite) Icons.Default.Favorite else Icons.Filled.FavoriteBorder,
                    size = 48.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Artist Badge (Flex-1)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Shadow
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(x = 4.dp, y = 4.dp)
                            .background(PureBlack)
                    )
                    // Badge
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(3.dp, PureBlack)
                            .background(MustardYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentSong?.artist?.uppercase() ?: "UNKNOWN",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = PureBlack,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Add Button
                NeoSquareButton(
                    onClick = { /* TODO */ },
                    icon = Icons.Default.Add,
                    size = 48.dp
                )
            }

            // --- PROGRESS BAR ---
            Column(
                modifier = Modifier.width(340.dp).padding(bottom = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .border(3.dp, PureBlack)
                        .background(OffWhite)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val p = (offset.x / size.width).coerceIn(0f, 1f)
                                viewModel.seekTo((p * duration).toLong())
                            }
                        }
                ) {
                    val progressFraction = if (duration > 0) position.toFloat() / duration else 0f
                    
                    // Black Fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .fillMaxHeight()
                            .background(PureBlack)
                    )
                    
                    // Red Head
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .fillMaxHeight()
                                .background(AccentRed)
                                .border(width = 0.dp, color = Color.Transparent)
                        ) {
                             // Left border simulator
                             Box(
                                 modifier = Modifier
                                     .width(3.dp)
                                     .fillMaxHeight()
                                     .background(PureBlack)
                                     .align(Alignment.CenterStart)
                             )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Time Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val timeStyle = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontSize = 10.sp
                    )
                    Text(text = formatTime(position), style = timeStyle, color = PureBlack)
                    Text(text = formatTime(duration), style = timeStyle, color = PureBlack)
                }
            }

            // --- CONTROLS ---
            Row(
                modifier = Modifier
                    .width(340.dp)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoSquareButton(
                    onClick = { viewModel.toggleShuffle() },
                    icon = Icons.Default.Shuffle,
                    size = 48.dp,
                    tint = if(shuffleEnabled) MangaRed else PureBlack
                )
                
                Icon(
                    Icons.Default.SkipPrevious, null,
                    modifier = Modifier.size(48.dp).clickable { viewModel.skipToPrevious() },
                    tint = PureBlack
                )
                
                NeoSquareButton(
                    onClick = { viewModel.togglePlayPause() },
                    icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    size = 80.dp,
                    borderWidth = 4.dp,
                    isLarge = true,
                    iconSize = 48.dp
                )

                Icon(
                    Icons.Default.SkipNext, null,
                    modifier = Modifier.size(48.dp).clickable { viewModel.skipToNext() },
                    tint = PureBlack
                )
                
                NeoSquareButton(
                    onClick = { viewModel.toggleRepeat() },
                    icon = if(repeatMode == 1) Icons.Default.RepeatOne else Icons.Default.Repeat,
                    size = 48.dp,
                    tint = if(repeatMode!=0) MangaRed else PureBlack
                )
            }
            
            Spacer(modifier = Modifier.weight(0.1f))
        }
    }
}

@Composable
fun NeoSquareButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    size: Dp,
    borderWidth: Dp = 3.dp,
    tint: Color = PureBlack,
    isLarge: Boolean = false,
    iconSize: Dp = 24.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clickable(onClick = onClick)
    ) {
        // Shadow (4px)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(PureBlack)
        )
        // Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(borderWidth, PureBlack)
                .background(OffWhite),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun DotGridBackground(
    modifier: Modifier = Modifier,
    dotColor: Color = Color.Black,
    spacing: Dp = 20.dp,
    radius: Dp = 2.dp
) {
    Canvas(modifier = modifier) {
        val spacingPx = spacing.toPx()
        val radiusPx = radius.toPx()
        
        // Fill the rest of the canvas
        val width = size.width
        val height = size.height
        
        val cols = (width / spacingPx).toInt() + 1
        val rows = (height / spacingPx).toInt() + 1
        
        for (i in 0..cols) {
            for (j in 0..rows) {
                drawCircle(
                    color = dotColor,
                    radius = radiusPx,
                    center = Offset(i * spacingPx, j * spacingPx)
                )
            }
        }
    }
}
