package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.NeoShadowLight
import com.fourshil.musicya.ui.theme.Slate50
import com.fourshil.musicya.ui.theme.Slate700

/**
 * Neo-Brutalism Song List Item
 * Clean card design with subtle selection state
 */
@Composable
fun SongListItem(
    song: Song,
    isFavorite: Boolean,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    // Selection uses accent color, normal uses theme outline
    val borderColor = if (isSelected) NeoCoral else MaterialTheme.colorScheme.outline
    val shadowColor = if (isSelected) NeoCoral else NeoShadowLight
    
    ArtisticCard(
        onClick = onClick,
        borderColor = borderColor,
        shadowColor = shadowColor,
        showHalftone = false,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NeoDimens.CardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .border(NeoDimens.BorderThin, MaterialTheme.colorScheme.outline)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (song.albumArtUri != null) {
                    AlbumArtImage(uri = song.albumArtUri, size = 56.dp)
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "No album art",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                MarqueeText(
                    text = song.title,
                    isActive = isSelected,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.25).sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Selection indicator or more button
            if (isSelectionMode) {
                // Optimize: Simple Box instead of heavy nesting
                 Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(NeoDimens.BorderThin, MaterialTheme.colorScheme.outline)
                        .background(if (isSelected) NeoCoral else MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            modifier = Modifier.size(16.dp),
                            tint = Slate50
                        )
                    }
                }
            } else {
                IconButton(onClick = onMoreClick) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

