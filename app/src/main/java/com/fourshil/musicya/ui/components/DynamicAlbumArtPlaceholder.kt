package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.ui.theme.NeoBlue
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.NeoGreen
import com.fourshil.musicya.ui.theme.NeoViolet

/**
 * Dynamic placeholder for album art that adapts to theme
 * Uses a gradient background for a more premium look
 */
@Composable
fun DynamicAlbumArtPlaceholder(
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    
    // Dynamic gradients based on theme
    val gradientColors = if (isDark) {
        listOf(
            NeoViolet.copy(alpha = 0.3f),
            NeoBlue.copy(alpha = 0.1f)
        )
    } else {
        listOf(
            NeoGreen.copy(alpha = 0.3f),
            NeoBlue.copy(alpha = 0.1f)
        )
    }
    
    val iconColor = if (isDark) NeoViolet else NeoGreen

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(colors = gradientColors)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(64.dp) // Large icon
        )
    }
}
