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
import com.fourshil.musicya.ui.theme.NeoDimens

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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Clean header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = NeoDimens.ScreenPadding, vertical = NeoDimens.SpacingL),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MinimalIconButton(
                    onClick = onBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Queue",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (queue.isNotEmpty()) {
                        Text(
                            text = "${currentIndex + 1} of ${queue.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Clear queue button
                MinimalIconButton(
                    onClick = { viewModel.clearQueue() },
                    icon = Icons.Default.ClearAll,
                    contentDescription = "Clear queue",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingM)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        "No songs in queue",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = NeoDimens.ScreenPadding,
                    end = NeoDimens.ScreenPadding,
                    top = 0.dp,
                    bottom = NeoDimens.ListBottomPadding
                ),
                verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingS)
            ) {
                // Played songs section header (if there are songs before current)
                if (currentIndex > 0) {
                    item(key = "played_header") {
                        Text(
                            text = "▲ Played (${currentIndex} songs)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = NeoDimens.SpacingS)
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
                        onPlay = { viewModel.playAt(index) },
                        onRemove = { viewModel.removeAt(index) }
                    )
                    
                    // Add "Up Next" label after current song
                    if (isPlaying && index < queue.size - 1) {
                        Text(
                            text = "▼ Up Next (${queue.size - currentIndex - 1} songs)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = NeoDimens.SpacingS)
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
    onPlay: () -> Unit,
    onRemove: () -> Unit
) {
    val backgroundColor = when {
        isPlaying -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        isPlayed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val contentAlpha = if (isPlayed) 0.6f else 1f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(contentAlpha)
            .clickable(onClick = onPlay),
        shape = RoundedCornerShape(NeoDimens.CornerMedium),
        color = backgroundColor,
        tonalElevation = when {
            isPlaying -> NeoDimens.ElevationHigh
            else -> NeoDimens.ElevationLow
        }
    ) {
        Row(
            modifier = Modifier.padding(NeoDimens.SpacingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art
            Box(
                modifier = Modifier
                    .size(NeoDimens.AlbumArtSmall)
                    .clip(RoundedCornerShape(NeoDimens.CornerSmall))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AlbumArtImage(uri = song.albumArtUri, size = NeoDimens.AlbumArtSmall)
                
                // Playing indicator overlay
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(NeoDimens.SpacingM))

            // Song info
            Column(modifier = Modifier.weight(1f)) {
                if (isPlaying) {
                    Text(
                        "NOW PLAYING",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isPlaying) FontWeight.SemiBold else FontWeight.Normal,
                    color = when {
                        isPlaying -> MaterialTheme.colorScheme.primary
                        isPlayed -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove from queue",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
