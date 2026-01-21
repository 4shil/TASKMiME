package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Clean Minimalistic Song List Item
 * Simple, readable design with proper touch targets
 */
@Composable
fun SongListItem(
    song: Song,
    isCurrentlyPlaying: Boolean = false,
    isSelected: Boolean = false,
    inSelectionMode: Boolean = false,
    showTrackNumber: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        isCurrentlyPlaying -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        else -> Color.Transparent
    }

    val contentColor = when {
        isCurrentlyPlaying -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = NeoDimens.SpacingL, vertical = NeoDimens.SpacingXS),
        color = backgroundColor,
        shape = RoundedCornerShape(NeoDimens.CornerMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NeoDimens.SpacingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection checkbox or Album Art
            Box(
                modifier = Modifier
                    .size(NeoDimens.AlbumArtSmall)
                    .clip(RoundedCornerShape(NeoDimens.CornerSmall))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (inSelectionMode && isSelected) {
                    // Selection checkmark
                    Box(
                        modifier = Modifier
                            .size(NeoDimens.AlbumArtSmall)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(NeoDimens.IconMedium)
                        )
                    }
                } else if (showTrackNumber) {
                    // Track number
                    Text(
                        text = song.trackNumber?.toString() ?: "-",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    // Album art
                    AlbumArtImage(
                        uri = song.albumArtUri,
                        size = NeoDimens.AlbumArtSmall
                    )
                }
            }

            Spacer(modifier = Modifier.width(NeoDimens.SpacingM))

            // Song info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrentlyPlaying) FontWeight.SemiBold else FontWeight.Normal,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Duration
            Text(
                text = formatDuration(song.duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = NeoDimens.SpacingS)
            )

            // More button (only when not in selection mode)
            if (!inSelectionMode) {
                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier.size(NeoDimens.TouchTargetMin)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(NeoDimens.IconMedium)
                    )
                }
            }
        }
    }
}

/**
 * Format duration from milliseconds to MM:SS
 */
private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

/**
 * Simple Song Item variant for Queue and smaller lists
 */
@Composable
fun SimpleQueueItem(
    song: Song,
    isCurrentlyPlaying: Boolean = false,
    onClick: () -> Unit,
    onRemove: (() -> Unit)? = null
) {
    val contentColor = if (isCurrentlyPlaying) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = NeoDimens.SpacingL, vertical = NeoDimens.SpacingM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album art
        Box(
            modifier = Modifier
                .size(NeoDimens.AlbumArtSmall)
                .clip(RoundedCornerShape(NeoDimens.CornerSmall))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AlbumArtImage(
                uri = song.albumArtUri,
                size = NeoDimens.AlbumArtSmall
            )
        }

        Spacer(modifier = Modifier.width(NeoDimens.SpacingM))

        // Song info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (isCurrentlyPlaying) {
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrentlyPlaying) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor,
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
        if (onRemove != null) {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
