package com.fourshil.musicya.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.theme.*

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
    val swipeThreshold = with(density) { 60.dp.toPx() }
    
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var swipeDirection by remember { mutableIntStateOf(0) }

    // Neo-Brutalist Colors
    val backgroundColor = MaterialTheme.colorScheme.surface
    val borderCol = MaterialTheme.colorScheme.outline
    
    NeoCard(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
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
                    onDragCancel = { dragOffsetX = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        dragOffsetX = (dragOffsetX + dragAmount).coerceIn(-swipeThreshold * 2f, swipeThreshold * 2f)
                    }
                )
            },
        backgroundColor = backgroundColor,
        borderColor = borderCol,
        borderWidth = 2.dp,
        shadowSize = 0.dp,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column {
            // Hard-edged Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.onSurface)
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
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Song info area
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = song,
                        transitionSpec = {
                            val direction = swipeDirection
                            swipeDirection = 0
                            if (direction < 0) {
                                (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                                        (slideOutHorizontally { width -> -width } + fadeOut())
                            } else if (direction > 0) {
                                (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                                        (slideOutHorizontally { width -> width } + fadeOut())
                            } else {
                                (fadeIn()) togetherWith (fadeOut())
                            } using SizeTransform(clip = false)
                        },
                        label = "songTransition"
                    ) { currentSong ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Album Art - Neo Card wrapped
                            NeoCard(
                                modifier = Modifier.size(48.dp),
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                borderWidth = 2.dp,
                                shadowSize = 0.dp,
                                shape = RoundedCornerShape(0.dp)
                            ) {
                                AlbumArtImage(
                                    uri = currentSong.albumArtUri,
                                    size = 48.dp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Track Info
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentSong.title.uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currentSong.artist,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Play/Pause Button - Neo Style
                NeoButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier.size(48.dp),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    borderWidth = 2.dp,
                    shadowSize = 2.dp,
                    shape = RoundedCornerShape(8.dp)
                ) {
                   Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Next Button
                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier
                        .size(40.dp)
                        .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
