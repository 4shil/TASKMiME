package com.fourshil.musicya.ui.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.components.PlaylistArtGrid
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.MangaYellow
import com.fourshil.musicya.ui.theme.PureBlack

@Composable
fun QueueScreen(
    viewModel: QueueViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val queue by viewModel.queue.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
             ArtisticButton(
                onClick = onBack,
                icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = PureBlack) },
                modifier = Modifier.size(56.dp)
            )
             ArtisticButton(
                onClick = { viewModel.clearQueue() },
                icon = { Icon(Icons.Default.ClearAll, null, tint = PureBlack) },
                modifier = Modifier.size(56.dp),
                backgroundColor = MangaRed
            )
        }
        
        Text(
            text = "QUEUE",
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = MangaYellow
            ),
             modifier = Modifier.padding(top = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (queue.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                 Text(
                     "STANDBY...", 
                     style = MaterialTheme.typography.headlineLarge,
                     color = PureBlack.copy(0.3f)
                 )
            }
        } else {
             LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 160.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(
                    items = queue,
                    key = { index, song -> "${song.id}_$index" },
                    contentType = { _, _ -> "queue_item" }
                ) { index, song ->
                    QueueArtisticItem(
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

@Composable
fun QueueArtisticItem(
    song: Song,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onRemove: () -> Unit
) {
    ArtisticCard(
        onClick = if (!isPlaying) onPlay else null,
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isPlaying) MangaRed else PureBlack,
        backgroundColor = if (isPlaying) MangaRed.copy(alpha = 0.1f) else Color.White,
        showHalftone = false // Performance optimization
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).border(2.dp, PureBlack).background(PureBlack)
            ) {
                PlaylistArtGrid(uris = listOf(song.albumArtUri), size = 48.dp)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                 if (isPlaying) {
                     Text("NOW PLAYING", style = MaterialTheme.typography.labelSmall, color = MangaRed, fontWeight = FontWeight.Bold)
                 }
                 Text(
                    text = song.title.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    maxLines = 1
                 )
                 Text(
                    text = song.artist.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = PureBlack.copy(alpha=0.6f)
                 )
            }
            
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, null, tint = PureBlack)
            }
        }
    }
}
