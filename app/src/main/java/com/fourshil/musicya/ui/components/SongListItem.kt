package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.theme.NeoBlue
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.NeoGreen
import com.fourshil.musicya.ui.theme.NeoPink
import androidx.compose.ui.unit.sp

/**
 * Soft Neo-Brutalist Song List Item
 * Proper touch targets, accessible labels, softer typography
 */
@OptIn(ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun SongListItem(
    song: Song,
    isCurrentlyPlaying: Boolean = false,
    isFavorite: Boolean = false,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    inSelectionMode: Boolean = false,
    showTrackNumber: Boolean = false,
    isScrolling: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    val selectionActive = isSelectionMode || inSelectionMode
    
    // Determine background color based on state
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.secondary
        isCurrentlyPlaying -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surface
    }

    NeoCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        backgroundColor = backgroundColor,
        shadowSize = NeoDimens.ShadowSubtle,
        borderWidth = NeoDimens.BorderDefault,
        onClick = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album Art Container
                Box(
                    modifier = Modifier
                        .size(NeoDimens.AlbumArtSmall)
                        .clip(RoundedCornerShape(NeoDimens.CornerSmall))
                        .border(NeoDimens.BorderDefault, MaterialTheme.colorScheme.outline, RoundedCornerShape(NeoDimens.CornerSmall))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectionActive && isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Song selected",
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    } else {
                        AlbumArtImage(
                            uri = song.albumArtUri,
                            size = NeoDimens.AlbumArtSmall,
                            isScrolling = isScrolling
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Song info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Favorite indicator
                if (isFavorite && !selectionActive) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite song",
                        tint = NeoPink,
                        modifier = Modifier
                            .size(NeoDimens.IconSmall)
                            .padding(end = 4.dp)
                    )
                }

                // Duration
                Text(
                    text = song.durationFormatted,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // More button - Proper touch target
                if (!selectionActive) {
                    IconButton(
                        onClick = onMoreClick,
                        modifier = Modifier.size(NeoDimens.TouchTargetMin)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options for ${song.title}",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
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
    val backgroundColor = if (isCurrentlyPlaying) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album art
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AlbumArtImage(
                uri = song.albumArtUri,
                size = 40.dp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Song info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (isCurrentlyPlaying) {
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.sp
                )
            }
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Remove button
        if (onRemove != null) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(NeoDimens.TouchTargetMin)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Remove from queue",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
