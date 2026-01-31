package com.fourshil.musicya.ui.playlist

import android.net.Uri
import androidx.compose.ui.graphics.Color
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
import com.fourshil.musicya.ui.components.NeoButton
import com.fourshil.musicya.ui.components.NeoCard
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
             NeoButton(
                onClick = onBack,
                modifier = Modifier.size(52.dp),
                borderWidth = NeoDimens.BorderBold,
                shadowSize = NeoDimens.ShadowDefault
            ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back", tint = MaterialTheme.colorScheme.onSurface) }
             Spacer(modifier = Modifier.width(NeoDimens.SpacingL))
             Text("Back", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onBackground)
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
                // Song list
                val listState = androidx.compose.foundation.lazy.rememberLazyListState()
                val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
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
                                 NeoCard(
                                    modifier = Modifier.size(200.dp),
                                    borderWidth = NeoDimens.BorderBold,
                                    shadowSize = NeoDimens.ShadowProminent
                                ) {
                                    LargeAlbumArt(
                                         uri = if (!artUri.isNullOrEmpty()) Uri.parse(artUri!!) else null,
                                        contentDescription = "",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(NeoDimens.SpacingL))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 2,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                 Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                
                                Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))
                                
                                // Actions
                                 Row(horizontalArrangement = Arrangement.spacedBy(NeoDimens.SpacingL)) {
                                     NeoButton(
                                        onClick = { viewModel.playAll() },
                                        backgroundColor = MaterialTheme.colorScheme.primary,
                                        borderWidth = NeoDimens.BorderBold,
                                        shadowSize = NeoDimens.ShadowDefault
                                     ) {
                                         Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                             Icon(Icons.Default.PlayArrow, contentDescription = "Play all songs", tint = MaterialTheme.colorScheme.onPrimary)
                                             Spacer(modifier = Modifier.width(8.dp))
                                             Text("Play All", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium)
                                         }
                                     }
                                      NeoButton(
                                        onClick = { viewModel.shufflePlay() },
                                        modifier = Modifier.size(52.dp),
                                        borderWidth = NeoDimens.BorderBold,
                                         shadowSize = NeoDimens.ShadowDefault
                                     ) {
                                         Icon(Icons.Default.Shuffle, contentDescription = "Shuffle play", tint = MaterialTheme.colorScheme.onSurface)
                                     }
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
                            isScrolling = isScrolling,
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
