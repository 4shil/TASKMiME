package com.fourshil.musicya.ui.playlist

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.components.LargeAlbumArt
import com.fourshil.musicya.ui.components.SongListItem
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack

@Composable
fun PlaylistDetailScreen(
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val songs by viewModel.songs.collectAsState()
    val title by viewModel.title.collectAsState()
    val subtitle by viewModel.subtitle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val artUri by viewModel.artUri.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Simple Back Header
         Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             ArtisticButton(
                onClick = onBack,
                icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = PureBlack) },
                modifier = Modifier.size(48.dp)
            )
             Spacer(modifier = Modifier.width(16.dp))
             Text("RETURN", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black))
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PureBlack)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 160.dp)
            ) {
                // Large Header
                item {
                     Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                         contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             ArtisticCard(
                                onClick = null,
                                modifier = Modifier.size(200.dp)
                            ) {
                                LargeAlbumArt(
                                     uri = if (artUri != null) Uri.parse(artUri) else null,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = title.uppercase(),
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                                maxLines = 2,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                             Text(
                                text = subtitle.uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MangaRed
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Actions
                             Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                 ArtisticButton(
                                    onClick = { viewModel.playAll() },
                                    text = "PLAY ALL",
                                    icon = { Icon(Icons.Default.PlayArrow, null) }
                                 )
                                  ArtisticButton(
                                    onClick = { viewModel.shufflePlay() },
                                    icon = { Icon(Icons.Default.Shuffle, null) },
                                    modifier = Modifier.size(56.dp)
                                 )
                             }
                        }
                    }
                }

                // Song list
                itemsIndexed(songs, key = { index, song -> "${song.id}_$index" }) { index, song ->
                     SongListItem(
                        song = song,
                        isFavorite = false,
                        isSelected = false,
                        isSelectionMode = false,
                        onClick = { viewModel.playSongAt(index) },
                        onLongClick = {},
                        onMoreClick = {} // Reduced complexity for detail view
                     )
                     Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
