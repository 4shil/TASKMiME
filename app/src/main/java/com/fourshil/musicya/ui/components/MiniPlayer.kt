package com.fourshil.musicya.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.theme.NeoDimens
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Clean Minimalistic Mini Player
 * Features swipe gestures for skip next/previous
 */
@Composable
fun MiniPlayer(
    song: Song?,
    isPlaying: Boolean,
    progress: Float,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit = {},
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (song == null) return

    val density = LocalDensity.current
    val swipeThreshold = with(density) { 80.dp.toPx() }
    
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "swipeOffset"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(NeoDimens.MiniPlayerHeight)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            offsetX < -swipeThreshold -> onNextClick()
                            offsetX > swipeThreshold -> onPreviousClick()
                        }
                        offsetX = 0f
                    },
                    onDragCancel = { offsetX = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceIn(-swipeThreshold * 1.5f, swipeThreshold * 1.5f)
                    }
                )
            }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(topStart = NeoDimens.CornerLarge, topEnd = NeoDimens.CornerLarge),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = NeoDimens.ElevationHigh,
        shadowElevation = NeoDimens.ElevationMedium
    ) {
        Column {
            // Progress bar at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            // Content with swipe offset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                    .graphicsLayer {
                        alpha = 1f - (abs(animatedOffsetX) / (swipeThreshold * 2))
                    }
                    .padding(horizontal = NeoDimens.SpacingL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album Art
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

                // Track Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(NeoDimens.SpacingS))

                // Play/Pause Button
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { onPlayPauseClick() },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(NeoDimens.IconMedium)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(NeoDimens.SpacingXS))

                // Next Button
                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(NeoDimens.IconMedium)
                    )
                }
            }
        }
    }
}
