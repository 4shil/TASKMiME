package com.fourshil.musicya.ui.nowplaying

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fourshil.musicya.ui.components.HalftoneBackground
import com.fourshil.musicya.ui.components.MarqueeText
import com.fourshil.musicya.ui.theme.*
import kotlin.math.coerceIn

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

    val isDark = isSystemInDarkTheme()
    val bg = if (isDark) Color(0xFF100D21) else Color(0xFFfaf9fb)
    val text = if (isDark) Color.White else Color.Black

    // Helper to format time
    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        // Halftone Background Effect
        HalftoneBackground(
            modifier = Modifier.fillMaxSize().alpha(0.1f),
            color = text,
            dotSize = 2f,
            spacing = 20f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoButton(
                    onClick = onBack,
                    icon = Icons.Default.ArrowBack,
                    size = 56.dp,
                    backgroundColor = if(isDark) Color(0xFF2E2E40) else Color.White
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "SKETCHBOOK // 004",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 4.sp
                        ),
                        color = text.copy(alpha = 0.4f)
                    )
                    Text(
                        "ACTIVE CANVAS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        ),
                        color = text
                    )
                }

                NeoButton(
                    onClick = onQueueClick,
                    icon = Icons.AutoMirrored.Filled.QueueMusic,
                    size = 56.dp,
                    backgroundColor = if(isDark) Color(0xFF2E2E40) else Color.White
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // --- ALBUM ARTWORK ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(340.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Large Background Disc Decoration (Rotates if playing)
                Icon(
                    Icons.Default.Album, null,
                    modifier = Modifier
                        .size(300.dp)
                        .alpha(0.05f)
                        .graphicsLayer { rotationZ = if(isPlaying) (position.toFloat() / 100) % 360f else 0f },
                    tint = text
                )

                // RAW TAG (Manga style)
                Box(
                    modifier = Modifier
                        .offset(x = 130.dp, y = (-160).dp)
                        .rotate(12f)
                        .zIndex(2f)
                        .background(MangaRed, RoundedCornerShape(4.dp))
                        .border(4.dp, Color.Black, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        "RAW", 
                        color = Color.White, 
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }

                // Brutalist Shadow
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 10.dp, y = 10.dp)
                        .background(Color.Black)
                )

                // Main Image Container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(6.dp, Color.Black)
                        .background(Color.White)
                ) {
                    AsyncImage(
                        model = currentSong?.albumArtUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().alpha(0.95f)
                    )
                    
                    // SFX Label
                    Text(
                        "VIBE!", 
                        color = MangaRed, 
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        ), 
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp).rotate(-12f)
                    )
                }
            }

            // --- SONG TITLE (CENTERED & MARQUEE) ---
            MarqueeText(
                isActive = true,
                text = currentSong?.title?.uppercase() ?: "UNTITLED",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    fontSize = 48.sp, // Slightly reduced for responsiveness
                    letterSpacing = (-2).sp,
                    lineHeight = 52.sp,
                    textAlign = TextAlign.Center
                ),
                color = text,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- ARTIST BADGE (RESPONSIVE & MARQUEE) ---
            Box(
                modifier = Modifier
                    .width(340.dp)
                    .height(56.dp)
            ) {
                // Brutalist Shadow
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = 6.dp, y = 6.dp)
                        .background(Color.Black)
                )
                // Badge
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(4.dp, Color.Black)
                        .background(MangaYellow),
                    contentAlignment = Alignment.Center
                ) {
                    MarqueeText(
                        isActive = true,
                        text = currentSong?.artist?.uppercase() ?: "UNKNOWN ARTIST",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // --- PROGRESS SECTION ---
            Column(modifier = Modifier.width(340.dp).padding(bottom = 24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("RENDERING...", style = MaterialTheme.typography.labelSmall, color = text.copy(0.6f))
                    Text(
                        "MASTER-EDIT", 
                        style = MaterialTheme.typography.labelSmall, 
                        color = MangaRed,
                        modifier = Modifier.drawBehind { 
                            val stroke = 2.dp.toPx()
                            drawLine(MangaRed, Offset(0f, size.height + 4.dp.toPx()), Offset(size.width, size.height + 4.dp.toPx()), stroke)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                // Blocky Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(6.dp, Color.Black)
                        .background(Color.White)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val p = (offset.x / size.width).coerceIn(0f, 1f)
                                viewModel.seekTo((p * duration).toLong())
                            }
                        }
                ) {
                    val progressFraction = if (duration > 0) position.toFloat() / duration else 0f
                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progressFraction).background(Color.Black)) {
                        // Progress Block Head
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(12.dp)
                                .fillMaxHeight()
                                .background(MangaRed)
                                .border(start = 4.dp, color = Color.Black)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val timeStyle = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Text(formatTime(position), style = timeStyle, color = text)
                    Text(formatTime(duration), style = timeStyle, color = text)
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // --- CONTROLS ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.toggleShuffle() }) {
                    Icon(Icons.Default.Shuffle, null, tint = if(shuffleEnabled) MangaRed else text, modifier = Modifier.rotate(-12f))
                }
                
                Icon(
                    Icons.Default.SkipPrevious, null, 
                    modifier = Modifier.size(48.dp).clickable { viewModel.skipToPrevious() }, 
                    tint = text
                )
                
                // Play/Pause (Neo-Brutalist Block)
                Surface(
                    onClick = { viewModel.togglePlayPause() },
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(6.dp, Color.Black),
                    shadowElevation = 12.dp,
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Crossfade(targetState = isPlaying, label = "PlayPause") { playing ->
                            Icon(
                                if (playing) Icons.Default.Pause else Icons.Default.PlayArrow, 
                                null, modifier = Modifier.size(64.dp), tint = Color.Black
                            )
                        }
                    }
                }

                Icon(
                    Icons.Default.SkipNext, null, 
                    modifier = Modifier.size(48.dp).clickable { viewModel.skipToNext() }, 
                    tint = text
                )

                IconButton(onClick = { viewModel.toggleRepeat() }) {
                    Icon(
                        if(repeatMode == 1) Icons.Default.RepeatOne else Icons.Default.Repeat, 
                        null, 
                        tint = if(repeatMode != 0) MangaRed else text, 
                        modifier = Modifier.rotate(12f)
                    )
                }
            }
        }
    }
}

@Composable
fun NeoButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    size: Dp,
    backgroundColor: Color = Color.White,
    iconSize: Dp = 24.dp,
    borderWidth: Dp = 4.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clickable(onClick = onClick)
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(Color.Black, RoundedCornerShape(4.dp))
        )
        // Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor, RoundedCornerShape(4.dp))
                .border(borderWidth, Color.Black, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = Color.Black
            )
        }
    }
}
