package com.fourshil.musicya.ui.nowplaying

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import com.fourshil.musicya.ui.components.MarqueeText
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fourshil.musicya.ui.components.*
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.MangaYellow
import com.fourshil.musicya.ui.theme.PureBlack
import com.fourshil.musicya.ui.theme.PureWhite

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
    val text = if (isDark) PureWhite else PureBlack

    // Disc Rotation
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        )
    )
    val currentRotation = if (isPlaying) rotation else 0f // In a real app we'd pause the value

    var isSeeking by remember { mutableStateOf(false) }
    var localProgress by remember { mutableFloatStateOf(0f) }
    
    val currentProgress = animateFloatAsState(
        targetValue = if (isSeeking) localProgress else if (duration > 0) position.toFloat() / duration else 0f,
        animationSpec = if (isSeeking) snap() else tween(durationMillis = 500),
        label = "progress"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Shared Halftone Background
       // HalftoneBackground(modifier = Modifier.alpha(0.1f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
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
                ArtisticButton(
                    onClick = onBack,
                    icon = { Icon(Icons.Default.ArrowBack, null, tint = PureBlack) },
                    modifier = Modifier.size(56.dp)
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "ACTIVE CANVAS",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                        color = text.copy(0.4f)
                    )
                    Text(
                        "NOW PLAYING",
                        style = MaterialTheme.typography.titleLarge,
                        color = text
                    )
                }

                ArtisticButton(
                    onClick = onQueueClick,
                    icon = { Icon(Icons.Default.QueueMusic, null, tint = PureBlack) },
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // Artwork
            Box(contentAlignment = Alignment.Center) {
                // Large Background Disc Decoration
                Icon(
                    Icons.Default.DiscFull, null,
                    modifier = Modifier
                        .size(320.dp)
                        .alpha(0.05f)
                        .rotate(currentRotation),
                    tint = text
                )

                // RAW TAG
                Box(
                    modifier = Modifier
                        .offset(x = 130.dp, y = (-120).dp)
                        .rotate(12f)
                        .zIndex(2f)
                        .background(MangaRed)
                        .border(3.dp, PureBlack)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("RAW", color = PureWhite, fontWeight = FontWeight.Black)
                }

                // Main Capsule / Card
                ArtisticCard(
                    modifier = Modifier.size(280.dp),
                    onClick = null
                ) {
                    Box {
                         // Overlay Speed lines?
                         // Built-in Halftone is already there from ArtisticCard

                        AsyncImage(
                            model = currentSong?.albumArtUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().alpha(0.9f)
                        )
                        
                        // Manga SFX
                        MangaFX(
                            text = "VIBE!",
                            color = MangaRed,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // Title & Artist
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MarqueeText(
                    text = currentSong?.title ?: "UNTITLED",
                    style = MaterialTheme.typography.displayMedium.copy(
                         fontSize = 36.sp,
                         lineHeight = 40.sp,
                         textAlign = TextAlign.Center
                    ),
                    color = text,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .rotate(-2f)
                        .background(MangaYellow)
                        .border(4.dp, PureBlack)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = currentSong?.artist ?: "UNKNOWN",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                        color = PureBlack
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // Seeker
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("RENDERING...", style = MaterialTheme.typography.labelSmall, color = text.copy(0.6f))
                    Text("MASTER-EDIT", style = MaterialTheme.typography.labelSmall, color = MangaRed, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                // Manga Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(5.dp, PureBlack)
                        .background(PureWhite)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val p = (offset.x / size.width).coerceIn(0f, 1f)
                                viewModel.seekTo((p * duration).toLong())
                            }
                        }
                ) {
                     // Halftone background inside?
                    HalftoneBackground(modifier = Modifier.alpha(0.1f))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(currentProgress.value)
                            .background(PureBlack)
                    ) {
                        // Progress head
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(12.dp)
                                .fillMaxHeight()
                                .background(MangaRed)
                                .border(3.dp, PureBlack)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 ArtisticButton(
                    onClick = { viewModel.toggleShuffle() },
                    icon = { Icon(Icons.Default.Shuffle, null, tint = if(shuffleEnabled) MangaRed else PureBlack) },
                    modifier = Modifier.size(56.dp).rotate(-8f)
                 )

                Icon(
                    Icons.Default.SkipPrevious, null, 
                    modifier = Modifier.size(56.dp).clickable { viewModel.skipToPrevious() }, 
                    tint = text
                )
                
                // Play Button
                ArtisticButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier.size(100.dp),
                    backgroundColor = PureWhite,
                    icon = { 
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, 
                            null, 
                            modifier = Modifier.size(48.dp),
                            tint = PureBlack
                        )
                    }
                )

                Icon(
                    Icons.Default.SkipNext, null, 
                    modifier = Modifier.size(56.dp).clickable { viewModel.skipToNext() }, 
                    tint = text
                )

                ArtisticButton(
                    onClick = { viewModel.toggleRepeat() },
                    icon = { Icon(Icons.Default.Repeat, null, tint = if(repeatMode!=0) MangaRed else PureBlack) },
                    modifier = Modifier.size(56.dp).rotate(8f)
                 )
            }
        }
    }
}
