package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp

@Composable
fun HalftoneBackground(
    color: Color = MaterialTheme.colorScheme.onBackground,
    dotSize: Float = 2f,
    spacing: Float = 12f,
    modifier: Modifier = Modifier
) {
    val paintColor = color.copy(alpha = 0.15f)
    val density = androidx.compose.ui.platform.LocalDensity.current

    // Cache the brush so we don't recreate the bitmap on every recomposition unless parameters change
    val brush = remember(color, dotSize, spacing, density) {
        // Create a small pattern bitmap (e.g. 12x12)
        val sizePx = with(density) { spacing.dp.toPx() }.toInt().coerceAtLeast(1)
        val dotPx = with(density) { dotSize.dp.toPx() }
        
        val bitmap = ImageBitmap(sizePx, sizePx)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            this.color = paintColor
            isAntiAlias = true
        }
        
        // Draw one dot in the center or corner to be tiled
        canvas.drawCircle(Offset(sizePx / 2f, sizePx / 2f), dotPx / 2f, paint)
        
        ShaderBrush(
            androidx.compose.ui.graphics.ImageShader(
                bitmap, 
                TileMode.Repeated, 
                TileMode.Repeated
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush)
    )
}
