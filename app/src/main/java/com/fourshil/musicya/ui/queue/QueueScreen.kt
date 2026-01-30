package com.fourshil.musicya.ui.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.AlbumArtImage
import com.fourshil.musicya.ui.components.MinimalIconButton
import com.fourshil.musicya.ui.components.NeoEmptyState
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.components.NeoScaffold
import com.fourshil.musicya.ui.components.NeoButton
import com.fourshil.musicya.ui.components.NeoCard
import com.fourshil.musicya.ui.theme.NeoBackground
import com.fourshil.musicya.ui.theme.NeoPink
import com.fourshil.musicya.ui.theme.NeoBlue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border

/**
 * Queue Screen - Centered on current song
 * Swipe up to see played songs, swipe down to see queued songs
 */
@Composable
fun QueueScreen(
    viewModel: QueueViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val queue by viewModel.queue.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    
    val listState = rememberLazyListState()
    
    // Auto-scroll to current song when screen opens or current index changes
    LaunchedEffect(currentIndex, queue.size) {
        if (currentIndex >= 0 && queue.isNotEmpty()) {
            // Scroll to put current song roughly in center of screen
            listState.animateScrollToItem(
                index = currentIndex,
                scrollOffset = -200 // Offset to show some items above
            )
        }
    }

    NeoScaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Clean header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NeoButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    shadowSize = 4.dp
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "QUEUE",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 1.sp
                    )
                    if (queue.isNotEmpty()) {
                        Text(
                            text = "${currentIndex + 1} OF ${queue.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Clear queue button
                NeoButton(
                    onClick = { viewModel.clearQueue() },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    shadowSize = 4.dp,
                    backgroundColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        Icons.Default.ClearAll,
                        contentDescription = "Clear queue",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { paddingValues ->
        if (queue.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                 NeoEmptyState(
                    message = "NO SONGS IN QUEUE",
                    icon = Icons.AutoMirrored.Filled.QueueMusic
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 0.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Played songs section header (if there are songs before current)
                if (currentIndex > 0) {
                    item(key = "played_header") {
                        Text(
                            text = "▲ PLAYED (${currentIndex})",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
                
                itemsIndexed(
                    items = queue,
                    key = { index, song -> "${song.id}_$index" },
                    contentType = { _, _ -> "queue_item" }
                ) { index, song ->
                    val isPlaying = index == currentIndex
                    val isPlayed = index < currentIndex
                    
                    QueueItem(
                        song = song,
                        isPlaying = isPlaying,
                        isPlayed = isPlayed,
                        isScrolling = listState.isScrollInProgress,
                        onPlay = { viewModel.playAt(index) },
                        onRemove = { viewModel.removeAt(index) }
                    )
                    
                    // Add "Up Next" label after current song
                    if (isPlaying && index < queue.size - 1) {
                        Text(
                            text = "▼ UP NEXT (${queue.size - currentIndex - 1})",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Queue Item with visual distinction for played/playing/queued states
 */
@Composable
private fun QueueItem(
    song: Song,
    isPlaying: Boolean,
    isPlayed: Boolean,
    isScrolling: Boolean = false,
    onPlay: () -> Unit,
    onRemove: () -> Unit
) {
    val backgroundColor = when {
        isPlaying -> MaterialTheme.colorScheme.primaryContainer
        isPlayed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = MaterialTheme.colorScheme.outline
    val borderWidth = if (isPlaying) 2.dp else 1.dp
    
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onPlay,
        backgroundColor = backgroundColor,
        shadowSize = if (isPlaying) 4.dp else 2.dp,
        borderWidth = borderWidth,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AlbumArtImage(
                    uri = song.albumArtUri, 
                    size = 48.dp,
                    isScrolling = isScrolling
                )
                
                // Playing indicator overlay
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Song info
            Column(modifier = Modifier.weight(1f)) {
                if (isPlaying) {
                    Text(
                        "NOW PLAYING",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isPlaying) FontWeight.Black else FontWeight.Bold,
                    color = if (isPlayed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove from queue",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

