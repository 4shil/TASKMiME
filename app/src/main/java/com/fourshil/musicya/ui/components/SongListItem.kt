package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.MoreVert
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
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.components.MarqueeText
import com.fourshil.musicya.ui.theme.PureBlack

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
    // We treat the "Selected" state as the "Active/Playing" style from the React model?
    // Or just a highlight. For now, let's use the ArtisticCard style.
    
    val borderColor = if (isSelected) MangaRed else PureBlack
    val shadowColor = if (isSelected) MangaRed else PureBlack
    
    ArtisticCard(
        onClick = onClick,
        borderColor = borderColor,
        shadowColor = shadowColor,
        showHalftone = false, // Performance optimization
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon / Art
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .border(3.dp, PureBlack)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
               if (song.albumArtUri != null) {
                   AlbumArtImage(uri = song.albumArtUri, size = 56.dp)
               } else {
                   Icon(
                       imageVector = Icons.Default.Bolt,
                       contentDescription = null,
                       modifier = Modifier.size(32.dp),
                       tint = PureBlack
                   )
               }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                MarqueeText(
                    text = song.title.uppercase(),
                    isActive = isSelected,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        letterSpacing = (-1).sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = song.artist.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                     maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alpha(0.6f)
                )
            }
            
            // Selection Check or Duration/More
            if (isSelectionMode) {
                 Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(2.dp, PureBlack)
                        .background(if(isSelected) MangaRed else MaterialTheme.colorScheme.surface)
                )
            } else {
                IconButton(onClick = onMoreClick) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = PureBlack)
                }
            }
        }
    }
}
