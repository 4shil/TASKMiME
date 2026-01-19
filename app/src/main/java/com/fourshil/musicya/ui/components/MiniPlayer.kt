package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.NeoShadowLight
import com.fourshil.musicya.ui.theme.Slate700
import com.fourshil.musicya.ui.theme.Slate900
import com.fourshil.musicya.ui.theme.Slate50
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow

/**
 * Neo-Brutalism Mini Player
 * Compact playback controls with clean design and progress indicator
 */
@Composable
fun MiniPlayer(
    song: Song?,
    isPlaying: Boolean,
    progress: Float,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (song == null) return

    val cardBg = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface
    val borderColor = MaterialTheme.colorScheme.outline
    val shadowColor = NeoShadowLight

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(NeoDimens.MiniPlayerHeight + NeoDimens.ShadowMedium)
            .clickable(onClick = onClick)
    ) {
        // Shadow layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(NeoDimens.MiniPlayerHeight)
                .align(Alignment.BottomCenter)
                .offset(x = NeoDimens.ShadowMedium, y = NeoDimens.ShadowMedium)
                .background(shadowColor)
        )

        // Main Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(NeoDimens.MiniPlayerHeight)
                .align(Alignment.BottomCenter)
                .border(NeoDimens.BorderThin, borderColor)
                .background(cardBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = NeoDimens.SpacingL, end = NeoDimens.SpacingM, top = NeoDimens.SpacingM, bottom = NeoDimens.SpacingM),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album Art Box
                Box(
                    modifier = Modifier
                        .size(NeoDimens.AlbumArtSmall)
                        .border(NeoDimens.BorderThin, borderColor)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    AlbumArtImage(
                        uri = song.albumArtUri,
                        size = NeoDimens.AlbumArtSmall
                    )
                }

                Spacer(modifier = Modifier.width(NeoDimens.SpacingL))

                // Track Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.25).sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor.copy(alpha = 0.6f)
                    )
                }
                
                // Play/Pause Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .border(NeoDimens.BorderThin, borderColor)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onPlayPauseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(NeoDimens.IconMedium)
                    )
                }
            }
            
            // Progress Bar at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .background(NeoCoral)
                )
            }
        }
    }
}

