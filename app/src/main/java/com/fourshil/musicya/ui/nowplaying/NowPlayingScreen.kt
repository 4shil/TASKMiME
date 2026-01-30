package com.fourshil.musicya.ui.nowplaying

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
// import coil.request.Precision (Removed)
import com.fourshil.musicya.ui.components.LyricsBottomSheet
import com.fourshil.musicya.ui.components.NeoButton
import com.fourshil.musicya.ui.components.NeoProgressBar
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
    val highQualityArtUri by viewModel.highQualityArtUri.collectAsState()
    
    // Lyrics state
    var showLyrics by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
 
    // Colors from HTML spec
    val bgLight = NeoBackground
    val textDark = Color(0xFF171717) // Slate-900 like
    
    // Status bar padding is handled by system logic usually, but here we might want manual control if pure full screen
    // Assuming Scaffolding above handles basic insets or we use systemBarsPadding
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp), // px-8 equivalent roughly
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // 1. Top Bar
            // h-12 flex justify-between items-center pt-4 (We'll just add spacer top)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (Expand More)
                NeoButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    shadowSize = 2.dp
                ) {
                    // Use ExpandMore to match specific design request "Expand" button looks like "down" usually for sheet-like player
                    Icon(Icons.Default.ExpandMore, null, tint = MaterialTheme.colorScheme.onSurface)
                }
                
                Text(
                    text = "NOW PLAYING",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                // Menu Button (More Horiz)
                Box {
                    NeoButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        shadowSize = 2.dp
                    ) {
                        Icon(Icons.Default.MoreHoriz, null, tint = MaterialTheme.colorScheme.onSurface)
                    }

                    MaterialTheme(
                        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))
                    ) {
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Details", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    showMenu = false
                                    showDetails = true
                                },
                                leadingIcon = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.onSurface) }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp)) // gap-10 roughly
            
            // 2. Album Art
            // aspect-square w-full bg-accent-blue border-4 border-black rounded-[48px] shadow-neobrutal-lg
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                // Shadow (lg = 8px)
                // We keep shadow hard black or scrim color? Hard black for Neo feel, even in dark mode often preferred if bg isn't pure black.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(8.dp, 8.dp)
                        .background(Color.Black, RoundedCornerShape(48.dp))
                )
                
                // Main Card
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(48.dp),
                    color = MaterialTheme.colorScheme.primaryContainer, // Use themed container
                    border = androidx.compose.foundation.BorderStroke(4.dp, if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else Color.Black)
                ) {
                    // Swipe Gestures on Art
                     val density = androidx.compose.ui.platform.LocalDensity.current
                     val swipeThreshold = with(density) { 100.dp.toPx() }
                     var offsetX by remember { mutableFloatStateOf(0f) }
                     
                     // We can keep the simpler logic or re-integrate the rigid spring swipe if desired.
                     // For Neobrutal, rigid movements are fine.
                     
                        if (currentSong != null) {
                            // Use high-quality art URI from ViewModel, fallback to default
                            val artUri = highQualityArtUri ?: currentSong!!.albumArtUri
                            AsyncImage(
                                model = coil.request.ImageRequest.Builder(LocalContext.current)
                                    .data(artUri)
                                    .size(coil.size.Size.ORIGINAL) // Force original size
                                    .precision(coil.size.Precision.EXACT) // Require exact dimensions
                                    .crossfade(true)
                                    .build(),
                             contentDescription = "Album Art",
                             contentScale = ContentScale.Crop,
                             modifier = Modifier
                                 .fillMaxSize()
                                 .pointerInput(Unit) {
                                     detectHorizontalDragGestures(
                                         onDragEnd = {
                                             if (offsetX < -swipeThreshold) viewModel.skipToNext()
                                             else if (offsetX > swipeThreshold) viewModel.skipToPrevious()
                                             offsetX = 0f
                                         },
                                         onHorizontalDrag = { _, dragAmount ->
                                             offsetX += dragAmount
                                         }
                                     )
                                 }
                         )
                     }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // 3. Song Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentSong?.title ?: "No Song",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = currentSong?.artist ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Favorite Button
                NeoButton(
                    onClick = { viewModel.toggleFavorite() },
                    modifier = Modifier.size(56.dp), // w-14 h-14
                    shape = RoundedCornerShape(16.dp),
                    shadowSize = 2.dp
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        null,
                        // Tint needs to be visible against default button bg (Surface)
                        tint = if (isFavorite) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurface 
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 4. Progress
            Column(modifier = Modifier.fillMaxWidth()) {
                // Interactive Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp) // Hit area
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                 val fraction = offset.x / size.width
                                 val seekPos = (fraction * duration).toLong()
                                 viewModel.seekTo(seekPos)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    NeoProgressBar(
                        progress = if (duration > 0) position.toFloat() / duration else 0f,
                        height = 24.dp, // h-6
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatTime(position),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        formatTime(duration),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 5. Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // gap-2 in HTML, but space-between/around is safer for responsive
            ) {
                // Shuffle
                NeoButton(
                    onClick = { viewModel.toggleShuffle() },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    shadowSize = 2.dp,
                    borderWidth = 2.dp
                ) {
                    Icon(
                        Icons.Default.Shuffle, 
                        null, 
                        modifier = Modifier.size(24.dp),
                        tint = if (shuffleEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Prev
                NeoButton(
                    onClick = { viewModel.skipToPrevious() },
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = MaterialTheme.colorScheme.surface, // Clean
                    shadowSize = 4.dp
                ) {
                    Icon(
                        Icons.Default.SkipPrevious, 
                        null, 
                        modifier = Modifier.size(36.dp), 
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Play
                NeoButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier.size(96.dp), // w-24 h-24
                    shape = RoundedCornerShape(32.dp),
                    backgroundColor = MaterialTheme.colorScheme.primary, // Was NeoPrimary
                    shadowSize = 8.dp // neobrutal-lg
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(60.dp) // text-6xl
                    )
                }
                
                // Next
                NeoButton(
                    onClick = { viewModel.skipToNext() },
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = MaterialTheme.colorScheme.surface, // Clean
                    shadowSize = 4.dp
                ) {
                    Icon(
                        Icons.Default.SkipNext, 
                        null, 
                        modifier = Modifier.size(36.dp), 
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Repeat
                NeoButton(
                    onClick = { viewModel.toggleRepeat() },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    shadowSize = 2.dp
                ) {
                     Icon(
                         if (repeatMode == 1) Icons.Default.RepeatOne else Icons.Default.Repeat,
                         null, 
                         modifier = Modifier.size(24.dp),
                         tint = if (repeatMode != 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                     )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 6. Bottom Actions
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionItem(icon = Icons.Default.Lyrics, label = "LYRICS", onClick = { showLyrics = true })
                ActionItem(icon = Icons.Default.QueueMusic, label = "QUEUE", onClick = onQueueClick)
                
                 val context = LocalContext.current
                 ActionItem(
                     icon = Icons.Default.Share, 
                     label = "SHARE", 
                     onClick = {
                        if (currentSong != null) {
                            val sendIntent: android.content.Intent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, "Check out \"${currentSong!!.title}\" by ${currentSong!!.artist}")
                                type = "text/plain"
                            }
                            val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }
                     }
                 )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Bottom Indicator line (decorative)
            Box(
                modifier = Modifier
                    .width(128.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.2f))
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    
    // Lyrics Sheet
    if (showLyrics && currentSong != null) {
        LyricsBottomSheet(
            songPath = currentSong?.path ?: "",
            currentPositionMs = position,
            onDismiss = { showLyrics = false }
        )
    }
    
    // Song Details (Placeholder)
    if (showDetails && currentSong != null) {
        com.fourshil.musicya.ui.components.SongDetailsDialog(
            song = currentSong!!,
            onDismiss = { showDetails = false }
        )
    }
}



@Composable
fun ActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick, indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() })
    ) {
        NeoButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            shadowSize = 2.dp
        ) {
            Icon(icon, null, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        )
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
