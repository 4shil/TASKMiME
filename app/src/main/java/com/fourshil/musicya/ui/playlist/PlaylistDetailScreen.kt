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
import com.fourshil.musicya.ui.theme.NeoDimens

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
            modifier = Modifier.padding(horizontal = NeoDimens.ScreenPadding, vertical = NeoDimens.SpacingL),
            verticalAlignment = Alignment.CenterVertically
        ) {
             ArtisticButton(
                onClick = onBack,
                icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.size(NeoDimens.ButtonHeightSmall) // 48.dp matches ButtonHeightSmall? Or Medium? Main Headers use Medium. Let's use Medium for consistent hit target. 
                // Ah, previous code used 48.dp. Medium is usually ~52-56. Let's stick to NeoDimens.ButtonHeightMedium (52.dp usually) for consistency.
            )
             Spacer(modifier = Modifier.width(NeoDimens.SpacingL))
             Text("RETURN", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black), color = MaterialTheme.colorScheme.onBackground)
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = NeoDimens.ScreenPadding, end = NeoDimens.ScreenPadding, bottom = NeoDimens.ListBottomPadding)
            ) {
                // Large Header
                item {
                     Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = NeoDimens.SpacingXL),
                         contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             ArtisticCard(
                                onClick = null,
                                modifier = Modifier.size(200.dp) // Large art, keep 200 or add NeoDimens.ArtLarge? 200 is fine as singular hero.
                            ) {
                                LargeAlbumArt(
                                     uri = if (!artUri.isNullOrEmpty()) Uri.parse(artUri!!) else null,
                                    contentDescription = "",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(NeoDimens.SpacingL))
                            Text(
                                text = title.uppercase(),
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                                maxLines = 2,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                             Text(
                                text = subtitle.uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MangaRed
                            )
                            
                            Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))
                            
                            // Actions
                             Row(horizontalArrangement = Arrangement.spacedBy(NeoDimens.SpacingL)) {
                                 ArtisticButton(
                                    onClick = { viewModel.playAll() },
                                    text = "PLAY ALL",
                                    icon = { Icon(Icons.Default.PlayArrow, null) }
                                 )
                                  ArtisticButton(
                                    onClick = { viewModel.shufflePlay() },
                                    icon = { Icon(Icons.Default.Shuffle, null) },
                                    modifier = Modifier.size(NeoDimens.ButtonHeightMedium)
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
                     Spacer(modifier = Modifier.height(NeoDimens.SpacingL))
                }
            }
        }
    }
}
