package com.fourshil.musicya.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Clean Minimalistic Mini Player
 * Features swipe gestures for skip next/previous - only song info animates
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
    val swipeThreshold = with(density) { 60.dp.toPx() } // Reduced threshold for easier swiping
    
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var swipeDirection by remember { mutableIntStateOf(0) } // -1 = left (next), 1 = right (prev), 0 = none

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(NeoDimens.MiniPlayerHeight)
            .pointerInput(Unit) {
                // Handle horizontal swipe gestures on the entire surface
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            dragOffsetX < -swipeThreshold -> {
                                swipeDirection = -1
                                onNextClick()
                            }
                            dragOffsetX > swipeThreshold -> {
                                swipeDirection = 1
                                onPreviousClick()
                            }
                        }
                        dragOffsetX = 0f
                    },
                    onDragCancel = { 
                        dragOffsetX = 0f 
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        dragOffsetX = (dragOffsetX + dragAmount).coerceIn(
                            -swipeThreshold * 2f, 
                            swipeThreshold * 2f
                        )
                    }
                )
            }
            .pointerInput(Unit) {
                // Handle tap separately
                detectTapGestures(
                    onTap = { onClick() }
                )
            },
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

            // Content row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = NeoDimens.SpacingL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Song info area - animates on song change
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = song,
                        transitionSpec = {
                            val direction = swipeDirection
                            // Reset after animation
                            swipeDirection = 0
                            
                            if (direction < 0) {
                                // Swipe left = next song - slide from right
                                (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                                        (slideOutHorizontally { width -> -width } + fadeOut())
                            } else if (direction > 0) {
                                // Swipe right = previous song - slide from left
                                (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                                        (slideOutHorizontally { width -> width } + fadeOut())
                            } else {
                                // Default transition
                                (fadeIn()) togetherWith (fadeOut())
                            } using SizeTransform(clip = false)
                        },
                        label = "songTransition"
                    ) { currentSong ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Album Art
                            Box(
                                modifier = Modifier
                                    .size(NeoDimens.AlbumArtSmall)
                                    .clip(RoundedCornerShape(NeoDimens.CornerSmall))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                AlbumArtImage(
                                    uri = currentSong.albumArtUri,
                                    size = NeoDimens.AlbumArtSmall
                                )
                            }

                            Spacer(modifier = Modifier.width(NeoDimens.SpacingM))

                            // Track Info
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentSong.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currentSong.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(NeoDimens.SpacingS))

                // Play/Pause Button - No selection indication
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(NeoDimens.IconMedium)
                            )
                        }
                    }
                }

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
