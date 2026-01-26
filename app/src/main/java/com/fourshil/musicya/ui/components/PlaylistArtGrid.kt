package com.fourshil.musicya.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * A composite artwork grid for playlists.
 * Shows a grid of up to 4 album arts from the songs in the playlist.
 */
@Composable
fun PlaylistArtGrid(
    uris: List<Uri>,
    size: Dp = 48.dp,
    isScrolling: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(size),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (uris.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.QueueMusic,
                    contentDescription = null,
                    modifier = Modifier.size(size / 2),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val context = LocalContext.current
            val distinctUris = uris.distinct().take(4)
            
            when (distinctUris.size) {
                1 -> {
                    ArtImage(uri = distinctUris[0], isScrolling = isScrolling, modifier = Modifier.fillMaxSize())
                }
                2 -> {
                    Row(Modifier.fillMaxSize()) {
                        ArtImage(uri = distinctUris[0], isScrolling = isScrolling, modifier = Modifier.weight(1f).fillMaxHeight())
                        ArtImage(uri = distinctUris[1], isScrolling = isScrolling, modifier = Modifier.weight(1f).fillMaxHeight())
                    }
                }
                3 -> {
                    Row(Modifier.fillMaxSize()) {
                        ArtImage(uri = distinctUris[0], isScrolling = isScrolling, modifier = Modifier.weight(1f).fillMaxHeight())
                        Column(Modifier.weight(1f).fillMaxHeight()) {
                            ArtImage(uri = distinctUris[1], isScrolling = isScrolling, modifier = Modifier.weight(1f).fillMaxWidth())
                            ArtImage(uri = distinctUris[2], isScrolling = isScrolling, modifier = Modifier.weight(1f).fillMaxWidth())
                        }
                    }
                }
                else -> { // 4 or more
                    Column(Modifier.fillMaxSize()) {
                        Row(Modifier.weight(1f)) {
                            ArtImage(uri = distinctUris[0], isScrolling = isScrolling, modifier = Modifier.weight(1f).fillMaxHeight())
                            ArtImage(uri = distinctUris[1], isScrolling = isScrolling, modifier = Modifier.weight(1f).fillMaxHeight())
                        }
                        Row(Modifier.weight(1f)) {
                            ArtImage(uri = distinctUris[2], isScrolling = isScrolling, modifier = Modifier.weight(1f).fillMaxHeight())
                            ArtImage(uri = distinctUris[3], isScrolling = isScrolling, modifier = Modifier.weight(1f).fillMaxHeight())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtImage(uri: Uri, isScrolling: Boolean = false, modifier: Modifier = Modifier) {
    val builder = ImageRequest.Builder(LocalContext.current)
        .data(uri)
        .crossfade(false)
    
    if (isScrolling) {
        builder.diskCachePolicy(coil.request.CachePolicy.DISABLED)
        builder.memoryCachePolicy(coil.request.CachePolicy.ENABLED)
    } else {
        builder.diskCachePolicy(coil.request.CachePolicy.ENABLED)
        builder.memoryCachePolicy(coil.request.CachePolicy.ENABLED)
    }
    
    val model = builder.build()

    AsyncImage(
        model = model,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
