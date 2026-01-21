package com.fourshil.musicya.ui.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * Clean Minimalistic Queue Screen
 */
@Composable
fun QueueScreen(
    viewModel: QueueViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val queue by viewModel.queue.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()

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
                
                Text(
                    text = "Queue",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
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
                        imageVector = Icons.Default.QueueMusic,
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
                itemsIndexed(
                    items = queue,
                    key = { index, song -> "${song.id}_$index" },
                    contentType = { _, _ -> "queue_item" }
                ) { index, song ->
                    QueueItem(
                        song = song,
                        isPlaying = index == currentIndex,
                        onPlay = { viewModel.playAt(index) },
                        onRemove = { viewModel.removeAt(index) }
                    )
                }
            }
        }
    }
}

/**
 * Clean Queue Item
 */
@Composable
private fun QueueItem(
    song: Song,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onRemove: () -> Unit
) {
    val backgroundColor = if (isPlaying) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay),
        shape = RoundedCornerShape(NeoDimens.CornerMedium),
        color = backgroundColor,
        tonalElevation = if (isPlaying) NeoDimens.ElevationMedium else NeoDimens.ElevationLow
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
            }

            Spacer(modifier = Modifier.width(NeoDimens.SpacingM))

            // Song info
            Column(modifier = Modifier.weight(1f)) {
                if (isPlaying) {
                    Text(
                        "Now Playing",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isPlaying) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
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
