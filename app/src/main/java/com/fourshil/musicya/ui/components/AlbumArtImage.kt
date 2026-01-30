package com.fourshil.musicya.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

/**
 * Reusable album art composable with proper loading and fallback states.
 * Shows a placeholder icon when no album art is available.
 * 
 * Performance optimizations:
 * - Size hints prevent unnecessary image scaling
 * - Hardware bitmaps for GPU acceleration
 * - Memory and disk caching enabled
 */
@Composable
fun AlbumArtImage(
    uri: Uri?,
    contentDescription: String = "Album Art",
    size: Dp = 48.dp,
    fallbackIcon: ImageVector = Icons.Default.MusicNote,
    isScrolling: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sizePx = with(LocalDensity.current) { size.roundToPx() }

    // State to track if we have successfully loaded the image
    var isLoaded by remember { mutableStateOf(false) }
    
    // We load if:
    // 1. We are NOT scrolling (allow full load)
    // 2. OR we are scrolling, but we ONLY check memory cache (fast)
    
    val builder = ImageRequest.Builder(context)
        .data(uri)
        .size(sizePx)
        .crossfade(150)
        .allowHardware(true)

    if (isScrolling) {
        // While scrolling: Only check memory. No disk/network.
        builder.diskCachePolicy(coil.request.CachePolicy.DISABLED)
        builder.memoryCachePolicy(coil.request.CachePolicy.ENABLED)
        // If it's not in memory, we don't want to start a decode job that blocks UI threads or simply creates object churn
        // However, Coil might still try to decode if we don't limit it. 
        // Ideally we want: "If in memory, show. Else, nothing/placeholder."
    } else {
        // Not scrolling: Full access
        builder.diskCachePolicy(coil.request.CachePolicy.ENABLED)
        builder.memoryCachePolicy(coil.request.CachePolicy.ENABLED)
    }

    val model = builder.build()

    Card(
        modifier = modifier.size(size),
        shape = MaterialTheme.shapes.small
    ) {
        SubcomposeAsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            onSuccess = { isLoaded = true },
            loading = {
                AlbumArtPlaceholder(icon = fallbackIcon)
            },
            error = {
                AlbumArtPlaceholder(icon = fallbackIcon)
            }
        )
    }
}

/**
 * Large album art for Now Playing screen
 */
@Composable
fun LargeAlbumArt(
    uri: Uri?,
    contentDescription: String = "Album Art",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(uri)
                .crossfade(true)
                .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
                LargeAlbumArtPlaceholder()
            },
            error = {
                LargeAlbumArtPlaceholder()
            }
        )
    }
}

@Composable
private fun AlbumArtPlaceholder(
    icon: ImageVector = Icons.Default.MusicNote
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun LargeAlbumArtPlaceholder() {
    DynamicAlbumArtPlaceholder(
        modifier = Modifier.fillMaxSize()
    )
}

/**
 * Artist image placeholder with person icon
 */
@Composable
fun ArtistImage(
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(size),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(size / 2),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
