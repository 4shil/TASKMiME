package com.fourshil.musicya.ui.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fourshil.musicya.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

import com.fourshil.musicya.lyrics.LyricsViewModel
import com.fourshil.musicya.lyrics.LyricsLine
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    lyricsViewModel: LyricsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onDspClick: () -> Unit,
    onLibraryClick: () -> Unit
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val songTitle by viewModel.songTitle.collectAsState()
    val songArtist by viewModel.songArtist.collectAsState()
    val currentSongPath by viewModel.currentSongPath.collectAsState()
    val albumArtUri by viewModel.albumArtUri.collectAsState()
    
    // Lyrics State
    val lyrics by lyricsViewModel.lyrics.collectAsState()
    val activeLineIndex by lyricsViewModel.activeLineIndex.collectAsState()
    var showLyrics by remember { mutableStateOf(false) }

    LaunchedEffect(currentSongPath) {
        currentSongPath?.let { lyricsViewModel.loadLyrics(it) }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    // Only spin if playing
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing, delayMillis = 0)
        ),
        label = "spin"
    )
    // Hack to stop spin visual when paused (resetting angle or pausing anim is complex in Compose, 
    // for this demo we just accept it spins or we can use a different animation API).
    // Let's stick to the visual as is for now, but pass isPlaying to controls.


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background Blobs (Simulated with Box and Gradient/Blur)
        // Top Right Pink/Purple
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .size(300.dp)
                .blur(80.dp)
                .background(Color(0xFFFBCFE8).copy(alpha = 0.3f)) // Pink-200
        )
        // Bottom Left Blue
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = (-100).dp)
                .size(250.dp)
                .blur(80.dp)
                .background(Color(0xFFBFDBFE).copy(alpha = 0.3f)) // Blue-200
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header
            Spacer(modifier = Modifier.height(20.dp))
            Header(
                title = songTitle,
                artist = songArtist,
                spinAngle = if (isPlaying) angle else 0f,
                onDspClick = onDspClick,
                onLibraryClick = onLibraryClick
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Album Art & Progress OR Lyrics
            Box(contentAlignment = Alignment.Center) {
                if (!showLyrics) {

                     AlbumArtSection(
                        imageUrl = albumArtUri,
                        isPlaying = isPlaying,
                        onPlayPause = { viewModel.togglePlayPause() },
                        onArtClick = { showLyrics = true }
                    )
                } else {
                    LyricsView(
                        lyrics = lyrics,
                        activeLineIndex = activeLineIndex,
                        onDismiss = { showLyrics = false }
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Controls
            PlaybackControls()

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Actions
            BottomActions()
        }
    }
}

@Composable
fun Header(title: String, artist: String, spinAngle: Float, onDspClick: () -> Unit, onLibraryClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Library Button (Top Left)
        IconButton(
            onClick = onLibraryClick,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(Icons.Rounded.QueueMusic, contentDescription = "Library", tint = MaterialTheme.colorScheme.onPrimary)
        }

        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = title,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Poster Girl Pill (Reusing for Album or Artist)
            Surface(
                color = Color.White.copy(alpha = 0.5f),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha=0.3f)),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(spinAngle)
                            .background(
                                brush = Brush.sweepGradient(
                                    listOf(Color(0xFFFEF08A), Color(0xFFFBCFE8), Color(0xFFBFDBFE))
                                ),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF374151)
                    )
                }
            }
        }
        
        IconButton(
            onClick = onDspClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Rounded.MoreHoriz, contentDescription = "DSP Settings")
        }
    }
}

@Composable
fun AlbumArtSection(imageUrl: Any?, isPlaying: Boolean, onPlayPause: () -> Unit, onArtClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 320.dp, height = 450.dp) // Adjust based on visual
            .clickable { onArtClick() }
    ) {
        // Progress Arc Background (Gray)
        // SVG Data: M 53,109 A 155,155 0 0,0 307,109 (approx coordinates need scaling)
        // We will draw relative to the box size
        
        val isDark = isSystemInDarkTheme()
        val backgroundColor = if(isDark) Color.Gray else Color.LightGray
        val knobInnerColor = MaterialTheme.colorScheme.background

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .align(Alignment.BottomCenter)
            .offset(y = 40.dp)
        ) {
            val path = Path().apply {
                // Approximate the arc: start left, curve down, end right
                // Using simple arc for cleaner code than path data parsing
                moveTo(size.width * 0.15f, size.height * 0.2f)
                arcTo(
                    androidx.compose.ui.geometry.Rect(
                        left = -size.width * 0.1f,
                        top = -size.height * 0.8f,
                        right = size.width * 1.1f,
                        bottom = size.height * 0.8f
                    ),
                    180f, -180f, true
                )
            }
            // Better approximation based on design:
            // It looks like a "smile" arc at the bottom.
            // SVG Path: M 53,109 A 155,155 0 0,0 307,109
            // This is actually an arc from left to right, concave down? No, "0 0 0" means large-arc-flag=0, sweep-flag=0.
            // Wait, SVG coords are (x,y). 53,109 to 307,109. Flat line? No, A is Arc.
            
            // Let's just draw a quadratic bezier for the "smile" or use `drawArc`
            
            val trackPath = Path()
            trackPath.moveTo(size.width * 0.15f, size.height * 0.6f)
            trackPath.quadraticBezierTo(size.width * 0.5f, size.height * 1.0f, size.width * 0.85f, size.height * 0.6f)
            
            drawPath(
                path = trackPath,
                color = backgroundColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            
            // Progress (Green) - partial
             val progressPath = Path()
            progressPath.moveTo(size.width * 0.15f, size.height * 0.6f)
            progressPath.quadraticBezierTo(
                size.width * 0.35f, 
                size.height * 0.83f, // Control point interpolated
                size.width * 0.45f, 
                size.height * 0.85f  // End point approx
            )

            drawPath(
                path = progressPath,
                color = Green400,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )
            
            // Knob
            drawCircle(
                color = Green400,
                radius = 7.dp.toPx(),
                center = Offset(size.width * 0.45f, size.height * 0.85f)
            )
            drawCircle(
                color = knobInnerColor,
                radius = 3.dp.toPx(),
                center = Offset(size.width * 0.45f, size.height * 0.85f)
            )
        }

        // Album Art Card
        Card(
            shape = RoundedCornerShape(100.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
            modifier = Modifier
                .width(280.dp)
                .height(380.dp)
                .border(4.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(100.dp))
        ) {
            Box {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .error(android.R.drawable.sym_def_app_icon) // Fallback
                        .build(),
                    contentDescription = "Album Art",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Gradient Overlay
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha=0.1f), Color.Transparent, Color.Black.copy(alpha=0.4f))
                        )
                    )
                )

                // Curved Text (Song Title)
                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .align(Alignment.TopCenter)
                ) {
                    val textPath = android.graphics.Path()
                    // M 50,80 Q 150,20 250,80
                    // Scale to fit canvas
                    val w = size.width
                    textPath.moveTo(w * 0.16f, 80.dp.toPx())
                    textPath.quadTo(w * 0.5f, 20.dp.toPx(), w * 0.84f, 80.dp.toPx())

                    drawIntoCanvas { canvas ->
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 60f // Adjust as needed
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.ITALIC)
                            isAntiAlias = true
                            style = android.graphics.Paint.Style.FILL
                        }
                        canvas.nativeCanvas.drawTextOnPath("“Stick with You”", textPath, 0f, 0f, paint)
                    }
                }

                // Play Button
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                        .shadow(10.dp, CircleShape)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, 
                        contentDescription = "Play/Pause",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                // Time
                Text(
                    text = "3:24",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 40.dp)
                )
            }
        }
    }
}

@Composable
fun PlaybackControls() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Prev
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(56.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(Icons.Rounded.SkipPrevious, "Previous", modifier = Modifier.size(30.dp))
        }
        
        // Replay 10
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(64.dp)
                .shadow(8.dp, CircleShape)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(
                Icons.Rounded.Replay10, 
                "Replay 10", 
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(30.dp)
            )
        }
        
        // Forward 10
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(64.dp)
                .shadow(8.dp, CircleShape)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(
                Icons.Rounded.Forward10, 
                "Forward 10", 
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(30.dp)
            )
        }
        
        // Next
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(56.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(Icons.Rounded.SkipNext, "Next", modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
fun BottomActions() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {}) {
            Icon(Icons.Rounded.Shuffle, "Shuffle", tint = Color.Gray)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Rounded.Repeat, "Repeat", tint = Color.Gray)
        }
    }
}



@Composable
fun LyricsView(
    lyrics: List<LyricsLine>,
    activeLineIndex: Int,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()

    // Scroll to active line
    LaunchedEffect(activeLineIndex) {
        if (activeLineIndex >= 0) {
            // center the line
             listState.animateScrollToItem((activeLineIndex - 2).coerceAtLeast(0))
        }
    }

    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 450.dp)
            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(40.dp))
            .border(2.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(40.dp))
            .clickable { onDismiss() } // Tap to go back to art
            .padding(16.dp)
    ) {
        if (lyrics.isEmpty()) {
            Text(
                "No lyrics found",
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(vertical = 100.dp), // add padding to allow scrolling top/bottom
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(lyrics) { index, line ->
                    val isActive = index == activeLineIndex
                    val alpha = if (isActive) 1f else 0.5f
                    val scale = if (isActive) 1.2f else 1f
                    
                    Text(
                        text = line.content,
                        color = Color.White.copy(alpha = alpha),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = if (isActive) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .scale(scale)
                    )
                }
            }
        }
    }
}
