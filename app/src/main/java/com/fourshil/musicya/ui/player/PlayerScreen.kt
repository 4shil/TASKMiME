package com.fourshil.musicya.ui.player

import android.graphics.Path
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
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

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onDspClick: () -> Unit // New callback
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
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
                spinAngle = if (isPlaying) angle else 0f,
                onDspClick = onDspClick
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Album Art & Progress
            AlbumArtSection(
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBYCjzUgIFJqFjVlj4TnXS_gOWIKRc81l8dpjLROTYr3y90inPv45I9Ma9RNIHASetaIXYYwM3ZNQ7_FMICWCfDuqe5JONiWxk-udGCVqtNuCfwq1lFsbH4BALVeuZmAtXC_iZL8_zAwEzq6N-nebFLHNa_zfe3LBaKxKUnu0oCNt_lXuLR9Oc7JouD-UHd1XzznjZO1zpBpVARM1A9FZalf6XeFY8qX-2g8t-wWKJQy0THQtP8Oh7carDEeLjQ1s_lY_mmEKNaDJj2",
                isPlaying = isPlaying,
                onPlayPause = { viewModel.togglePlayPause() }
            )

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
fun Header(spinAngle: Float, onDspClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "Zara\nLarsson",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Poster Girl Pill
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
                        text = "Poster Girl",
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
fun AlbumArtSection(imageUrl: String, isPlaying: Boolean, onPlayPause: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(width = 320.dp, height = 450.dp) // Adjust based on visual
    ) {
        // Progress Arc Background (Gray)
        // SVG Data: M 53,109 A 155,155 0 0,0 307,109 (approx coordinates need scaling)
        // We will draw relative to the box size
        
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
            trackPath.quadTo(size.width * 0.5f, size.height * 1.0f, size.width * 0.85f, size.height * 0.6f)
            
            drawPath(
                path = trackPath.asComposePath(),
                color = if(isSystemInDarkTheme()) Color.Gray else Color.LightGray,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            
            // Progress (Green) - partial
             val progressPath = Path()
            progressPath.moveTo(size.width * 0.15f, size.height * 0.6f)
            progressPath.quadTo(
                size.width * 0.35f, 
                size.height * 0.83f, // Control point interpolated
                size.width * 0.45f, 
                size.height * 0.85f  // End point approx
            )

            drawPath(
                path = progressPath.asComposePath(),
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
                color = MaterialTheme.colorScheme.background,
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
                    val textPath = Path()
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
                            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
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

// Extensions
fun Path.asComposePath(): androidx.compose.ui.graphics.Path {
    return androidx.compose.ui.graphics.Path().apply {
        addPath(this@asComposePath)
    }
}
